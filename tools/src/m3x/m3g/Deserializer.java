/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import java.io.ByteArrayInputStream;
import m3x.m3g.primitives.FileIdentifier;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;
import m3x.m3g.primitives.Section;
import m3x.m3g.primitives.Serializable;
import m3x.util.LittleEndianDeserializer;

/**
 *
 * @author jgasseli
 */
public final class Deserializer extends LittleEndianDeserializer
        implements DeserializerListener
{
    private Vector<Object3D> rootObjects;
    private Vector<Object3D> objects;
    private DeserializerListener listener;

    public Deserializer()
    {
        this(null);
    }

    public Deserializer(DeserializerListener l)
    {
        super();
        
        listener = l;
        rootObjects = new Vector<Object3D>();
        objects = new Vector<Object3D>();

        //always have the null object at index 0
        objects.add(null);
    }

    /**
     * Sets the listener to be used in deserialization. It may be handy for tools
     * to inject one of these for polishing or visualization.
     * @param l the listener to use, or null to disable listening
     */
    public void setListener(DeserializerListener l)
    {
        listener = l;
    }
    
    /**
     * Checks the stream for the correct file identifier.
     * Do this before actually deserialising the stream.
     * @param in
     * @return true if the file identifier is valid.
     * @throws java.io.IOException
     */
    public boolean verifyFileIdentifier(InputStream in)
        throws IOException
    {
        //compare the bytes at the current position to the
        //FileIdentifier constant.
        final byte[] bytes = FileIdentifier.BYTES;
        byte[] fileIdentifier = new byte[bytes.length];
        int pos = 0;
        while (pos < fileIdentifier.length)
        {
            int nread = in.read(fileIdentifier, pos, fileIdentifier.length - pos);
            if (nread == -1)
            {
                //EOF
                throw new EOFException();
            }
            pos += nread;
        }
        return Arrays.equals(fileIdentifier, bytes);
    }

    public void deserialize(InputStream stream) throws IOException
    {
        pushInputStream(stream);

        while (true)
        {
            try
            {
                Section section = new Section();
                section.deserialise(this);
            }
            catch (EOFException eof)
            {
                break;
            }
        }

        popInputStream();
    }

    public void deserializeSingle(byte[] data, Serializable object)
        throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        pushInputStream(in);

        object.deserialise(this);

        popInputStream();
    }

    public void addObject(Object3D obj)
    {
        final int index = objects.size();
        obj = objectDeserialized(obj, index);
        objects.add(obj);
        if (obj != null)
        {
            rootObjects.add(obj);
        }
    }

    /**
     * Gets a copy of the root objects in the file deserialised.
     * @return a array copy of the root objects
     */
    public Object3D[] getRootObjects()
    {
        Object3D[] ret = new Object3D[rootObjects.size()];
        rootObjects.copyInto(ret);
        return ret;
    }

    public byte[] readByteArray() throws IOException
    {
        final int length = readInt();
        byte[] arr = null;
        if (length > 0)
        {
            arr = new byte[length];
            for (int i = 0; i < length; ++i)
            {
                arr[i] = readByte();
            }
        }
        return arr;
    }

    /**
     * Reads an object reference from the stream. If the object was previously
     * considered a root object it is no longer treated as such.
     * @return the referenced object
     * @throws java.io.IOException
     */
    public Object3D readReference() throws IOException
    {
        final int index = readInt();
        if (index < 0)
        {
            throw new ArrayIndexOutOfBoundsException("index < 0");
        }
        if (index >= objects.size())
        {
            throw new ArrayIndexOutOfBoundsException("index >= objects.size()");
        }
        
        final Object3D obj = objects.elementAt(index);
        //rely on the implementation to make remove a no-op if obj is not
        //in the collection.
        rootObjects.remove(obj);

        return referenceDeserialized(obj, index);
    }

    /**
     * Reads an object reference from the stream. If the object is a root object
     * it will remain as such.
     * @return the weakly referenced object
     * @throws java.io.IOException
     */
    public Object3D readWeakReference() throws IOException
    {
        final int index = readInt();
        Object3D obj = objects.elementAt(index);

        return referenceDeserialized(obj, index);
    }

    public void sectionStarted(int compressionScheme, int totalSectionLength, int uncompressedLength)
    {
        if (listener != null)
        {
            listener.sectionStarted(compressionScheme, totalSectionLength, uncompressedLength);
        }
    }

    public void sectionEnded(int checksum)
    {
        if (listener != null)
        {
            listener.sectionEnded(checksum);
        }
    }

    public m3x.m3g.Object3D objectDeserialized(m3x.m3g.Object3D obj, int objectIndex)
    {
        m3x.m3g.Object3D ret = obj;
        if (listener != null)
        {
            ret = listener.objectDeserialized(obj, objectIndex);
        }
        return ret;
    }

    public m3x.m3g.Object3D referenceDeserialized(m3x.m3g.Object3D obj, int objectIndex)
    {
        m3x.m3g.Object3D ret = obj;
        if (listener != null)
        {
            ret = listener.referenceDeserialized(obj, objectIndex);
        }
        return ret;
    }
}
