/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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

        final float c = qw;
        if (Math.abs(c) > 0.999999f)
        {
            //angle is close enough to zero that rotation is undefined
            //return the zero vector in this case.
            angleAxis[0] = 0;
            angleAxis[1] = 0;
            angleAxis[2] = 0;
            angleAxis[3] = 0;
        }
        else
        {
            //sine of the half angle is found using trig identity
            final float invS = 1.0f / ((float) Math.sqrt(1 - (c * c)));
            angleAxis[0] = (float) Math.toDegrees(Math.acos(c) * 2);
            angleAxis[1] = qx * invS;
            angleAxis[2] = qy * invS;
            angleAxis[3] = qz * invS;
        }
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
        rotate(angle, ax, ay, az, false);
    }

    /**
     * Pre or post rotates the orientation of this quaternion.
     * @param angle
     * @param ax
     * @param ay
     * @param az
     * @param pre
     */
    private void rotate(float angle, float ax, float ay, float az, boolean pre)
    {
        //FIXME: this needs to be an epsilon equals
        if (angle == 0.0f)
        {
            return;
        }
        else
        {
            final float length = (float) Math.sqrt(ax * ax + ay * ay + az * az);

            //FIXME: this needs to be an epsilon equals
            if (length == 0)
            {
                throw new IllegalArgumentException("rotation axis is zero and" +
                        " angle is nonzero");
            }

            final double radHalf = Math.toRadians(angle * 0.5f);
            final float c = (float) Math.cos(radHalf);
            final float s = (float) Math.sin(radHalf) / length;

            multiplyQuaternion(ax * s, ay * s, az * s, c, pre);
        }
    }

    /**
     * Multiplies the quaternion of this transformable from the left or right
     * by a given quaternion
     * @param qx
     * @param qy
     * @param qz
     * @param qw
     * @param pre
     */
    private void multiplyQuaternion(float qx, float qy, float qz, float qw, boolean pre)
    {
        float qwL, qxL, qyL, qzL;
        float qwR, qxR, qyR, qzR;
        if (pre) //multiply current quaternion from the left
        {
            qwL = qw;
            qxL = qx;
            qyL = qy;
            qzL = qz;

            qwR = this.qw;
            qxR = this.qx;
            qyR = this.qy;
            qzR = this.qz;
        }
        else //multiply current quaternion from the right
        {
            qwL = this.qw;
            qxL = this.qx;
            qyL = this.qy;
            qzL = this.qz;

            qwR = qw;
            qxR = qx;
            qyR = qy;
            qzR = qz;
        }

        final float x = qwL * qxR + qxL * qwR + qyL * qzR - qzL * qyR;
        final float y = qwL * qyR - qxL * qzR + qyL * qwR + qzL * qxR;
        final float z = qwL * qzR - qxL * qyR - qyL * qxR + qzL * qwR;
        final float w = qwL * qwR - qxL * qxR - qyL * qyR - qzL * qzR;

        setOrientationQuat(x, y, z, w);
    }

    public void postRotateQuat(float qx, float qy, float qz, float qw)
    {
        multiplyQuaternion(qx, qy, qz, qw, false);        
    }

    public void preRotate(float angle, float ax, float ay, float az)
    {
        rotate(angle, ax, ay, az, true);
    }

    public void preRotateQuat(float qx, float qy, float qz, float qw)
    {
        multiplyQuaternion(qx, qy, qz, qw, true);
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
        final float upMag = (float)Math.sqrt(upX * upX + upY * upY + upZ * upZ);
        final float targetMag = (float)Math.sqrt(targetX * targetX +
                                                 targetY * targetY +
                                                 targetZ * targetZ);

        if (upMag == 0.0f || targetMag == 0.0f)
        {
            throw new ArithmeticException("Expected non-zero target and up vectors, " +
                    "got target = " + targetX + ", " + targetY + ", " + targetZ +
                    " and up = " + upX + ", " + upY + ", " + upZ);
        }

        //normalize the target and up vectors
        upX /= upMag;
        upY /= upMag;
        upZ /= upMag;

        targetX /= targetMag;
        targetY /= targetMag;
        targetZ /= targetMag;

        //compute two consecutive rotations:
        //1. align the -z axis with the target direction
        //2. align the +y axis with the new up direction.
        //the result of these transformations is well defined if
        //the up and target vectors are linearly independent

        //transformation 1
        float cosHalfAngle1 = (float)Math.sqrt(-0.5f * targetZ + 0.5f);//since cos2v = 2cos^2v - 1
        float sinHalfAngle1 = (float)Math.sqrt(1 - cosHalfAngle1 * cosHalfAngle1);

        float q1x = targetY * sinHalfAngle1;
        float q1y = -targetX * sinHalfAngle1;
        float q1z = 0.0f;
        float q1w = cosHalfAngle1;

        //transformation 2
        //compute the transformed default up direction (ie +y) by
        //quaternion multiplication
        float defUpTransX = 0.0f;
        float defUpTransY = 0.0f;
        float defUpTransZ = 0.0f;

        //find the rotation around the target vector that aligns the
        //transformed default up direction with the desired up direction
        float a2x = targetX;
        float a2y = targetY;
        float a2z = targetZ;

        float cosAngle2 = upY;

        //getAlignmentTransformation(

        throw new UnsupportedOperationException();
    }



    public void setOrientationQuat(float qx, float qy, float qz, float qw)
    {
        if (qx == 0.0f && qy == 0.0f && qz == 0.0f && qw == 0.0f)
        {
            throw new IllegalArgumentException("Zero quaternion");
        }
        
        final float invNorm = (float) (1.0 / Math.sqrt(qx * qx + qy * qy + qz * qz + qw * qw));
        
        this.qx = qx * invNorm;
        this.qy = qy * invNorm;
        this.qz = qz * invNorm;
        this.qw = qw * invNorm;

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

    private void updateCompositeTransform()
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
