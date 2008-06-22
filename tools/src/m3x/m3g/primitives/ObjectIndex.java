package m3x.m3g.primitives;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class ObjectIndex implements M3GSerializable
{
  private int index;

  public ObjectIndex(int index)
  {
    this.index = index;
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.index));
  }
}
