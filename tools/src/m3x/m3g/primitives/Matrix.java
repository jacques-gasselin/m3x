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

  private final static int MATRIX_LENGTH = 16;
  
  public Matrix(float[] matrix)
  {
    assert(matrix != null && matrix.length == MATRIX_LENGTH);
    this.matrix = matrix;
  }

  public Matrix(double[] matrix)
  {
    assert(matrix != null && matrix.length == MATRIX_LENGTH);
    this.matrix = new float[MATRIX_LENGTH];
    for (int i = 0; i < matrix.length; i++)
    {
      this.matrix[i] = (float)matrix[i];
    }
  }
  
  public Matrix()
  {
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    this.matrix = new float[MATRIX_LENGTH];
    for (int i = 0; i < MATRIX_LENGTH; i++)
    {
      this.matrix[i] = M3GSupport.readFloat(dataInputStream);
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
