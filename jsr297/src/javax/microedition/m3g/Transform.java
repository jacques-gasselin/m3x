/*
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

package javax.microedition.m3g;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
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

    private static final void requireNotNull(Object obj, String name)
    {
        if (obj == null)
        {
            throw new NullPointerException(name + " is null");
        }
    }

    public void add(Transform transform)
    {
        requireNotNull(transform, "transform");

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
        requireNotNull(matrix, "matrix");

        throw new UnsupportedOperationException();
    }

    public void invert()
    {
        this.matrix.invert();
    }

    public void multiply(float scalar)
    {
        this.matrix.mul(scalar);
    }

    public void postMultiply(Transform transform)
    {
        requireNotNull(transform, "transform");

        this.matrix.mul(transform.matrix);
    }

    public void postRotate(float angle, float ax, float ay, float az)
    {
        Matrix4f rotate = new Matrix4f();
        rotate.setIdentity();
        rotate.set(new AxisAngle4f(ax, ay, az, (float)Math.toRadians(angle)));
        this.matrix.mul(rotate);
    }

    public void postRotateQuat(float qx, float qy, float qz, float qw)
    {
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
        requireNotNull(matrix, "matrix");

        this.matrix.set(matrix);
    }

    public void set(Transform transform)
    {
        requireNotNull(transform, "transform");

        this.matrix.set(transform.matrix);
    }

    public void setIdentity()
    {
        this.matrix.setIdentity();
    }

    public void transform(float[] vectors)
    {
        requireNotNull(vectors, "vectors");

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
        requireNotNull(in, "in");
        requireNotNull(out, "out");
        
        throw new UnsupportedOperationException();
    }

    public void transpose()
    {
        this.matrix.transpose();
    }
}
