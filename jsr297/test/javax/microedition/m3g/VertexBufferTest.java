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
public class VertexBufferTest extends TestCase
{
    public void testConstructor()
    {
        try
        {
            VertexArray va = new VertexArray(3, 3, VertexArray.BYTE);
            VertexBuffer vb = new VertexBuffer();
            vb.setPositions(va, 1.0f, null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            fail("valid constructor call must not throw");
        }
    }

    private static final void createPositions(VertexBuffer vb, int vertexCount, int numComponents, float[] bias)
    {
        VertexArray pos = new VertexArray(vertexCount, numComponents, VertexArray.BYTE);
        vb.setPositions(pos, 1.0f, bias);
    }

    private static final void createPositions(VertexBuffer vb, int numComponents, float[] bias)
    {
        createPositions(vb, 3, numComponents, bias);
    }

    private static final void createNormals(VertexBuffer vb, int vertexCount)
    {
        VertexArray norm = new VertexArray(vertexCount, 3, VertexArray.BYTE);
        vb.setNormals(norm);
    }

    private static final void createNormals(VertexBuffer vb)
    {
        createNormals(vb, 3);
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
}
