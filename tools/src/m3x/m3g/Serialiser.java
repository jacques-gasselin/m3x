package m3x.m3g;

import m3x.m3g.primitives.Header;
import m3x.m3g.primitives.Serializable;
import m3x.m3g.primitives.FileIdentifier;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.zip.Adler32;
import m3x.m3g.primitives.Section;
import m3x.m3g.util.LittleEndianDataOutputStream;

/**
 *
 * @author jgasseli
 */
public class Serialiser implements DataOutput
{
    private String version;
    private String author;
    private Stack<LittleEndianDataOutputStream> outputStreams;
    private Hashtable<Object, Integer> objectToIndexMap;
    
    public Serialiser(String version, String author)
    {
        this.version = version;
        this.author = author;

        outputStreams = new Stack<LittleEndianDataOutputStream>();
        objectToIndexMap = new Hashtable<Object, Integer>();
        //add null as index 0
        getObjectIndex(null);
    }

    public void writeFileIdentifier(OutputStream out) throws IOException
    {
        out.write(FileIdentifier.BYTES);
    }

    public int getObjectIndex(Object key)
    {
        Integer value = objectToIndexMap.get(key);
        if (value == null)
        {
            //the object does not exist in the mapping.
            //get the index it should have
            value = objectToIndexMap.size();
            //store it in the mapping as a new key
            objectToIndexMap.put(key, value);
        }
        return value;
    }

    public void serialise(OutputStream out, Object3D[] roots) throws IOException
    {
        pushOutputStream(out);

        Section headerSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32);
        //add an object index for the header object.
        Header headerObject = new Header();
        getObjectIndex(headerObject);


        //write out content to the file
        Section contentSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32);
        //get the objects in leaf first order
        List<Serializable> lfoObjects = getLeafFirstOrderList(roots);
        Serializable[] objects = lfoObjects.toArray(null);
        contentSection.serialize(this, objects);

        popOutputStream();
    }

    private List<Serializable> getLeafFirstOrderList(Object3D[] roots)
    {
        //store the objects in leaf first order from a depth-first search.
        List<Serializable> objects = new Vector<Serializable>();
        //use a containment set to ensure there are no duplicates.
        Set<Serializable> containmentSet = new HashSet<Serializable>();

        for (Object3D root : roots)
        {
            processLeafFirstOrderList(root, objects, containmentSet);
        }

        return objects;
    }

    private void processLeafFirstOrderList(Object3D object,
            List<Serializable> objects, Set<Serializable> containmentSet)
    {
        if (containmentSet.contains(object))
        {
            return;
        }

        final int referenceCount = object.getReferences(null);
        if (referenceCount == 0)
        {
            //it is a leaf.
            objects.add(object);
            containmentSet.add(object);
        }
        else
        {
            Object3D[] references = new Object3D[referenceCount];
            object.getReferences(references);
            for (Object3D ref : references)
            {
                if (!containmentSet.contains(ref))
                {
                    processLeafFirstOrderList(ref, objects, containmentSet);
                    //all the children will have been added first
                    objects.add(ref);
                    containmentSet.add(ref);
                }
            }
        }
    }

    public void popOutputStream()
    {
        outputStreams.pop();
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

    public void writeByte(int v) throws IOException
    {
        getOutputStream().writeByte(v);
    }

    public void writeShort(int v) throws IOException
    {
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
        writeInt(getObjectIndex(object));
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
