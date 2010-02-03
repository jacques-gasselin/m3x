/*
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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
 *
 * @author jgasseli
 */
public class TransformTest extends AbstractTestCase
{
    public TransformTest()
    {
    }

    @Override
    public void setUp()
    {
    }

    @Override
    public void tearDown()
    {
    }

    public void testNew()
    {
        try
        {
            Transform trans = new Transform();
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

    public void testGet()
    {
        Transform trans = new Transform();
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
        catch (Throwable t)
        {
            fail(t.toString());
        }

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
        catch (Throwable t)
        {
            fail(t.toString());
        }

        //> 16
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

    public void testSetMatrix()
    {
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
        trans.postTranslate(1, 2, 3);
        trans.invert();

        float[] expected = {
            1, 0, 0, -1,
            0, 1, 0, -2,
            0, 0, 1, -3,
            0, 0, 0,  1
        };

        assertEquals(expected, trans);
    }

    public void testInvertTranslate2()
    {
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
        Transform trans = new Transform();
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
}