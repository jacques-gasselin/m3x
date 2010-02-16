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
public class CompositingModeTest extends AbstractTestCase
{
    private CompositingMode cm;

    @Override
    public void setUp()
    {
        cm = new CompositingMode();
    }

    @Override
    public void tearDown()
    {
        cm = null;
    }

    public void testDuplicate()
    {
        cm.setAlphaTest(CompositingMode.EQUAL);
        cm.setAlphaThreshold(0.128f);
        
        final Object3D dup = cm.duplicate();
        assertNotNull(dup);
        assertTrue(cm.getClass().isInstance(dup));
        final CompositingMode cmDup = (CompositingMode) dup;
        assertEquals(cm.getAlphaTest(), cmDup.getAlphaTest());
        assertEquals(cm.getAlphaThreshold(), cmDup.getAlphaThreshold(), 0.001f);
    }

    public void testGetAlphaTest()
    {
        assertEquals(CompositingMode.GEQUAL, cm.getAlphaTest());
    }

    public void testGetAlphaThreshold()
    {
        assertEquals(0, cm.getAlphaThreshold(), 0.001f);
    }

    public void testGetBlender()
    {
        assertNull(cm.getBlender());
    }

    public void testGetBlending()
    {
        assertEquals(CompositingMode.REPLACE, cm.getBlending());
    }

    public void testGetColorWriteMask()
    {
        assertEquals(-1, cm.getColorWriteMask());
    }

    public void testGetDepthOffsetFactor()
    {
        assertEquals(0, cm.getDepthOffsetFactor(), 0.001f);
    }

    public void testGetDepthOffsetUnits()
    {
        assertEquals(0, cm.getDepthOffsetUnits(), 0.001f);
    }

    public void testGetDepthTest()
    {
        assertEquals(CompositingMode.LEQUAL, cm.getDepthTest());
    }

    public void testGetStencil()
    {
        assertNull(cm.getStencil());
    }

    @Deprecated
    public void testIsAlphaWriteEnabled()
    {
        assertTrue(cm.isAlphaWriteEnabled());
    }
}
