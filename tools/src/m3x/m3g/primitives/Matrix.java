package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

/**
 * A normal 4x4 matrix using floating point elements.
 * 
 * @author jsaarinen
 */
public class Matrix implements M3GSerializable
{
    /**
     * The 4x4 matrix.
     */
    private float[] matrix;
    private final static int MATRIX_LENGTH = 16;

    /**
     * Constructs a new matrix.
     *
     * @param matrix
     */
    public Matrix(float[] matrix)
    {
        assert (matrix != null && matrix.length == MATRIX_LENGTH);
        this.matrix = matrix;
    }

    /**
     * Constructs a new matrix from doubles.
     * @param matrix
     */
    public Matrix(double[] matrix)
    {
        assert (matrix != null && matrix.length == MATRIX_LENGTH);
        this.matrix = new float[MATRIX_LENGTH];
        for (int i = 0; i < matrix.length; i++)
        {
            this.matrix[i] = (float) matrix[i];
        }
    }

    /**
     * Constructs a new matrix, no float[] created at this point,
     * this is meant to be used with deserialize(), which will
     * then create the array.
     */
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

    /**
     * Writes float array to the stream.
     */
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws java.io.IOException
    {
        for (float element : this.matrix)
        {
            M3GSupport.writeFloat(dataOutputStream, element);
        }
    }

    public float[] getMatrix()
    {
        return this.matrix;
    }

    public void setMatrix(float[] matrix)
    {
        this.matrix = matrix;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Matrix))
        {
            return false;
        }
        Matrix other = (Matrix) obj;
        int size = this.matrix.length;
        for (int i = 0; i < size; i++)
        {
            if (this.matrix[i] != other.matrix[i])
            {
                return false;
            }
        }
        return true;
    }
}
