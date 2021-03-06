/*
 * Copyright (c) 2010, Jacques Gasselin de Richebourg
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

package m3x;

/**
 * Vector and linear algebra utility class.
 * 
 * @author jgasseli
 */
public final class Vmath
{
    private Vmath()
    {
        
    }

    /**
     * <p>Create a plane from the 3 position vectors {@code a}, {@code b} and
     * {@code c}. A normal will be calculated from the positions with {@code a},
     * {@code b} and {@code c} considered to be in counter clockwise order.
     * Thus the normal of the plane should be the normalized cross product of
     * {@code b->a} and {@code b->c}.</p>
     *
     * @param plane the plane 4D vector to store the results in.
     * @param a the first 3D position vector.
     * @param b the second 3D position vector.
     * @param c the third 3D position vector.
     */
    public static final void pcreate(float[] plane, float[] a, float[] b, float[] c)
    {
        final float[] bToA = new float[3];
        final float[] bToC = new float[3];
        vsub3(bToA, a, b);
        vsub3(bToC, c, b);
        vcross(plane, bToA, bToC);
        vnormalize3(plane);
        plane[3] = -vdot3(plane, b);
    }

    /**
     * <p>Create a plane from one position vector, {@code a}, and a normal,
     * {@code normal}. The position vector {@code a} is considered to be on the
     * plane and will be used to calculate the plane offset.
     *
     * @param plane the plane 4D vector to store the results in.
     * @param normal the plane normal, must be normalized already.
     * @param a a position vector that is on the plane.
     */
    public static final void pcreate(float[] plane, float[] normal, float[] a)
    {
        vmov3(plane, normal);
        plane[3] = -vdot3(normal, a);
    }

    /**
     * <p>Gets the distance of the given position vector from the given plane.
     * The plane vector is considered to consist of (nx, ny, nz, d).</p>
     *
     * @param plane the plane normal and distance from origin.
     * @param a the input position vector to find the distance to the plane with.
     * @return the distance from the plane for the vector.
     */
    public static final float pdist(float[] plane, float[] a)
    {
        return vdot3(plane, a) - plane[3];
    }

    /**
     * <p>Adds the 3D vector {@code a} to the 3D vector {@code b},
     * storing the result in the 3D vector {@code result}.</p>
     *
     * <p>This is equivalent to the + operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using either {@code a} or
     * {@code b} as the result vector is stable and will work as expected.</p>
     * 
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     * @param result the vector to store the result in.
     */
    public static final void vadd3(float[] result, float[] a, float[] b)
    {
        result[0] = a[0] + b[0];
        result[1] = a[1] + b[1];
        result[2] = a[2] + b[2];
    }

    /**
     * <p>Adds the 4D vector {@code a} to the 4D vector {@code b},
     * storing the result in the 4D vector {@code result}.</p>
     *
     * <p>This is equivalent to the + operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using either {@code a} or
     * {@code b} as the result vector is stable and will work as expected.</p>
     *
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     * @param result the vector to store the result in.
     */
    public static final void vadd4(float[] result, float[] a, float[] b)
    {
        result[0] = a[0] + b[0];
        result[1] = a[1] + b[1];
        result[2] = a[2] + b[2];
        result[3] = a[3] + b[3];
    }

    /**
     * <p>Adds the 4D vector {@code a} to the 4D vector {@code b},
     * storing the result back in {@code a}.</p>
     *
     * <p>This is equivalent to the += operator on two vectors</p>
     *
     * @param a the left operand and result, vector.
     * @param b the right operand, vector.
     */
    public static final void vadd4(float[] a, float[] b)
    {
        vadd4(a, a, b);
    }

