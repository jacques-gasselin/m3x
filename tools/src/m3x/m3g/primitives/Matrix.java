package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
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
  
  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException, FileFormatException
  {
    for (int i = 0; i < 16; i++)
    {
      this.matrix[i] = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
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
