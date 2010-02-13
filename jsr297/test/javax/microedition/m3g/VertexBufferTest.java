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

import junit.framework.TestCase;

/**
 * @author jgasseli
 */
public class VertexBufferTest extends TestCase
{
    private static final VertexArray createByteVA(int numVertices, int numComponents)
    {
        return new VertexArray(numVertices, numComponents, VertexArray.BYTE);
    }

    private static final void createPositions(VertexBuffer vb, int numVertices, int numComponents, float[] bias)
    {
        VertexArray pos = createByteVA(numVertices, numComponents);
        vb.setPositions(pos, 1.0f, bias);
    }

    private static final void createPositions(VertexBuffer vb, int numComponents, float[] bias)
    {
        createPositions(vb, 3, numComponents, bias);
    }

    private static final void createNormals(VertexBuffer vb, int vertexCount)
    {
        VertexArray norm = createByteVA(vertexCount, 3);
        vb.setNormals(norm);
    }

    private static final void createNormals(VertexBuffer vb)
    {
        createNormals(vb, 3);
    }

    public void testConstructor()
    {
        try
        {
            VertexBuffer vb = new VertexBuffer();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail("valid constructor call must not throw");
        }
    }

    public void testDuplicate()
    {
        final VertexBuffer vb = new VertexBuffer();
        final int defaultColor = 0xff1f3b;
        vb.setDefaultColor(defaultColor);
        final VertexArray positions = createByteVA(4, 3);
        final float positionScale = 2.0f;
        final float[] positionBias = { 1, 2, 3 };
        vb.setPositions(positions, positionScale, positionBias);

        final VertexArray normals = createByteVA(4, 3);
        vb.setNormals(normals);

        final VertexArray colors = createByteVA(4, 4);
        vb.setColors(colors);

        //TODO test more attributes

        VertexBuffer dup = (VertexBuffer) vb.duplicate();
        assertNotNull(dup);
        assertEquals(vb.getVertexCount(), dup.getVertexCount());
        assertEquals(defaultColor, dup.getDefaultColor());
        
        final float[] positionScaleBias = new float[4];
        assertSame(positions, dup.getPositions(positionScaleBias));
        final float delta = 0.001f;
        assertEquals(positionScale, positionScaleBias[0], delta);
        assertEquals(positionBias[0], positionScaleBias[1], delta);
        assertEquals(positionBias[1], positionScaleBias[2], delta);
        assertEquals(positionBias[2], positionScaleBias[3], delta);

        assertSame(normals, vb.getNormals());

        assertSame(colors, vb.getColors());
    }
    
    public void testGetPositionsNullBiasNull()
    {
        VertexBuffer vb = new VertexBuffer();
        assertNull(vb.getPositions(null));
    }

    public void testGetPositionsNullBias1()
    {
        VertexBuffer vb = new VertexBuffer();
        assertNull(vb.getPositions(new float[1]));
    }

    public void testGetPositionsNullBias2()
    {
        VertexBuffer vb = new VertexBuffer();
        assertNull(vb.getPositions(new float[2]));
    }

    public void testGetPositionsNullBias3()
    {
        VertexBuffer vb = new VertexBuffer();
        assertNull(vb.getPositions(new float[3]));
    }

    public void testGetPositionsNullBias4()
    {
        VertexBuffer vb = new VertexBuffer();
        assertNull(vb.getPositions(new float[4]));
    }

    public void testSetPositions1BiasNull()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 1, null);
    }

    public void testSetPositions2BiasNull()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 2, null);
    }

    public void testSetPositions3BiasNull()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 3, null);
    }

    public void testSetPositions4BiasNull()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 4, null);
    }

    public void testSetPositions1Bias1()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 1, new float[]{ 0 });
    }

    public void testSetPositions2Bias1()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            createPositions(vb, 2, new float[]{ 0 });
            fail("insufficient bias length, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }
    }

    public void testSetPositions3Bias1()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            createPositions(vb, 3, new float[]{ 0 });
            fail("insufficient bias length, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }
    }

    public void testSetPositions4Bias1()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            createPositions(vb, 4, new float[]{ 0 });
            fail("insufficient bias length, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }

    }

    public void testSetPositions2Bias2()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 2, new float[]{ 0, 0 });
    }

    public void testSetPositions3Bias2()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            createPositions(vb, 3, new float[]{ 0, 0 });
            fail("insufficient bias length, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }
    }

    public void testSetPositions4Bias2()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            createPositions(vb, 4, new float[]{ 0, 0 });
            fail("insufficient bias length, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }
    }

    public void testSet3Positions4And3Normals3()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 3, 4, null);
        createNormals(vb, 3);
    }

    public void testSet4Positions4And3Normals3()
    {
        VertexBuffer vb = new VertexBuffer();
        createPositions(vb, 4, 4, null);
        try
        {
            createNormals(vb, 3);
            fail("vertex count mismatch, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //success
        }
    }

    public void testSetNormals1()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            vb.setNormals(createByteVA(3, 1));
            fail("normals must have 3 components, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testSetNormals2()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            vb.setNormals(createByteVA(3, 2));
            fail("normals must have 3 components, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testSetNormals3()
    {
        VertexBuffer vb = new VertexBuffer();
        vb.setNormals(createByteVA(3, 3));
    }

    public void testSetNormals4()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            vb.setNormals(createByteVA(3, 4));
            fail("normals must have 3 components, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testSetColors1()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            vb.setColors(createByteVA(3, 1));
            fail("normals must have 3 components, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testSetColors2()
    {
        VertexBuffer vb = new VertexBuffer();
        try
        {
            vb.setColors(createByteVA(3, 2));
            fail("normals must have 3 components, must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            //correct
        }
    }

    public void testSetColors3()
    {
        VertexBuffer vb = new VertexBuffer();
        final VertexArray va = createByteVA(3, 3);
        assertNull(vb.getColors());
        vb.setColors(va);
        assertSame(va, vb.getColors());
    }

    public void testSetColors4()
    {
        VertexBuffer vb = new VertexBuffer();
        final VertexArray va = createByteVA(3, 4);
        assertNull(vb.getColors());
        vb.setColors(va);
        assertSame(va, vb.getColors());
    }

    public void testIsMutable()
    {
        VertexBuffer vb = new VertexBuffer();
        assertTrue(vb.isMutable());
    }

    public void testGetDefaultColor()
    {
        VertexBuffer vb = new VertexBuffer();
        assertEquals(0xffffffff, vb.getDefaultColor());
    }

    public void testGetDefaultPointSize()
    {
        VertexBuffer vb = new VertexBuffer();
        assertEquals(1.0f, vb.getDefaultPointSize(), 0.001f);
    }
    
    public void testGetVertexCount()
    {
        VertexBuffer vb = new VertexBuffer();
        assertEquals(0, vb.getVertexCount());
    }
}
