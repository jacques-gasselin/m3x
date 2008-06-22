package m3x.m3g.primitives;

import java.io.DataOutputStream;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class Matrix implements M3GSerializable
{
  /**
   * The 4x4 matrix.
   */
  private float[] matrix;

  public Matrix(float[] matrix)
  {
    assert(matrix != null && matrix.length == 16);
    this.matrix = matrix;
  }

  public Matrix(double[] matrix)
  {
    assert(matrix != null && matrix.length == 16);
    this.matrix = new float[16];
    for (int i = 0; i < matrix.length; i++)
    {
      this.matrix[i] = (float)matrix[i];
    }
  }
  
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws java.io.IOException
  {
    for (float element : this.matrix)
    {
      dataOutputStream.writeInt(M3GSupport.swapBytes(element));
    }
  }
}
