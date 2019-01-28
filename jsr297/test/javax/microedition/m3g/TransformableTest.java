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

package javax.microedition.m3g;

import m3x.AbstractTestCase;

/**
 * @author jgasseli
 */
public class TransformableTest extends AbstractTestCase
{
    private Group g;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        g = new Group();
    }

    public void testGetCompositeTransform()
    {
        final Transform t = new Transform();
        g.getCompositeTransform(t);

        Transform expected = new Transform();

        assertCompositeTransformEquals(expected, g);
    }

    public void testGetCompositeTransformNull()
    {
        try
        {
            g.getCompositeTransform(null);
            fail("null transform must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    private void getOrientation(float[] v)
    {
        try
        {
            g.getOrientation(v);
            assertNotNull("null angleAxis must throw NPE", v);
            assertTrue("length < 4 must throw IAE",
                    v.length >= 4);
        }
        catch (NullPointerException e)
        {
            assertNull(v);
            assertNotNull(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(v);
            assertFalse("length >= 4 must not throw IAE",
                    v.length >= 4);
            assertNotNull(e.getMessage());
        }
    }

    private final void getOrientationQuat(float[] v)
    {
        try
        {
            g.getOrientationQuat(v);
            assertNotNull("null quat must throw NPE", v);
            assertTrue("length < 4 must throw IAE",
                    v.length >= 4);
        }
        catch (NullPointerException e)
        {
            assertNull(v);
            assertNotNull(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(v);
            assertFalse("length >= 4 must not throw IAE",
                    v.length >= 4);
            assertNotNull(e.getMessage());
        }
    }
    
    public void testGetOrientation()
    {
        getOrientation(new float[4]);
    }

    public void testGetOrientationNull()
    {
        getOrientation(null);
    }

    public void testGetOrientationZeroElements()
    {
        getOrientation(new float[0]);
    }

    public void testGetOrientationOneElement()
    {
        getOrientation(new float[1]);
    }

    public void testGetOrientationTwoElements()
    {
        getOrientation(new float[2]);
    }

    public void testGetOrientationThreeElements()
    {
        getOrientation(new float[3]);
    }

    public void testGetOrientationFiveElements()
    {
        getOrientation(new float[5]);
    }

    public void testGetOrientationQuat()
    {
        final float[] quat = new float[4];
        g.getOrientationQuat(quat);
    }

    public void testGetOrientationQuatNull()
    {
        getOrientationQuat(null);
    }

    public void testGetOrientationQuatZeroElements()
    {
        getOrientationQuat(new float[0]);
    }

    public void testGetOrientationQuatOneElement()
    {
        getOrientationQuat(new float[1]);
    }

    public void testGetOrientationQuatTwoElements()
    {
        getOrientationQuat(new float[2]);
    }

    public void testGetOrientationQuatThreeElements()
    {
        getOrientationQuat(new float[3]);
    }

    public void testGetOrientationQuatFiveElements()
    {
        getOrientationQuat(new float[5]);
    }

    public void testPostRotateZero()
    {
        Transform expected = new Transform();

        assertCompositeTransformEquals(expected, g);
    }

    public void testPostRotateSmall()
    {
        final float angle = 15.0f;
        g.postRotate(angle, 0, 1, 0);
        
        Transform expected = new Transform();
        expected.postRotate(angle, 0, 1, 0);

        assertCompositeTransformEquals(expected, g);
    }

    public void testPostRotateSmallNonNormal()
    {
        final float angle = 15.0f;
        g.postRotate(angle, 1, 2, 3);

        Transform expected = new Transform();
        expected.postRotate(angle, 1, 2, 3);

        assertCompositeTransformEquals(expected, g);
    }

    public void testPostRotateMultiple()
    {
        final float angle1 = 15.0f;
        final float angle2 = -66.3f;
        final float angle3 = -15.0f;
        g.postRotate(angle1, 1, 2, 3);
        g.postRotate(angle2, 0, 0, 1);
        g.postRotate(angle3, 1, 0, 1);

        Transform expected = new Transform();
        expected.postRotate(angle1, 1, 2, 3);
        expected.postRotate(angle2, 0, 0, 1);
        expected.postRotate(angle3, 1, 0, 1);

        assertCompositeTransformEquals(expected, g);
    }

    public void testPreRotateSmall()
    {
        final float angle = 15.0f;
        g.preRotate(angle, 0, 1, 0);

        Transform expected = new Transform();
        expected.postRotate(angle, 0, 1, 0);

        assertCompositeTransformEquals(expected, g);
    }

    public void testPreRotateSmallNonNormal()
    {
        final float angle = 15.0f;
        g.preRotate(angle, 1, 2, 3);

        Transform expected = new Transform();
        expected.postRotate(angle, 1, 2, 3);

        assertCompositeTransformEquals(expected, g);
    }

    public void testPreRotateMultiple()
    {
        final float angle1 = 15.0f;
        final float angle2 = -66.3f;
        final float angle3 = -15.0f;
        g.preRotate(angle1, 1, 2, 3);
        g.preRotate(angle2, 0, 0, 1);
        g.preRotate(angle3, 1, 0, 1);

        Transform expected = new Transform();
        expected.preRotate(angle1, 1, 2, 3);
        expected.preRotate(angle2, 0, 0, 1);
        expected.preRotate(angle3, 1, 0, 1);

        assertCompositeTransformEquals(expected, g);
    }
}
