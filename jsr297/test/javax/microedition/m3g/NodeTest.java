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
public class NodeTest extends AbstractTestCase
{
    private Group g;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        g = new Group();
    }

    public void testAlignNull()
    {
        g.align(null);
    }

    public void testAlignSeparate()
    {
        final Group g2 = new Group();
        try
        {
            g.align(g2);
            fail("align from separate trees should throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testInSameTreeFalse()
    {
        final Group g2 = new Group();
        assertFalse(Node.inSameTree(g, g2));
    }

    public void testInSameTreeTrue()
    {
        final Group g2 = new Group();
        final Group g3 = new Group();
        g.addChild(g2);
        g.addChild(g3);
        assertTrue(Node.inSameTree(g2, g3));
    }

    public void testFindCommonAncestor()
    {
        Camera camera = new Camera();
        camera.setTranslation(2, 2, 20);
        Group child = new Group();
        child.setTranslation(10, 0, 0);
        g.addChild(camera);
        g.addChild(child);

        assertSame(g, Node.findCommonAncestor(camera, child));
    }

    public void testFindCommonAncestorImpossible()
    {
        Camera camera = new Camera();
        camera.setTranslation(2, 2, 20);
        Group child = new Group();
        child.setTranslation(10, 0, 0);

        assertNull(Node.findCommonAncestor(camera, child));
    }
    
    public void testGetAlignmentReferenceX()
    {
        try
        {
            g.getAlignmentReference(Node.X_AXIS);
            fail("getting alignment reference for X axis must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetAlignmentReferenceY()
    {
        assertNull(g.getAlignmentReference(Node.Y_AXIS));
    }

    public void testGetAlignmentReferenceZ()
    {
        assertNull(g.getAlignmentReference(Node.Z_AXIS));
    }

    public void testGetAlignmentReferenceZero()
    {
        try
        {
            g.getAlignmentReference(0);
            fail("getting alignment reference for 0 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetAlignmentReferenceZPlus1()
    {
        try
        {
            g.getAlignmentReference(Node.Z_AXIS + 1);
            fail("getting alignment reference for Z + 1 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetAlphaFactor()
    {
        assertEquals(1.0f, g.getAlphaFactor(), 0.001f);
    }

    public void testGetBoundingBoxNull()
    {
        assertFalse(g.getBoundingBox(null));
    }

    public void testGetBoundingShpereNull()
    {
        assertFalse(g.getBoundingSphere(null));
    }

    public void testGetLODResolution()
    {
        assertEquals(0.0f, g.getLODResolution(), 0.001f);
    }

    public void testGetParent()
    {
        assertNull(g.getParent());
    }

    public void testGetParentOfChild()
    {
        final Group g2 = new Group();
        g.addChild(g2);
        assertSame(g, g2.getParent());
    }

    public void testGetScope()
    {
        assertEquals(-1, g.getScope());
    }

    public void testIsCollisionEnabled()
    {
        assertTrue(g.isCollisionEnabled());
    }

    public void testIsPickingEnabled()
    {
        assertTrue(g.isPickingEnabled());
    }

    public void testIsRenderingEnabled()
    {
        assertTrue(g.isRenderingEnabled());
    }

    public void testGetTransformToParent()
    {
        Camera camera = new Camera();
        camera.setTranslation(2, 2, 20);
        g.addChild(camera);

        final Transform worldToCamera = new Transform();
        assertTrue(g.getTransformTo(camera, worldToCamera));

        final Transform cameraToWorld = new Transform();
        assertTrue(camera.getTransformTo(g, cameraToWorld));

        assertInverse(worldToCamera, cameraToWorld);

        Transform worldToCameraExpected = new Transform();
        worldToCameraExpected.postTranslate(-2, -2, -20);

        assertEquals(worldToCameraExpected, worldToCamera);

        Transform cameraToWorldExpected = new Transform();
        cameraToWorldExpected.postTranslate(2, 2, 20);

        assertEquals(cameraToWorldExpected, cameraToWorld);
    }

    public void testGetTransformToSiblings()
    {
        Camera camera = new Camera();
        camera.setTranslation(2, 2, 20);
        Group child = new Group();
        child.setTranslation(10, 0, 0);
        g.addChild(camera);
        g.addChild(child);

        final Transform childToCamera = new Transform();
        assertTrue(child.getTransformTo(camera, childToCamera));

        final Transform cameraToChild = new Transform();
        assertTrue(camera.getTransformTo(child, cameraToChild));

        assertInverse(childToCamera, cameraToChild);

        Transform childToCameraExpected = new Transform();
        childToCameraExpected.postTranslate(10, 0, 0);
        childToCameraExpected.postTranslate(-2, -2, -20);

        assertEquals(childToCameraExpected, childToCamera);

        Transform cameraToChildExpected = new Transform();
        cameraToChildExpected.postTranslate(2, 2, 20);
        cameraToChildExpected.postTranslate(-10, 0, 0);

        assertEquals(cameraToChildExpected, cameraToChild);
    }
    
    public void testGetTransformToSiblingsIndentityChild()
    {
        Camera camera = new Camera();
        camera.setTranslation(2, 2, 20);
        Group child = new Group();
        g.addChild(camera);
        g.addChild(child);

        final Transform worldToCamera = new Transform();
        assertTrue(child.getTransformTo(camera, worldToCamera));

        final Transform cameraToWorld = new Transform();
        assertTrue(camera.getTransformTo(child, cameraToWorld));

        assertInverse(worldToCamera, cameraToWorld);

        Transform worldToCameraExpected = new Transform();
        worldToCameraExpected.postTranslate(-2, -2, -20);

        assertEquals(worldToCameraExpected, worldToCamera);

        Transform cameraToWorldExpected = new Transform();
        cameraToWorldExpected.postTranslate(2, 2, 20);

        assertEquals(cameraToWorldExpected, cameraToWorld);
    }
}
