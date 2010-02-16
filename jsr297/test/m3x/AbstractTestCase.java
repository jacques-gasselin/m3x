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

import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;
import junit.framework.AssertionFailedError;

/**
 * @author jgasseli
 */
public abstract class AbstractTestCase extends junit.framework.TestCase
{
    private static final String toString(final float[] matrix)
    {
        StringBuilder b = new StringBuilder();
        for (int j = 0; j < 4; ++j)
        {
            if (j == 0)
            {
                b.append("[");
            }
            else
            {
                b.append(" ");
            }
            
            for (int i = 0; i < 4; ++i)
            {
                b.append(" ").append(matrix[j * 4 + i]).append(",");
            }

            if (j == 3)
            {
                b.append(" ]\n");
            }
            else
            {
                b.append("\n");
            }
        }
        return b.toString();
    }

    private static final String toString(final Transform transform)
    {
        final float[] mat = new float[16];
        transform.get(mat);
        return toString(mat);
    }
    
    public static final void assertEquals(final float[] expected, final float[] actual, float delta)
    {
        assertNotSame(expected, actual);
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.length, actual.length);

        final int length = expected.length;
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    public static final void assertCompositeTransformEquals(final Transform expected,
            final Transformable actualTransformable)
    {
        assertNotNull(actualTransformable);
        
        final Transform actual = new Transform();
        actualTransformable.getCompositeTransform(actual);

        assertEquals(expected, actual);
    }

    public static final void assertEquals(final Transform expected, final Transform actual)
    {
        assertNotSame(expected, actual);
        assertNotNull(expected);
        assertNotNull(actual);

        final float[] expectedMat = new float[16];
        expected.get(expectedMat);
        
        final float[] actualMat = new float[16];
        actual.get(actualMat);

        try
        {
            assertEquals(expectedMat, actualMat, 0.001f);
        }
        catch (AssertionFailedError e)
        {
            StringBuilder b = new StringBuilder();
            b.append("expected:\n").append(toString(expected));
            b.append("actual:\n").append(toString(actual));
            throw new AssertionFailedError(b.toString());
        }
    }
    
    public static final void assertEquals(final float[] expected, final Transform actual)
    {
        assertNotNull(actual);

        float[] mat = new float[16];
        actual.get(mat);
        assertEquals(expected, mat, 0.001f);
    }

    public static final void assertIdentity(final Transform actual)
    {
        assertNotNull(actual);

        final Transform identity = new Transform();
        assertEquals(identity, actual);
    }
    
    public static final void assertInverse(final Transform original,
            final Transform inverse)
    {
        assertNotSame(original, inverse);
        assertNotNull(original);
        assertNotNull(inverse);
        
        final Transform transform = new Transform(original);
        transform.postMultiply(inverse);

        try
        {
            assertIdentity(transform);
        }
        catch (AssertionFailedError e)
        {
            StringBuilder b = new StringBuilder();
            b.append("original:\n").append(toString(original));
            b.append("multiplied by:\n").append(toString(inverse));
            b.append("is not identity, but:\n").append(toString(transform));
            throw new AssertionFailedError(b.toString());
        }
    }
}
