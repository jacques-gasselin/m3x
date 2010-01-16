/**
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import m3x.m3g.primitives.Header;
import m3x.m3g.primitives.Serializable;
import m3x.m3g.primitives.FileIdentifier;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.Adler32;
import m3x.m3g.primitives.Section;
import m3x.m3g.primitives.SectionSerializable;
import m3x.util.LittleEndianDataOutputStream;

/**
 *
 * @author jgasseli
 */
public class Serializer implements DataOutput
{
    private String version;
    private String author;
    private Stack<LittleEndianDataOutputStream> outputStreams;
    private Map<Object, Integer> objectToIndexMap;
    
    public Serializer(String version, String author)
    {
        this.version = version;
        this.author = author;

        outputStreams = new Stack<LittleEndianDataOutputStream>();
        //Use an identity hashmap beause value equality is not always
        //valid for the mapping between object and serialised index.
        //Duplicates in the input data set should be removed before
        //serialisation occurs.
        objectToIndexMap = new IdentityHashMap<Object, Integer>();
    }

    public void writeFileIdentifier(OutputStream out) throws IOException
    {
        out.write(FileIdentifier.BYTES);
    }

    public int getObjectIndex(Object key)
    {
        if (key == null)
        {
            return 0;
        }
        Integer value = objectToIndexMap.get(key);
        if (value == null)
        {
            //the object does not exist in the mapping.
            throw new IllegalStateException("mising reference to " + key);
        }
        return value.intValue();
    }

    public void addObject(Object key)
    {
        if (key == null)
        {
            throw new NullPointerException("key is null");
        }
        Integer value = objectToIndexMap.get(key);
        if (value != null)
        {
            //the object already exists in the mapping.
            return;
        }
        //get the index it should have
        //add 1 because null is index 0
        value = Integer.valueOf(objectToIndexMap.size() + 1);
        //store it in the mapping as a new key
        objectToIndexMap.put(key, value);
    }

    public byte[] serializeSingle(Serializable object) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pushOutputStream(out);

        object.serialise(this);

