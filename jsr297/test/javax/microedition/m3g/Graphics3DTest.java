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
 * @author jgasseli
 */
public class Graphics3DTest extends TestCase
{
    private Graphics3D g3d;

    @Override
    public void setUp()
    {
        g3d = Graphics3D.getInstance();
    }

    @Override
    public void tearDown()
    {
        g3d.resetLights();
    }
    
    public void testGetInstance()
    {
        assertNotNull(g3d);
    }

    public void testGetCameraNull()
    {
        assertNull(g3d.getCamera(null));
    }

    public void testGetCamera()
    {
        Transform t = new Transform();
        assertNull(g3d.getCamera(t));
    }

    public void testSetCameraNullNull()
    {
        g3d.setCamera(null, null);
    }

    public void testSetCamera1stNull()
    {
        Transform t = new Transform();
        g3d.setCamera(null, t);
    }

    public void testSetCamera2ndNull()
    {
        Camera c = new Camera();
        g3d.setCamera(c, null);
    }

    public void testSetCameraNonIvertibleZero()
    {
        Camera c = new Camera();
        Transform t = new Transform();
        float[] m = {
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0 };
        t.set(m);
        try
        {
            g3d.setCamera(c, t);
            fail("must throw AE");
        }
        catch (ArithmeticException e)
        {
            //correct
        }
    }

    public void testSetCamera()
    {
        Camera c = new Camera();
        Transform t = new Transform();
        g3d.setCamera(c, t);
        assertEquals(c, g3d.getCamera(t));
    }

    public void testAddLightNull()
    {
        try
        {
            g3d.addLight(null, null);
            fail("must throw NPE");
        }
        catch (NullPointerException e)
        {
            //correct
        }
    }

    public void testAddLight2ndNull()
    {
        Light l = new Light();
        g3d.addLight(l, null);
    }

    public void testAddLight()
    {
        Light l = new Light();
        Transform t = new Transform();
        g3d.addLight(l, t);
    }

    public void testGetLightEmpty()
    {
        assertEquals(0, g3d.getLightCount());

        try
        {
            g3d.getLight(0, null);
            fail("most throw IOOBE when empty");
        }
        catch (IndexOutOfBoundsException e)
        {
            //correct
        }
    }

    public void testGetLightNegative()
    {
        g3d.addLight(new Light(), null);
        assertEquals(1, g3d.getLightCount());

        try
        {
            g3d.getLight(-1, null);
            fail("most throw IOOBE when index < 0");
        }
        catch (IndexOutOfBoundsException e)
        {
            //correct
        }
    }

    public void testGetLightOverflow()
    {
        g3d.addLight(new Light(), null);
        assertEquals(1, g3d.getLightCount());
        
        try
        {
            g3d.getLight(1, null);
            fail("most throw IOOBE when index >= getLightCount()");
        }
        catch (IndexOutOfBoundsException e)
        {
            //correct
        }
    }

    public void testGetLight()
    {
        Light l = new Light();
        Transform t = new Transform();
        g3d.addLight(l, t);

        assertEquals(1, g3d.getLightCount());
        l = g3d.getLight(0, t);
    }
}
