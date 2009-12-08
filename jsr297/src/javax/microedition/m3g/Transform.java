/*
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

package javax.microedition.m3g;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector4f;

/**
 * @author jgasseli
 */
public final class Transform
{
    private Matrix4f matrix = new Matrix4f();
    
    public Transform()
    {
        setIdentity();
    }

    public Transform(Transform transform)
    {
        set(transform);
    }

    private static final void requireMinLength(float[] arr, int length, String name)
    {
        if (arr.length < length)
        {
            throw new IllegalArgumentException(name + ".length < " + length);
        }
    }

    public void add(Transform transform)
    {
        Require.notNull(transform, "transform");

        this.matrix.add(transform.matrix);
    }

    public float determinant()
    {
        return this.matrix.determinant();
    }

    public float determinant3x3()
    {
        throw new UnsupportedOperationException();
    }

    public void get(float[] matrix)
    {
        Require.notNull(matrix, "matrix");
        requireMinLength(matrix, 16, "matrix");

        //row-major
        final float[] rowValues = new float[4];
        for (int row = 0; row < 4; ++row)
        {
            this.matrix.getRow(row, rowValues);
            System.arraycopy(rowValues, 0, matrix, row * 4, 4);
        }
    }

    final float[] getColumnMajor()
    {
        final float[] matrix = new float[16];
        
        //column-major
        final float[] columnValues = new float[4];
        for (int column = 0; column < 4; ++column)
        {
            this.matrix.getColumn(column, columnValues);
            System.arraycopy(columnValues, 0, matrix, column * 4, 4);
        }

        return matrix;
    }
    
    /**
     * Inverts the transform matrix.
     * @throws ArithmeticException if the matrix is not invertible.
     */
    public void invert()
    {
        try
        {
            this.matrix.invert();
        }
        catch (SingularMatrixException e)
        {
            throw new ArithmeticException("singular matrix - non invertible");
        }
    }

    public void multiply(float scalar)
    {
        this.matrix.mul(scalar);
    }

    public void postMultiply(Transform transform)
    {
        Require.notNull(transform, "transform");

        this.matrix.mul(transform.matrix);
    }

    public void postRotate(float angle, float ax, float ay, float az)
    {
        if (angle != 0 && (ax == 0 && ay == 0 && az == 0))
        {
            throw new IllegalArgumentException("angle is nonzero but rotation axis is zero");
        }
        
        Matrix4f rotate = new Matrix4f();
        rotate.setIdentity();
        rotate.set(new AxisAngle4f(ax, ay, az, (float)Math.toRadians(angle)));
        this.matrix.mul(rotate);
    }

    public void postRotateQuat(float qx, float qy, float qz, float qw)
    {
        if (qx == 0 && qy == 0 && qz == 0 && qw == 0)
        {
            throw new IllegalArgumentException("quaternion components are all zero");
        }
        
        Matrix4f rotate = new Matrix4f();
        rotate.setIdentity();
        rotate.set(new Quat4f(qx, qy, qz, qw));
        this.matrix.mul(rotate);
    }

    public void postScale(float sx, float sy, float sz)
    {
        Matrix4f scale = new Matrix4f();
        scale.setIdentity();
        scale.m00 = sx;
        scale.m11 = sy;
        scale.m22 = sz;
        scale.m33 = 1;
        this.matrix.mul(scale);
    }

    public void postTranslate(float tx, float ty, float tz)
    {
        Matrix4f trans = new Matrix4f();
        trans.setIdentity();
        trans.m03 = tx;
        trans.m13 = ty;
        trans.m23 = tz;
        this.matrix.mul(trans);
    }
    
    public void set(float[] matrix)
    {
        Require.notNull(matrix, "matrix");
        requireMinLength(matrix, 16, "matrix");

        this.matrix.set(matrix);
    }

    /**
     * Sets the value of this transform to a copy of the passed in transform.
     *
     * @param transform the transform to copy from
     * @throws NullPointerException if transform is null
     */
    public void set(Transform transform)
    {
        Require.notNull(transform, "transform");

        this.matrix.set(transform.matrix);
    }

    /**
     * Sets this transform to the identity 4x4 matrix.
     */
    public void setIdentity()
    {
        this.matrix.setIdentity();
    }

    public void transform(float[] vectors)
    {
        Require.notNull(vectors, "vectors");

        final int vecCount = vectors.length >> 2;
        if (vecCount == 0)
        {
            return;
        }
        
        Vector4f vec = new Vector4f();
        
        this.matrix.transform(vec);
        throw new UnsupportedOperationException();
    }

    public void transform(VertexArray in, float[] out, boolean w)
    {
        Require.notNull(in, "in");
        Require.notNull(out, "out");
        
        throw new UnsupportedOperationException();
    }

    public void transpose()
    {
        this.matrix.transpose();
    }

    boolean is4thRowUnit()
    {
        if (this.matrix.m30 != 0)
        {
            return false;
        }
        if (this.matrix.m31 != 0)
        {
            return false;
        }
        if (this.matrix.m32 != 0)
        {
            return false;
        }
        if (this.matrix.m33 != 1)
        {
            return false;
        }
        return true;
    }
}
