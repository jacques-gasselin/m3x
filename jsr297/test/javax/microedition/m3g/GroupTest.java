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
    public void testConstructor()
    {
        final Group g = new Group();
    }

    public void testAddChildNull()
    {
        final Group g = new Group();
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
        final Group g = new Group();
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
        final Group g = new Group();
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
        final Group g = new Group();
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
}
