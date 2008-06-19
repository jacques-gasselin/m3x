package m3x.m3g.primitives;

import java.io.DataOutputStream;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class Matrix implements M3GSerializable
{
  private float[] matrix;

  public Matrix(float[] matrix)
  {
    assert(matrix.length == 16);
    this.matrix = matrix;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws java.io.IOException
  {
    for (float element : this.matrix)
    {
      dataOutputStream.writeInt(M3GSupport.swapBytes(element));
    }
  }
}