        popOutputStream();
        return out.toByteArray();
    }

    public void serializeSingle(OutputStream out, Serializable object) throws IOException
    {
        pushOutputStream(out);

        object.serialise(this);

        popOutputStream();
    }

    public void serialize(OutputStream out, Object3D[] roots) throws IOException
    {
        pushOutputStream(out);
        
        //Because the m3g format requires a totalFileSize encoded at the start
        //we must write the file to an array. Then we can fill out the fileSize
        //field and send the data to a file.
        Section headerSection = new Section(Section.UNCOMPRESSED_ADLER32);
        Header headerObject = new Header();
        headerObject.setVersion(version);
        headerObject.setAuthoringInformation(author);

        int headerSize = 0;
        {
            //Write the header out once just to get the size
            ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
            pushOutputStream(headerStream);
            headerSection.serialise(this, new SectionSerializable[]{ headerObject });
            popOutputStream();
            headerStream.flush();
            headerSize = headerStream.size();
        }

        //write out content to an array
        byte[] contentData = null;
        {
            ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
            
            Section contentSection = new Section(Section.UNCOMPRESSED_ADLER32);
            //get the objects in depth first order
            List<SectionSerializable> dfoObjects = getDepthFirstOrderList(roots);
            SectionSerializable[] objects = new SectionSerializable[dfoObjects.size()];
            dfoObjects.toArray(objects);

            pushOutputStream(contentStream);
            contentSection.serialise(this, objects);
            popOutputStream();

            //store it to an array
            contentData = contentStream.toByteArray();
        }

        //calculate the total file size
        int totalFileSize = FileIdentifier.BYTES.length
                          + headerSize
                          + contentData.length;
        headerObject.setTotalFileSize(totalFileSize);
        headerObject.setApproximateContentSize(totalFileSize);

        //assume the file identifier has already been written
        //serialise the header section again to make sure the right sizes
        //are written.
        headerSection.serialise(this, new SectionSerializable[]{ headerObject });
        //write out the content data we serialised earlier
        write(contentData);
        
        popOutputStream();
    }

    private List<SectionSerializable> getDepthFirstOrderList(Object3D[] roots)
    {
        //store the objects in leaf first order from a depth-first search.
        List<SectionSerializable> objects = new Vector<SectionSerializable>();
        //use a containment set to ensure there are no duplicates.
        Set<SectionSerializable> containmentSet = new HashSet<SectionSerializable>();

        for (Object3D root : roots)
        {
            processDepthFirstOrderList(root, objects, containmentSet);
        }

        //System.out.println("objects: " + objects);
        return objects;
    }

    private void processDepthFirstOrderList(Object3D object,
            List<SectionSerializable> objects, Set<SectionSerializable> containmentSet)
    {
        if (containmentSet.contains(object))
        {
            return;
        }

        final int referenceCount = object.getReferences(null);
        //System.out.println("referenceCount: " + referenceCount);
        if (referenceCount > 0)
        {
            Object3D[] references = new Object3D[referenceCount];
            object.getReferences(references);
            for (Object3D ref : references)
            {
                if (!containmentSet.contains(ref))
                {
                    processDepthFirstOrderList(ref, objects, containmentSet);
                }
            }
        }
        //all the children will have been added first
        objects.add(object);
        containmentSet.add(object);
    }

    public void popOutputStream()
    {
        LittleEndianDataOutputStream top = outputStreams.pop();
        try
        {
            top.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void pushOutputStream(OutputStream out)
    {
        outputStreams.push(new LittleEndianDataOutputStream(out));
    }

    public LittleEndianDataOutputStream getOutputStream()
    {
        return outputStreams.peek();
    }

    public void write(int b) throws IOException
    {
        getOutputStream().write(b);
    }

    public void write(byte[] b) throws IOException
    {
        getOutputStream().write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        getOutputStream().write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException
    {
        getOutputStream().writeBoolean(v);
    }

    public void writeUnsignedByte(int v) throws IOException
    {
        if (v < 0)
        {
            throw new IllegalArgumentException("v < 0");
        }
        if (v > 255)
        {
            throw new IllegalArgumentException("v > 255");
        }
        
        getOutputStream().writeByte(v);
    }

    public void writeByte(int v) throws IOException
    {
        if (v < Byte.MIN_VALUE)
        {
            throw new IllegalArgumentException("v < Byte.MIN_VALUE");
        }
        if (v > Byte.MAX_VALUE)
        {
            throw new IllegalArgumentException("v > Byte.MAX_VALUE");
        }

        getOutputStream().writeByte(v);
    }

    public void writeByteArray(byte[] v) throws IOException
    {
        LittleEndianDataOutputStream out = getOutputStream();
        if (v == null)
        {
            out.writeInt(0);
        }
        else
        {
            out.writeInt(v.length);
            out.write(v, 0, v.length);
        }
    }

    public void writeUnsignedShort(int v) throws IOException
    {
        if (v < 0)
        {
            throw new IllegalArgumentException("v < 0");
        }
        if (v > 65535)
        {
            throw new IllegalArgumentException("v > 65535");
        }

        getOutputStream().writeShort(v);
    }

    public void writeShort(int v) throws IOException
    {
        if (v < Short.MIN_VALUE)
        {
            throw new IllegalArgumentException("v < Short.MIN_VALUE");
        }
        if (v > Short.MAX_VALUE)
        {
            throw new IllegalArgumentException("v > Short.MAX_VALUE");
        }

        getOutputStream().writeShort(v);
    }

    public void writeChar(int v) throws IOException
    {
        getOutputStream().writeChar(v);
    }

    public void writeInt(int v) throws IOException
    {
        getOutputStream().writeInt(v);
    }

    public void writeLong(long v) throws IOException
    {
        getOutputStream().writeLong(v);
    }

    public void writeFloat(float v) throws IOException
    {
        getOutputStream().writeFloat(v);
    }

    public void writeDouble(double v) throws IOException
    {
        getOutputStream().writeDouble(v);
    }

    public void writeBytes(String s) throws IOException
    {
        getOutputStream().writeBytes(s);
    }

    public void writeChars(String s) throws IOException
    {
        getOutputStream().writeChars(s);
    }

    public void writeUTF(String str) throws IOException
    {
        getOutputStream().writeUTF(str);
    }

    public void writeUTF8(String str) throws IOException
    {
        getOutputStream().writeUTF8(str);
    }

    public void writeReference(Object object) throws IOException
    {
        final int index = getObjectIndex(object);
        if (index < 0)
        {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        //System.out.println(object + " : " + index);
        writeInt(index);
    }

    public void startChecksum(Adler32 adler) throws IOException
    {
        getOutputStream().startChecksum(adler);
    }

    public void endChecksum() throws IOException
    {
        getOutputStream().endChecksum();
    }

}
