package m3x.m3g.primitives;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;

public class ArrayType implements M3GSerializable
{
  private M3GSerializable[] array;
  
  public ArrayType(M3GSerializable[] array)
  {
    assert(array != null);
    this.array = array;
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    for (int i = 0; i < this.array.length; i++)
    {
      this.array[i].serialize(dataOutputStream, null);
    }
  }
}
