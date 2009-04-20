/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g.primitives;

import java.io.IOException;

import java.util.List;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

/**
 * A normal 4x4 matrix using floating point elements.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public class Matrix implements Serialisable
{
    /**
     * The 4x4 matrix.
     */
    private float[] matrix;
    private final static int MATRIX_LENGTH = 16;

    private static final float[] IDENTITY =
    {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };

    /**
     * Constructs a new matrix, a zero filled float[] is created at this point,
     * this is meant to be used with deserialize(), which will
     * then create the array.
     */
    public Matrix()
    {
        this.matrix = new float[MATRIX_LENGTH];
    }

    public Matrix(List<Float> matrix)
    {
        this();
        set(matrix);
    }

    /**
     * Constructs a new matrix.
     *
     * @param matrix
     */
    public Matrix(float[] matrix)
    {
        this();
        set(matrix);
    }

    /**
     * Constructs a new matrix from doubles.
     * @param matrix
     */
    public Matrix(double[] matrix)
    {
        this();
        set(matrix);
    }

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        this.matrix = new float[MATRIX_LENGTH];
        for (int i = 0; i < MATRIX_LENGTH; i++)
        {
            this.matrix[i] = deserialiser.readFloat();
        }
    }

    /**
     * Writes float array to the stream.
     */
    public void serialise(Serialiser serialiser) throws java.io.IOException
    {
        for (float element : this.matrix)
        {
            serialiser.writeFloat(element);
        }
    }

    public float[] getMatrix()
    {
        return this.matrix;
    }

    public void setIdentity()
    {
        set(IDENTITY);
    }

    public void set(float[] matrix)
    {
        if (matrix == null)
        {
            throw new NullPointerException("matrix is null");
        }
        if (matrix.length < 16)
        {
            throw new IllegalArgumentException("matrix.length < 16");
        }
        System.arraycopy(matrix, 0, this.matrix, 0, MATRIX_LENGTH);
    }

    public void set(double[] matrix)
    {
        if (matrix == null)
        {
            throw new NullPointerException("matrix is null");
        }
        if (matrix.length < 16)
        {
            throw new IllegalArgumentException("matrix.length < 16");
        }
        for (int i = 0; i < MATRIX_LENGTH; ++i)
        {
            this.matrix[i] = (float) matrix[i];
        }
    }

    public void set(List<Float> matrix)
    {
        if (matrix == null)
        {
            throw new NullPointerException("matrix is null");
        }
        if (matrix.size() < 16)
        {
            throw new IllegalArgumentException("matrix.size() < 16");
        }
        for (int i = 0; i < MATRIX_LENGTH; i++)
        {
            this.matrix[i] = matrix.get(i);
        }
    }

    @Override
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
