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

import junit.framework.TestCase;

/**
 *
 * @author jgasseli
 */
public class TransformTest extends TestCase
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

    private static final void assertEquals(float[] expected, float[] result, float delta)
    {
        assertNotNull(expected);
        assertNotNull(result);
        assertEquals(expected.length, result.length);

        final int length = expected.length;
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expected[i], result[i], delta);
        }
    }

    private static final void assertEquals(float[] expected, Transform result)
    {
        assertNotNull(result);
        
        float[] mat = new float[16];
        result.get(mat);
        assertEquals(expected, mat, 0.001f);
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
}