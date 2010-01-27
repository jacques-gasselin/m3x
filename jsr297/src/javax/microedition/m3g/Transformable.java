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

/**
 * @author jgasseli
 */
public abstract class Transformable extends Object3D
{
    private float tx, ty, tz;
    private float qx, qy, qz, qw = 1.0f;
    private float sx = 1.0f, sy = 1.0f, sz = 1.0f;
    private final Transform transform = new Transform();
    private boolean compositeTransformNeedsUpdate;
    private final Transform compositeTransform = new Transform();
    
    public Transformable()
    {
        
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);

        final Transformable t = (Transformable) target;
        t.setOrientationQuat(qx, qy, qz, qw);
        t.setScale(sx, sy, sz);
        t.setTranslation(tx, ty, tz);
        t.setTransform(transform);
    }
    
    final Transform getCompositeTransform()
    {
        if (compositeTransformNeedsUpdate)
        {
            updateCompositeTransform();
        }

        return this.compositeTransform;
    }

    public void getCompositeTransform(Transform transform)
    {
        Require.notNull(transform, "transform");

        transform.set(getCompositeTransform());
    }

    public void getOrientation(float[] angleAxis)
    {
        Require.argumentHasCapacity(angleAxis, "angleAxis", 4);
        
        throw new UnsupportedOperationException();
    }

    public void getOrientationQuat(float[] quaternion)
    {
        Require.argumentHasCapacity(quaternion, "quaternion", 4);

        quaternion[0] = qx;
        quaternion[1] = qy;
        quaternion[2] = qz;
        quaternion[3] = qw;
    }

    public void getScale(float[] xyz)
    {
        Require.argumentHasCapacity(xyz, "xyz", 3);

        xyz[0] = sx;
        xyz[1] = sy;
        xyz[2] = sz;
    }

    public void getTransform(Transform transform)
    {
        Require.notNull(transform, "transform");

        transform.set(this.transform);
    }

    public void getTranslation(float[] xyz)
    {
        Require.argumentHasCapacity(xyz, "xyz", 3);

        xyz[0] = tx;
        xyz[1] = ty;
        xyz[2] = tz;
    }

    public void postRotate(float angle, float ax, float ay, float az)
    {
        compositeTransformNeedsUpdate = true;

        throw new UnsupportedOperationException();
    }

    public void postRotateQuat(float qx, float qy, float qz, float qw)
    {
        compositeTransformNeedsUpdate = true;

        throw new UnsupportedOperationException();
    }

    public void preRotate(float angle, float ax, float ay, float az)
    {
        compositeTransformNeedsUpdate = true;

        throw new UnsupportedOperationException();
    }

    public void preRotateQuat(float qx, float qy, float qz, float qw)
    {
        compositeTransformNeedsUpdate = true;

        throw new UnsupportedOperationException();
    }

    public void scale(float sx, float sy, float sz)
    {
        this.sx *= sx;
        this.sy *= sy;
        this.sz *= sz;

        compositeTransformNeedsUpdate = true;
    }

    public void setOrientation(float angle, float ax, float ay, float az)
    {
        if (angle == 0)
        {
            setOrientationQuat(0.0f, 0.0f, 0.0f, 1.0f);
        }
        else
        {
            final float length = (float) Math.sqrt(ax * ax + ay * ay + az * az);

            if (length == 0)
            {
                throw new IllegalArgumentException("rotation axis is zero and" +
                        " angle is nonzero");
            }

            final double radHalf = Math.toRadians(angle * 0.5f);
            final float c = (float) Math.cos(radHalf);
            final float s = (float) Math.sin(radHalf) / length;

            setOrientationQuat(ax * s, ay * s, az * s, c);
        }
    }

    public void setOrientationLookAt(float targetX, float targetY, float targetZ,
            float upX, float upY, float upZ)
    {
        compositeTransformNeedsUpdate = true;

        throw new UnsupportedOperationException();
    }

    public void setOrientationQuat(float qx, float qy, float qz, float qw)
    {
        this.qx = qx;
        this.qy = qy;
        this.qz = qz;
        this.qw = qw;

        compositeTransformNeedsUpdate = true;
    }

    public void setScale(float sx, float sy, float sz)
    {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;

        compositeTransformNeedsUpdate = true;
    }

    public void setTransform(Transform transform)
    {
        if (transform == null)
        {
            this.transform.setIdentity();
        }
        else
        {
            this.transform.set(transform);
        }

        compositeTransformNeedsUpdate = true;
    }

    public void setTranslation(float tx, float ty, float tz)
    {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;

        compositeTransformNeedsUpdate = true;
    }

    public void translate(float tx, float ty, float tz)
    {
        this.tx += tx;
        this.ty += ty;
        this.tz += tz;

        compositeTransformNeedsUpdate = true;
    }

    private final void updateCompositeTransform()
    {
        final Transform t = this.compositeTransform;
        t.setIdentity();
        t.postTranslate(tx, ty, tz);
        t.postRotateQuat(qx, qy, qz, qw);
        t.postScale(sx, sy, sz);
        t.postMultiply(transform);
        compositeTransformNeedsUpdate = false;
    }
}
