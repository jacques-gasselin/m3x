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
 * @author jgasseli
 */
public class AppearanceTest extends AbstractTestCase
{
    private Appearance a;

    @Override
    public void setUp()
    {
        a = new Appearance();
    }

    @Override
    public void tearDown()
    {
        a = null;
    }

    public void testGetFog()
    {
        assertNull(a.getFog());
    }

    public void testGetMaterial()
    {
        assertNull(a.getMaterial());
    }

    public void testGetPointSpriteMode()
    {
        assertNull(a.getPointSpriteMode());
    }

    public void testGetTexture()
    {
        assertNull(a.getTexture(0));
        assertNull(a.getTexture(1));
    }

    public void testGetTextureNegative()
    {
        try
        {
            a.getTexture(-1);
            fail("negative index should throw IOOBE");
        }
        catch (IndexOutOfBoundsException e)
        {
            //correct
        }
        catch (AssertionFailedError e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            throw new AssertionFailedError(t.getMessage());
        }
    }

    public void testSetFog()
    {
        final Fog f = new Fog();
        a.setFog(f);
        assertSame(f, a.getFog());
    }

    public void testSetFogNull()
    {
        final Fog f = new Fog();
        a.setFog(f);
        assertNotNull(a.getFog());
        a.setFog(null);
        assertNull(a.getFog());
    }

    public void testSetMaterial()
    {
        final Material m = new Material();
        a.setMaterial(m);
        assertSame(m, a.getMaterial());
    }

    public void testSetMaterialNull()
    {
        final Material m = new Material();
        a.setMaterial(m);
        assertNotNull(a.getMaterial());
        a.setMaterial(null);
        assertNull(a.getMaterial());
    }
}
