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

import junit.framework.AssertionFailedError;
import m3x.AbstractTestCase;

/**
 *
 * @author jgasseli
 */
public class TransformTest extends AbstractTestCase
{
    private Transform trans;

    public TransformTest()
    {
    }

    @Override
    public void setUp() throws Exception
    {
        trans = new Transform();
    }

    @Override
    public void tearDown()
    {
        trans = null;
    }

    public void testNew()
    {
        try
        {
            Transform t = new Transform();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail("default constructor must not throw");
        }
    }

    private static final float[] IDENTITY = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    };

    public void testAdd()
    {
        final float[] mat = {
            1, 2, 3, 4,
            5, 6, 7, 8,
            9, 10, 11, 12,
            13, 14, 15, 16,
        };

        Transform t = new Transform();
        t.set(mat);

        trans.add(t);

        final float[] expected = {
            2, 2, 3, 4,
            5, 7, 7, 8,
            9, 10, 12, 12,
            13, 14, 15, 17,
        };

        assertEquals(expected, trans);
    }

    public void testAddNull()
    {
        try
        {
            trans.add(null);
            fail("null transform must throw NPE");
        }
        catch (NullPointerException e)
        {
            //correct
        }
    }

    public void testGet()
    {
        //length >= 16
        try
        {
            float[] mat = new float[22];
            trans.get(mat);
        }
        catch (Throwable t)
        {
            fail(t.toString());
        }

        //check result
        //a new transform is expected to be identity
        assertEquals(IDENTITY, trans);
    }

    public void testGetNull()
    {
        //test NPE handling
        try
        {
            trans.get(null);
            fail();
        }
        catch (NullPointerException e)
        {
            //correct
        }
        catch (AssertionFailedError e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            fail(t.toString());
        }
    }

    public void testGetTooShort()
    {
        //test length handling
        //< 16
        try
        {
            float[] mat = new float[2];
            trans.get(mat);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
        catch (AssertionFailedError e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            fail(t.toString());
        }
    }

    public void testSetIdentity()
    {
        trans.postScale(2, 3, 4);
        trans.setIdentity();

        assertEquals(IDENTITY, trans);
    }

    public void testSetMatrix()
    {
        //test NPE handling
        try
        {
            trans.set((float[])null);
            fail();
        }
        catch (NullPointerException e)
        {
            //correct
        }
        catch (Throwable t)
        {
            fail(t.toString());
        }

        float[] expected = new float[16];
        for (int i = 0; i < 16; ++i)
        {
            expected[i] = i;
        }
        trans.set(expected);
        //elements should have retained row-major order
        assertEquals(expected, trans);
    }

    public void testPostMultiplyNull()
    {
        try
        {
            trans.postMultiply(null);
            fail("must throw NPE");
        }
        catch (NullPointerException e)
        {
            //correct
        }
    }

    public void testPostMultiply1()
    {
        trans.postTranslate(1, 2, 3);
        Transform t2 = new Transform();
        t2.postScale(1, 2, 3);
        trans.postMultiply(t2);

        float[] expected = {
            1, 0, 0, 1,
            0, 2, 0, 2,
            0, 0, 3, 3,
            0, 0, 0, 1
        };

        assertEquals(expected, trans);
    }

    public void testTranslate1()
    {
        trans.postTranslate(1, 2, 3);

        float[] expected = {
            1, 0, 0, 1,
            0, 1, 0, 2,
            0, 0, 1, 3,
            0, 0, 0, 1
        };
        
        assertEquals(expected, trans);
    }

    public void testTranslate2()
    {
        trans.postTranslate(1, 2, 3);
        trans.postTranslate(3, 2, 1);

        float[] expected = {
            1, 0, 0, 4,
            0, 1, 0, 4,
            0, 0, 1, 4,
            0, 0, 0, 1
        };
        
        assertEquals(expected, trans);
    }

    public void testTranslate3()
    {
        trans.postTranslate(1, 2, 3);
        trans.postTranslate(3, 2, 1);
        trans.postTranslate(-4, -4, -4);

        float[] expected = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };
        
        assertEquals(expected, trans);
    }

    public void testTranspose1()
    {
        trans.postTranslate(1, 2, 3);
        trans.transpose();

        float[] expected = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            1, 2, 3, 1
        };

        assertEquals(expected, trans);
    }

    public void testTranspose2()
    {
        trans.postScale(1, 2, 3);
        trans.transpose();

        float[] expected = {
            1, 0, 0, 0,
            0, 2, 0, 0,
            0, 0, 3, 0,
            0, 0, 0, 1
        };

        assertEquals(expected, trans);
    }

    public void testScale1()
    {
        trans.postScale(1, 2, 3);

        float[] expected = {
            1, 0, 0, 0,
            0, 2, 0, 0,
            0, 0, 3, 0,
            0, 0, 0, 1
        };
        
        assertEquals(expected, trans);
    }

    public void testScale2()
    {
        trans.postScale(1, 2, 3);
        trans.postScale(3, 2, 1);

        float[] expected = {
            3, 0, 0, 0,
            0, 4, 0, 0,
            0, 0, 3, 0,
            0, 0, 0, 1
        };
        
        assertEquals(expected, trans);
    }

    public void testTranslateScale1()
    {
        trans.postTranslate(1, 2, 3);
        trans.postScale(1, 2, 3);

        float[] expected = {
            1, 0, 0, 1,
            0, 2, 0, 2,
            0, 0, 3, 3,
            0, 0, 0, 1
        };

        assertEquals(expected, trans);
    }

    public void testRotateIllegal()
    {
        try
        {
            trans.postRotate(1, 0, 0, 0);
            fail("must throw IEA as angle is non-zero and axis is zero");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testRotate1()
    {
        trans.postRotate(10, 0, 1, 0);

        float[] expected = {
            0.9848078f,    0,    0.1736482f,   0,
            0,             1,    0,            0,
           -0.1736482f,    0,    0.9848078f,   0,
            0,             0,    0,            1
        };

        assertEquals(expected, trans);
    }

    public void testRotate1Normalized()
    {
        trans.postRotate(10, 0, 2, 0);

        float[] expected = {
            0.9848078f,    0,    0.1736482f,   0,
            0,             1,    0,            0,
           -0.1736482f,    0,    0.9848078f,   0,
            0,             0,    0,            1
        };

        assertEquals(expected, trans);
    }

    public void testRotate2()
    {
        trans.postRotate(25, 1, 1, 1);

        float[] expected = {
            0.9375385f,   -0.2127680f,    0.2752295f,    0,
            0.2752295f,    0.9375385f,   -0.2127680f,    0,
           -0.2127680f,    0.2752295f,    0.9375385f,    0,
            0,             0,             0,             1
        };

        assertEquals(expected, trans);
    }
    
    public void testInvertTranslate1()
    {
        trans.postTranslate(1, 2, 3);
        trans.invert();

        final float[] expected = {
            1, 0, 0, -1,
            0, 1, 0, -2,
            0, 0, 1, -3,
            0, 0, 0,  1
        };

        assertEquals(expected, trans);
    }

    public void testInvertTranslate2()
    {
        trans.postTranslate(4, 0, 3);
        trans.invert();

        float[] expected = {
            1, 0, 0, -4,
            0, 1, 0,  0,
            0, 0, 1, -3,
            0, 0, 0,  1
        };

        assertEquals(expected, trans);
    }

    public void testInvertTranslateScale1()
    {
        trans.postTranslate(3, 2, 1);
        trans.postScale(2, 1, 3);
        trans.invert();

        float[] expected = {
            0.5f, 0,        0, -1.5f,
            0,    1,        0, -2,
            0,    0, 1 / 3.0f, -1 / 3.0f,
            0,    0,        0,  1
        };

        assertEquals(expected, trans);
    }

    public void testInvertTranslateScale2()
    {
        trans.postTranslate(3, 2, 1);
        trans.postScale(2, 3, 1);
        trans.invert();

        float[] expected = {
            0.5f, 0,        0, -1.5f,
            0,    1 / 3.0f, 0, -2 / 3.0f,
            0,    0,        1, -1,
            0,    0,        0,  1
        };

        assertEquals(expected, trans);
    }

    public void testInvertTranslateScale3()
    {
        Transform expected = new Transform();
        expected.postScale(1 / 2.0f, 1 / 3.0f, 1);
        expected.postTranslate(-3, -2, -1);

        Transform actual = trans;
        actual.postTranslate(3, 2, 1);
        actual.postScale(2, 3, 1);
        actual.invert();

        assertEquals(expected, actual);
    }

    public void testInvertMultiple1()
    {
        Transform expected = new Transform();
        expected.postScale(1 / 2.0f, 1 / 3.0f, 1);
        expected.postTranslate(-3, -2, -1);
        expected.postRotate(-30, 0, 1, 0);

        Transform actual = trans;
        actual.postRotate(30, 0, 1, 0);
        actual.postTranslate(3, 2, 1);
        actual.postScale(2, 3, 1);
        actual.invert();

        assertEquals(expected, actual);
    }

    public void testInvertMultiple2()
    {
        Transform expected = new Transform();
        expected.postScale(1 / 4.0f, 1 / 3.0f, 1);
        expected.postTranslate(-3, -2, -1);
        expected.postRotate(-30, 0, 1, 0);
        expected.postScale(4.0f, 3.0f, 1);

        Transform actual = trans;
        actual.postScale(1 / 4.0f, 1 / 3.0f, 1);
        actual.postRotate(30, 0, 1, 0);
        actual.postTranslate(3, 2, 1);
        actual.postScale(4, 3, 1);
        actual.invert();

        assertEquals(expected, actual);
    }
}