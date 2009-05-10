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

/**
 * @author jgasseli
 */
public final class Transform
{
    public Transform()
    {
        setIdentity();
    }

    public Transform(Transform transform)
    {
        set(transform);
    }

    private static final void requireNotNull(VertexArray array)
    {
        if (array == null)
        {
            throw new NullPointerException("array is null");
        }
    }

    private static final void requireNotNull(Transform transform)
    {
        if (transform == null)
        {
            throw new NullPointerException("transform is null");
        }
    }

    private static final void requireNotNull(float[] matrix)
    {
        if (matrix == null)
        {
            throw new NullPointerException("matrix is null");
        }
    }
    
    public void add(Transform transform)
    {
        requireNotNull(transform);

        throw new UnsupportedOperationException();
    }

    public float determinant()
    {
        throw new UnsupportedOperationException();
    }

    public float determinant3x3()
    {
        throw new UnsupportedOperationException();
    }

    public void get(float[] matrix)
    {
        requireNotNull(matrix);

        throw new UnsupportedOperationException();
    }

    public void invert()
    {
        throw new UnsupportedOperationException();
    }

    public void multiply(float scalar)
    {
        throw new UnsupportedOperationException();
    }

    public void postMultiply(Transform transform)
    {
        requireNotNull(transform);
        
        throw new UnsupportedOperationException();
    }

    public void postRotate(float angle, float ax, float ay, float az)
    {
        throw new UnsupportedOperationException();
    }

    public void postRotateQuat(float qx, float qy, float qz, float qw)
    {
        throw new UnsupportedOperationException();
    }

    public void postScale(float sx, float sy, float sz)
    {
        throw new UnsupportedOperationException();
    }

    public void postTranslate(float tx, float ty, float tz)
    {
        throw new UnsupportedOperationException();
    }
    
    public void set(float[] matrix)
    {
        requireNotNull(matrix);

        throw new UnsupportedOperationException();
    }

    public void set(Transform transform)
    {
        requireNotNull(transform);
        
        throw new UnsupportedOperationException();
    }

    public void setIdentity()
    {
        throw new UnsupportedOperationException();
    }

    public void transform(float[] vectors)
    {
        requireNotNull(vectors);
        
        throw new UnsupportedOperationException();
    }

    public void transform(VertexArray in, float[] out, boolean w)
    {
        requireNotNull(in);
        requireNotNull(out);
        
        throw new UnsupportedOperationException();
    }

    public void transpose()
    {
        throw new UnsupportedOperationException();
    }
}