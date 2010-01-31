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

import junit.framework.TestCase;

/**
 * @author jgasseli
 */
public class GroupTest extends TestCase
{
    private Group g;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        g = new Group();
    }

    public void testConstructor()
    {
        assertNotNull(g);
    }

    public void testAddChild()
    {
        final Group child = new Group();
        g.addChild(child);
        assertEquals(1, g.getChildCount());
        assertSame(child, g.getChild(0));
    }

    public void testAddChildNull()
    {
        try
        {
            g.addChild(null);
            fail("null as child must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testAddChildThis()
    {
        try
        {
            g.addChild(g);
            fail("this as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }
    
    public void testAddChildWorld()
    {
        final World w = new World();
        
        try
        {
            g.addChild(w);
            fail("world as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testAddChildWithOtherParent()
    {
        final Group g2 = new Group();
        final Group child = new Group();
        g.addChild(child);
        
        try
        {
            g2.addChild(child);
            fail("child of another parent as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    1, g.getChildCount());
            assertEquals("group must remain invariant on error",
                    0, g2.getChildCount());
        }
    }

    public void testGetChild()
    {
        final Group child = new Group();
        g.addChild(child);
        assertEquals(1, g.getChildCount());
        assertSame(child, g.getChild(0));
    }

    public void testGetChildNegative()
    {
        final Group child = new Group();
        g.addChild(child);
        try
        {
            g.getChild(-1);
            fail("negative index must throw IOBE");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals("group must remain invariant on error",
                    1, g.getChildCount());
        }
        assertSame(child, g.getChild(0));
    }

    public void testGetChildOverflow()
    {
        final Group child = new Group();
        g.addChild(child);
        try
        {
            g.getChild(10);
            fail("overflow index must throw IOBE");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals("group must remain invariant on error",
                    1, g.getChildCount());
        }
        assertSame(child, g.getChild(0));
    }

    public void testGetChildCount()
    {
        assertEquals(0, g.getChildCount());
    }

    public void testGetLODBlendFactor()
    {
        final float factor = g.getLODBlendFactor();
        assertEquals(0.0f, factor, 0.001f);
    }

    public void testGetLODChild()
    {
        final int index = g.getLODChild();
        assertEquals(-1, index);
    }

    public void testGetLODHysteresis()
    {
        final float hysteresis = g.getLODHysteresis();
        assertEquals(0.0f, hysteresis, 0.001f);
    }

    public void testGetLODOffset()
    {
        final float offset = g.getLODOffset();
        assertEquals(0.0f, offset, 0.001f);
    }

    public void testInsertChild()
    {
        final Group child = new Group();
        g.insertChild(child, 0);
        assertEquals(1, g.getChildCount());
        assertSame(child, g.getChild(0));
    }

    public void testInsertChildNegative()
    {
        final Group child = new Group();
        try
        {
            g.insertChild(child, -1);
            fail("negative index must throw IOBE");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testInsertChildOverflow()
    {
        final Group child = new Group();
        try
        {
            g.insertChild(child, 10);
            fail("overflow index must throw IOBE");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }
    
    public void testInsertChildFirst()
    {
        final Group first = new Group();
        g.addChild(first);
        final Group child = new Group();
        g.insertChild(child, 0);
        assertEquals(2, g.getChildCount());
        assertSame(child, g.getChild(0));
        assertSame(first, g.getChild(1));
    }

    public void testInsertChildLast()
    {
        final Group first = new Group();
        g.addChild(first);
        final Group child = new Group();
        g.insertChild(child, 1);
        assertEquals(2, g.getChildCount());
        assertSame(first, g.getChild(0));
        assertSame(child, g.getChild(1));
    }


    public void testInsertChildNull()
    {
        try
        {
            g.insertChild(null, 0);
            fail("null as child must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testInsertChildThis()
    {
        try
        {
            g.insertChild(g, 0);
            fail("this as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testInsertChildWorld()
    {
        final World w = new World();

        try
        {
            g.insertChild(w, 0);
            fail("world as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getChildCount());
        }
    }

    public void testInsertChildWithOtherParent()
    {
        final Group g2 = new Group();
        final Group child = new Group();
        g.addChild(child);

        try
        {
            g2.insertChild(child, 0);
            fail("child of another parent as child must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    1, g.getChildCount());
            assertEquals("group must remain invariant on error",
                    0, g2.getChildCount());
        }
    }

    public void testIsLODEnabled()
    {
        assertFalse(g.isLodEnabled());
    }

    public void testPickCameraNull()
    {
        try
        {
            g.pick(g.getScope(), 0, 0, null, null);
            fail("null camera should throw NPE");
        }
        catch (NullPointerException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testPickRayOneDx()
    {
        try
        {
            g.pick(g.getScope(), 0, 0, 0, 1, 0, 0, null);
        }
        catch (IllegalArgumentException e)
        {
            fail("dx = 1, dy = dz = 0 should not throw IAE");
        }
    }

    public void testPickRayOneDy()
    {
        try
        {
            g.pick(g.getScope(), 0, 0, 0, 0, 1, 0, null);
        }
        catch (IllegalArgumentException e)
        {
            fail("dx = 0, dy = 1 and dz = 0 should not throw IAE");
        }
    }

    public void testPickRayOneDz()
    {
        try
        {
            g.pick(g.getScope(), 0, 0, 0, 0, 0, 1, null);
        }
        catch (IllegalArgumentException e)
        {
            fail("dx = dy = 0 and dz = 1 should not throw IAE");
        }
    }

    public void testPickRayZero()
    {
        try
        {
            g.pick(g.getScope(), 0, 0, 0, 0, 0, 0, null);
            fail("dx = dy = dz = 0 should throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }
    
    public void testRemoveChild()
    {
        final Group child = new Group();
        g.addChild(child);
        g.removeChild(child);
        assertEquals(0, g.getChildCount());
    }

    public void testRemoveChildNull()
    {
        final Group child = new Group();
        g.addChild(child);
        g.removeChild(null);
        assertEquals(1, g.getChildCount());
        assertSame(child, g.getChild(0));
    }

    public void testSetLODEnable()
    {
        final float hysteresis = 0.1f;
        g.setLODEnable(true, hysteresis);
        assertTrue(g.isLodEnabled());
        assertEquals(hysteresis, g.getLODHysteresis(), 0.001f);
    }

    public void testSetLODEnableNegative()
    {
        assertEquals(0, g.getLODHysteresis(), 0.001f);
        assertFalse(g.isLodEnabled());
        try
        {
            final float hysteresis = -0.1f;
            g.setLODEnable(true, hysteresis);
            fail("negative hysteresis must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("group must remain invariant on error",
                    0, g.getLODHysteresis(), 0.001f);
            assertFalse("group must remain invariant on error",
                    g.isLodEnabled());
        }
    }

    public void testSetLODOffset()
    {
        final float offset = 2.0f;
        g.setLODOffset(offset);
        assertEquals(offset, g.getLODOffset(), 0.001f);
    }
}
