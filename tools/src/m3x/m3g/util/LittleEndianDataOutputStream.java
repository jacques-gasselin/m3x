package m3x.m3g.util;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Adler32;

public class LittleEndianDataOutputStream implements DataOutput
{
    private final OutputStream originalStream;
    private DataOutputStream dos;

    private LittleEndianDataOutputStream()
    {
        super();
        originalStream = null;
    }

    public LittleEndianDataOutputStream(OutputStream out)
    {
        originalStream = out;
        dos = new DataOutputStream(out);
    }

    public void startChecksum(Adler32 adler) throws IOException
    {
        //make sure the previous data is written
        dos.flush();
        dos = new DataOutputStream(
                new Adler32FilterOutputStream(adler, originalStream));
    }

    public void endChecksum() throws IOException
    {
        //make sure all the checksum data is written
        dos.flush();
        dos = new DataOutputStream(originalStream);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        this.dos.write(b, off, len);
    }

    public void write(byte[] b) throws IOException
    {
        this.dos.write(b);
    }

    public void write(int b) throws IOException
    {
        this.dos.write(b);
    }

    public void writeBoolean(boolean v) throws IOException
    {
        this.dos.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException
    {
        this.dos.write(v);
    }

    public void writeBytes(String s) throws IOException
    {
        this.dos.writeBytes(s);
    }

    public void writeChar(int v) throws IOException
    {
        this.dos.writeChar(v);
    }

    public void writeChars(String s) throws IOException
    {
        this.dos.writeChars(s);
    }

    public void writeDouble(double v) throws IOException
    {
        long value = Double.doubleToLongBits(v);
        this.dos.writeLong(Long.reverseBytes(value));
    }

    public void writeFloat(float v) throws IOException
    {
        int value = Float.floatToIntBits(v);
        this.dos.writeInt(Integer.reverseBytes(value));
    }

    public void writeInt(int v) throws IOException
    {
        this.dos.writeInt(Integer.reverseBytes(v));
    }

    public void writeLong(long v) throws IOException
    {
        this.dos.writeLong(Long.reverseBytes(v));
    }

    public void writeShort(int v) throws IOException
    {
        this.dos.writeShort(Short.reverseBytes((short) v));
    }

    public void writeUTF(String str) throws IOException
    {
        this.dos.writeUTF(str);
    }

    public void writeUTF8(String str) throws IOException
    {
        if (str == null)
        {
            throw new NullPointerException("str is null");
        }
        this.dos.write(str.getBytes("UTF-8"));
        this.dos.writeByte(0);
    }
}