    /**
     * <p>Computes the 3D cross product of the vectors {@code a} and {@code b},
     * storing the result in {@code result}.</p>
     * 
     * @param result the vector to store the result in.
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     */
    public static final void vcross(float[] result, float[] a, float[] b)
    {
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];
    }
    
    /**
     * Computes the 3D dot product of 2 input vectors.
     * 
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     * @return the dot product, scalar
     */
    public static final float vdot3(float[] a, float[] b)
    {
        return  a[0] * b[0] +
                a[1] * b[1] +
                a[2] * b[2];
    }

    /**
     * Computes the 4D dot product of 2 input vectors.
     * 
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     * @return the dot product, scalar
     */
    public static final float vdot4(float[] a, float[] b)
    {
        return  a[0] * b[0] +
                a[1] * b[1] +
                a[2] * b[2] +
                a[3] * b[3];
    }


    /**
     * <p>Interpolates the two 3D vectors {@code a, b} by the linear interpolant
     * {@code s}, storing the result in the 3D vector {@code result}.</p>
     *
     * <p>Note: the algorithm guarantees that using either {@code a} or {@code b}
     * as the result vector is stable and will work as expected.</p>
     *
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     * @param s the interpolant, scalar. The norm is to use a value in the range
     * [0, 1] though using values outside this range is valid, usually referred
     * to as extrapolation.
     * @param result the vector to store the result in.
     */
    public static final void vlerp3(float[] result, float[] a, float[] b,
            float s)
    {
        result[0] = a[0] * (1 - s) + b[0] * s;
        result[1] = a[1] * (1 - s) + b[1] * s;
        result[2] = a[2] * (1 - s) + b[2] * s;
    }

    /**
     * <p>Loads a 3D vector with the individual components {@code x, y, z}.
     * 
     * @param result the vector to store the components in.
     * @param x the x component, stored at index 0.
     * @param y the y component, stored at index 1.
     * @param z the z component, stored at index 2.
     */
    public static final void vload3(float[] result, float x, float y, float z)
    {
        result[0] = x;
        result[1] = y;
        result[2] = z;
    }

    /**
     * <p>Loads a 4D vector with the individual components {@code x, y, z, w}.
     *
     * @param result the vector to store the components in.
     * @param x the x component, stored at index 0.
     * @param y the y component, stored at index 1.
     * @param z the z component, stored at index 2.
     * @param w the w component, stored at index 3.
     */
    public static final void vload4(float[] result, float x, float y, float z, float w)
    {
        result[0] = x;
        result[1] = y;
        result[2] = z;
        result[3] = w;
    }
    
    /**
     * <p>Calculates the magnitude of the 3D vector {@code a}.</p>
     * 
     * @param a the vector to get the length of.
     * @return the vector mangnitude, the scalar length.
     */
    public static final float vmag3(float[] a)
    {
        return (float) Math.sqrt(vmagSqr3(a));
    }

    /**
     * <p>Calculates the magnitude of the 3D components {@code x, y, z}.</p>
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return the vector mangnitude, the scalar length.
     */
    public static final float vmag3(float x, float y, float z)
    {
        return (float) Math.sqrt(vmagSqr3(x, y, z));
    }

    /**
     * <p>Calculates the magnitude of the 4D vector {@code a}.</p>
     *
     * @param a the vector to get the length of.
     * @return the vector mangnitude, the scalar length.
     */
    public static final float vmag4(float[] a)
    {
        return (float) Math.sqrt(vmagSqr4(a));
    }

    /**
     * <p>Calculates the squared magnitude of the 3D vector {@code a}.</p>
     *
     * @param a the vector to get the squared length of.
     * @return the vector mangnitude squared, the scalar length squared.
     */
    public static final float vmagSqr3(float[] a)
    {
        return vmagSqr3(a[0], a[1], a[2]);
    }

    /**
     * <p>Calculates the squared magnitude of the 3D components {@code x, y, z}.</p>
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return the vector mangnitude squared, the scalar length squared.
     */
    public static final float vmagSqr3(float x, float y, float z)
    {
        return x * x + y * y + z * z;
    }

    /**
     * <p>Calculates the squared magnitude of the 4D vector {@code a}.</p>
     *
     * @param a the vector to get the squared length of.
     * @return the vector mangnitude squared, the scalar length squared.
     */
    public static final float vmagSqr4(float[] a)
    {
        final float x = a[0];
        final float y = a[1];
        final float z = a[2];
        final float w = a[3];
        return x * x + y * y + z * z + w * w;
    }
    
    /**
     * <p>Copies the data from the 3D vector {@code a} storing it in
     * the 3D vector {@code result}.</p>
     *
     * <p>This is equivalent to the = operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using {@code a} as the result
     * vector is stable and will work as expected. Though in this case it will
     * be and expensive no-op.</p>
     *
     * @param a the left operand, vector.
     * @param result the vector to store the result in.
     */
    public static final void vmov3(float[] result, float[] a)
    {
        result[0] = a[0];
        result[1] = a[1];
        result[2] = a[2];
    }

    /**
     * <p>Copies the data from the 4D vector {@code a} storing it in
     * the 4D vector {@code result}.</p>
     *
     * <p>This is equivalent to the = operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using {@code a} as the result
     * vector is stable and will work as expected. Though in this case it will
     * be and expensive no-op.</p>
     *
     * @param a the left operand, vector.
     * @param result the vector to store the result in.
     */
    public static final void vmov4(float[] result, float[] a)
    {
        result[0] = a[0];
        result[1] = a[1];
        result[2] = a[2];
        result[3] = a[3];
    }

    /**
     * <p>Multiplies the 3D vector {@code a} by the scalar {@code b},
     * storing the result back in {@code a}.</p>
     *
     * <p>This is equivalent to the *= operator on a vector</p>
     *
     * @param a the left operand and result, vector.
     * @param b the right operand, scalar.
     */
    public static final void vmul3(float[] a, float b)
    {
        vmul3(a, a, b);
    }
    
    /**
     * <p>Multiplies the 3D vector {@code a} by the scalar {@code b},
     * storing the result in the 3D vector {@code result}.</p>
     *
     * <p>This is equivalent to the * operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using {@code a} as the result
     * vector is stable and will work as expected.</p>
     *
     * @param a the left operand, vector.
     * @param b the right operand, scalar.
     * @param result the vector to store the result in.
     */
    public static final void vmul3(float[] result, float[] a, float b)
    {
        result[0] = a[0] * b;
        result[1] = a[1] * b;
        result[2] = a[2] * b;
    }

    /**
     * <p>Multiplies the 4D vector {@code a} by the scalar {@code b},
     * storing the result back in {@code a}.</p>
     *
     * <p>This is equivalent to the *= operator on a vector</p>
     *
     * @param a the left operand and result, vector.
     * @param b the right operand, scalar.
     */
    public static final void vmul4(float[] a, float b)
    {
        vmul4(a, a, b);
    }
    
    /**
     * <p>Multiplies the 4D vector {@code a} by the scalar {@code b},
     * storing the result in the 4D vector {@code result}.</p>
     *
     * <p>This is equivalent to the * operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using {@code a} as the result
     * vector is stable and will work as expected.</p>
     *
     * @param a the left operand, vector.
     * @param b the right operand, scalar.
     * @param result the vector to store the result in.
     */
    public static final void vmul4(float[] result, float[] a, float b)
    {
        result[0] = a[0] * b;
        result[1] = a[1] * b;
        result[2] = a[2] * b;
    }

    /**
     * <p>Normalizes the 3D vector, {@code vector}, storing the result in another
     * vector, {@code result}.</p>
     * 
     * <p>Note: the algorithm guarantees that using {@code vector} as the result
     * vector is stable and will work as expected.</p>
     * 
     * @param result the vector to store the result in.
     * @param a the vector to normalize.
     */
    public static final void vnormalize3(float[] result, float[] a)
    {
        final float invmag = 1.0f / vmag3(a);
        vmul3(result, a, invmag);
    }

    /**
     * <p>Normalizes the 3D vector, {@code vector}, storing the result back in
     * itself.</p>
     *
     * @param result the vector to normalize and store the result in.
     */
    public static final void vnormalize3(float[] result)
    {
        final float invmag = 1.0f / vmag3(result);
        vmul3(result, invmag);
    }
    
    /**
     * <p>Subtracts the 3D vector {@code b} from the 3D vector {@code a},
     * storing the result in the 3D vector {@code result}.</p>
     *
     * <p>This is equivalent to the - operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using either {@code a} or
     * {@code b} as the result vector is stable and will work as expected.</p>
     *
     * @param result the vector to store the result in.
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     */
    public static final void vsub3(float[] result, float[] a, float[] b)
    {
        result[0] = a[0] - b[0];
        result[1] = a[1] - b[1];
        result[2] = a[2] - b[2];
    }

    /**
     * <p>Subtracts the 3D vector {@code b} from the 3D vector {@code a},
     * storing the result back in {@code a}.</p>
     *
     * <p>This is equivalent to the -= operator on two vectors</p>
     *
     * @param a the left operand and result vector.
     * @param b the right operand, vector.
     */
    public static final void vsub3(float[] a, float[] b)
    {
        vsub3(a, a, b);
    }

    /**
     * <p>Subtracts the 4D vector {@code b} from the 4D vector {@code a},
     * storing the result in the 4D vector {@code result}.</p>
     *
     * <p>This is equivalent to the - operator on two vectors</p>
     *
     * <p>Note: the algorithm guarantees that using either {@code a} or
     * {@code b} as the result vector is stable and will work as expected.</p>
     *
     * @param result the vector to store the result in.
     * @param a the left operand, vector.
     * @param b the right operand, vector.
     */
    public static final void vsub4(float[] result, float[] a, float[] b)
    {
        result[0] = a[0] - b[0];
        result[1] = a[1] - b[1];
        result[2] = a[2] - b[2];
        result[3] = a[3] - b[3];
    }

    /**
     * <p>Subtracts the 4D vector {@code b} from the 4D vector {@code a},
     * storing the result back in {@code a}.</p>
     *
     * <p>This is equivalent to the -= operator on two vectors</p>
     *
     * @param a the left operand and result vector.
     * @param b the right operand, vector.
     */
    public static final void vsub4(float[] a, float[] b)
    {
        vsub4(a, a, b);
    }
}
