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

import java.util.IdentityHashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 * @author jgasseli
 */
public class Object3DTest extends TestCase
{
    public void testGetAnimationTrackCount()
    {
        Group g = new Group();
        assertEquals("track count should default to 0",
                0, g.getAnimationTrackCount());
    }

    public void testGetUserID()
    {
        Group g = new Group();
        assertEquals("userID should default to 0",
                0, g.getUserID());
    }

    public void testGetUserObject()
    {
        Group g = new Group();
        assertNull("userID should default to null",
                g.getUserObject());
    }

    public void testIsAnimationEnabled()
    {
        Group g = new Group();
        assertTrue("animation should default to enabled",
                g.isAnimationEnabled());
    }

    public void testSetUserID()
    {
        Group g = new Group();
        final int userID = 123;
        g.setUserID(userID);
        assertEquals(userID, g.getUserID());
    }

    public void testFindSimple()
    {
        Group g1 = new Group();
        g1.setUserID(1);

        Group g2 = new Group();
        g2.setUserID(2);

        Group g3 = new Group();
        g3.setUserID(3);

        g1.addChild(g2);
        g1.addChild(g3);

        assertNull(g1.find(0));
        //ensure find does go down the hierachy
        assertSame(g1, g1.find(1));
        assertSame(g2, g1.find(2));
        assertSame(g3, g1.find(3));
        //ensure find does not go up the hierachy
        assertNull(g2.find(1));
        assertNull(g3.find(1));
        assertNull(g2.find(3));
        assertNull(g3.find(2));
    }

    public void testFindAllSimple()
    {
        Group g1 = new Group();
        Group g2 = new Group();
        Group g3 = new Group();

        g1.addChild(g2);
        g1.addChild(g3);

        final Object3D[] objects = g1.findAll(Group.class);
        assertNotNull(objects);
        assertEquals(3, objects.length);
        //no guarantee about the object order, but check for containment
        final Map<Object3D, Object3D> map = new IdentityHashMap<Object3D, Object3D>();
        for (Object3D obj : objects)
        {
            assertNull("duplicate elements not allowed in this case",
                    map.put(obj, obj));
        }

        assertTrue(map.containsKey(g1));
        assertTrue(map.containsKey(g2));
        assertTrue(map.containsKey(g3));
    }
}
