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
public class BackgroundTest extends AbstractTestCase
{
    private Background b;

    @Override
    protected void setUp() throws Exception
    {
        b = new Background();
    }

    @Override
    protected void tearDown()
    {
        b = null;
    }

    public void testGetColor()
    {
        assertEquals(0, b.getColor());
    }

    public void testGetColorClearMask()
    {
        assertEquals(-1, b.getColorClearMask());
    }

    public void testGetCropHeight()
    {
        assertEquals(0, b.getCropHeight());
    }

    public void testGetCropWidth()
    {
        assertEquals(0, b.getCropWidth());
    }

    public void testGetCropX()
    {
        assertEquals(0, b.getCropX());
    }

    public void testGetCropY()
    {
        assertEquals(0, b.getCropY());
    }

    public void testGetDepth()
    {
        assertEquals(1.0f, b.getDepth(), 0.001f);
    }

    public void testGetImage()
    {
        assertNull(b.getImage());
    }

    public void testGetImageModeX()
    {
        assertEquals(Background.BORDER, b.getImageModeX());
    }

    public void testGetImageModeY()
    {
        assertEquals(Background.BORDER, b.getImageModeY());
    }

    public void testGetReferences()
    {
        assertEquals(0, b.getReferences((Object3D[])null));
    }

    public void testGetStencil()
    {
        assertEquals(0, b.getStencil());
    }

    public void testGetStencilClearMask()
    {
        assertEquals(-1, b.getStencilClearMask());
    }

    public void testIsColorClearEnabled()
    {
        assertTrue(b.isColorClearEnabled());
    }

    public void testIsDepthClearEnabled()
    {
        assertTrue(b.isDepthClearEnabled());
    }
}
