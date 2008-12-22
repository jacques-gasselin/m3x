package m3x.m3g;

import java.io.ByteArrayInputStream;
import m3x.m3g.primitives.FileIdentifier;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;
import m3x.m3g.primitives.Section;
import m3x.m3g.primitives.Serialisable;
import m3x.m3g.util.LittleEndianDataInputStream;

/**
 *
 * @author jgasseli
 */
public class Deserialiser implements DataInput
{

    private Vector<Object3D> rootObjects;
    private Vector<Object3D> objects;
    private Stack<LittleEndianDataInputStream> inputStreams;

    public Deserialiser()
    {
        rootObjects = new Vector<Object3D>();
        objects = new Vector<Object3D>();
        inputStreams = new Stack<LittleEndianDataInputStream>();

        //always have the null object at index 0
        objects.add(null);
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

    public void deserialise(InputStream stream) throws IOException
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

    public void deserialiseSingle(byte[] data, Serialisable object)
        throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        pushInputStream(in);

        object.deserialise(this);

        popInputStream();
    }

    public void pushInputStream(InputStream stream)
    {
        inputStreams.push(new LittleEndianDataInputStream(stream));
    }

    public void popInputStream()
    {
        inputStreams.pop();
    }

    public LittleEndianDataInputStream getInputStream()
    {
        return inputStreams.peek();
    }

    public void addObject(Object3D obj)
    {
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

    public void readFully(byte[] b) throws IOException
    {
        getInputStream().readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException
    {
        getInputStream().readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException
    {
        return getInputStream().skipBytes(n);
    }

    public boolean readBoolean() throws IOException
    {
        return getInputStream().readBoolean();
    }

    public byte readByte() throws IOException
    {
        return getInputStream().readByte();
    }

    public int readUnsignedByte() throws IOException
    {
        return getInputStream().readUnsignedByte();
    }

    public short readShort() throws IOException
    {
        return getInputStream().readShort();
    }

    public int readUnsignedShort() throws IOException
    {
        return getInputStream().readUnsignedShort();
    }

    public char readChar() throws IOException
    {
        return getInputStream().readChar();
    }

    public int readInt() throws IOException
    {
        return getInputStream().readInt();
    }

    public long readLong() throws IOException
    {
        return getInputStream().readLong();
    }

    public float readFloat() throws IOException
    {
        return getInputStream().readFloat();
    }

    public double readDouble() throws IOException
    {
        return getInputStream().readDouble();
    }

    public String readLine() throws IOException
    {
        return getInputStream().readLine();
    }

    public String readUTF() throws IOException
    {
        return getInputStream().readUTF();
    }

    public String readUTF8() throws IOException
    {
        return getInputStream().readUTF8();
    }

    /**
     * Reads an object reference from the stream. If the object was previously
     * considered a root object it is no longer treated as such.
     * @return the referenced object
     * @throws java.io.IOException
     */
    public Object3D readReference() throws IOException
    {
        int index = readInt();
        Object3D obj = objects.elementAt(index);
        if (rootObjects.contains(obj))
        {
            rootObjects.remove(obj);
        }
        System.out.println(index + " : " + obj);
        return obj;
    }

    /**
     * Reads an object reference from the stream. If the object is a root object
     * it will remain as such.
     * @return the weakly referenced object
     * @throws java.io.IOException
     */
    public Object3D readWeakReference() throws IOException
    {
        Object3D obj = objects.elementAt(readInt());
        return obj;
    }
}
