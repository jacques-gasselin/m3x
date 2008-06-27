package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class CountedArrayType extends ArrayType implements M3GSerializable
{
  private final int count;
  
  public CountedArrayType(M3GSerializable[] array, int count)
  {
    super(array);
    assert(array.length == count);
    this.count = count;
  }
 
  public void deserialize(DataInputStream dataInputStream, String version) throws IOException, FileFormatException
  {
    throw new IOException("deserialize() cannot be implemented!");
  }
  
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.count));
    super.serialize(dataOutputStream, m3gVersion);
  }
}