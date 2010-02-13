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

import junit.framework.TestCase;

/**
 * @author jgasseli
 */
public class CameraTest extends TestCase
{
    public void testNewCamera()
    {
        Camera c = new Camera();
    }

    public void testDefaultGetProjection()
    {
        Camera c = new Camera();
        assertEquals("Camera projection type should default to GENERIC",
                Camera.GENERIC, c.getProjection((float[])null));
    }

    public void testSetParallelZeroHeight()
    {
        Camera c = new Camera();
        try
        {
            c.setParallel(0, 1, 0, 1);
            fail("zero height must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetParallelNegativeHeight()
    {
        Camera c = new Camera();
        try
        {
            c.setParallel(-2, 1, 0, 1);
            fail("negative height must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetParallelZeroAspect()
    {
        Camera c = new Camera();
        try
        {
            c.setParallel(1, 0, 0, 1);
            fail("zero aspect must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetParallelNegativeAspect()
    {
        Camera c = new Camera();
        try
        {
            c.setParallel(1, -1, 0, 1);
            fail("negative aspect must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveZeroFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(0, 1, 1, 2);
            fail("zero fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveNegativeFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(-1, 1, 1, 2);
            fail("negative fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveNaNFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(Float.NaN, 1, 1, 2);
            fail("NaN fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspective180Fovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(180, 1, 1, 2);
            fail("180 fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveOverflowFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(200, 1, 1, 2);
            fail(">180 fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveNegativeInfFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(Float.NEGATIVE_INFINITY, 1, 1, 2);
            fail("-inf fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }

    public void testSetPerspectiveInfFovy()
    {
        Camera c = new Camera();
        try
        {
            c.setPerspective(Float.POSITIVE_INFINITY, 1, 1, 2);
            fail("+inf fovy must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //this is correct, but ensure there was a message
            assertNotNull(e.getMessage());
        }
    }
}
