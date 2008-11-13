package m3x.m3g.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class LittleEndianDataOutputStream implements DataOutput
{
  private final DataOutputStream dos;
  
  private LittleEndianDataOutputStream()
  {    
    this.dos = null;
  }
  
  public LittleEndianDataOutputStream(DataOutputStream dos)
  {
    this.dos = dos;
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
}
