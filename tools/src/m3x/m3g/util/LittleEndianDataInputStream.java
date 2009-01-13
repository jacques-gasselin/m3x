package m3x.m3g.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jgasseli
 */
public class LittleEndianDataInputStream extends Object
        implements DataInput
{
    private DataInputStream dataInputStream;

    private LittleEndianDataInputStream()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a data input object that reads from a stream.
     * All data methods read values in little-endian byte
     * order. This is the reverse to DataInputStream which always
     * reads in big-endian order.
     * @param in the stream to read from
     */
    public LittleEndianDataInputStream(InputStream in)
    {
        if (in == null) {
            throw new NullPointerException("in is null");
        }
        dataInputStream = new DataInputStream(in);
    }

    /**
     * Creates a data input object that reads from a stream.
     * All data methods read values in little-endian byte
     * order. This is the reverse to DataInputStream which always
     * reads in big-endian order.
     * @param in the stream to read from
     */
    public LittleEndianDataInputStream(DataInputStream in)
    {
        if (in == null) {
            throw new NullPointerException("in is null");
        }
        dataInputStream = in;
    }

    public void close() throws IOException
    {
        dataInputStream.close();
    }

    public void readFully(byte[] b) throws IOException
    {
        dataInputStream.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException
    {
        dataInputStream.readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException
    {
        return dataInputStream.skipBytes(n);
    }

    public boolean readBoolean() throws IOException
    {
        return dataInputStream.readBoolean();
    }

    public byte readByte() throws IOException
    {
        return dataInputStream.readByte();
    }

    public int readUnsignedByte() throws IOException
    {
        return dataInputStream.readUnsignedByte();
    }

    public short readShort() throws IOException
    {
        return Short.reverseBytes(dataInputStream.readShort());
    }

    public int readUnsignedShort() throws IOException
    {
        return ((int) readShort()) & 0x0000ffff;
    }

    public char readChar() throws IOException
    {
        return Character.reverseBytes(dataInputStream.readChar());
    }

    public int readInt() throws IOException
    {
        return Integer.reverseBytes(dataInputStream.readInt());
    }

    public long readLong() throws IOException
    {
        return Long.reverseBytes(dataInputStream.readLong());
    }

    public float readFloat() throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }

    public String readLine() throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String readUTF() throws IOException
    {
        return dataInputStream.readUTF();
    }

    public String readUTF8() throws IOException
    {
        final int bufferSize = 256;
        byte[] buffer = new byte[bufferSize];
        int index = 0;
        while (true)
        {
            byte ch = dataInputStream.readByte();
            if (ch == 0)
            {
                //terminating byte
                break;
            }
            if (index == buffer.length)
            {
                //resize the buffer
                byte[] newBuffer = new byte[buffer.length + bufferSize];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
            buffer[index++] = ch;
        }
        if (index > 0)
        {
            return new String(buffer, 0, index, "UTF-8");
        }
        return new String();
    }

    public DataInputStream getDataInputStream()
    {
        return this.dataInputStream;
    }
}
