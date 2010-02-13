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
public class MeshTest extends AbstractTestCase
{
    public void testNewMeshEmpty()
    {
        Mesh m = new Mesh(1, 0);
        assertEquals(1, m.getSubmeshCount());
        assertEquals(0, m.getMorphTargetCount());
    }

    public void testNewMeshSingleMorph()
    {
        Mesh m = new Mesh(1, 1);
        assertEquals(1, m.getSubmeshCount());
        assertEquals(1, m.getMorphTargetCount());
    }

    public void testDuplicate()
    {
        Mesh m = new Mesh(1, 0);
        VertexBuffer vb = new VertexBuffer();
        VertexArray va = new VertexArray(2, 3, VertexArray.FLOAT);
        vb.setPositions(va, 1.0f, null);
        IndexBuffer ib = new IndexBuffer(IndexBuffer.LINES, 2, 0);
        m.setVertexBuffer(vb);
        m.setIndexBuffer(0, ib);

        Mesh dup = (Mesh) m.duplicate();
        assertNotNull(dup);
        assertSame(vb, dup.getVertexBuffer());
        assertSame(va, dup.getVertexBuffer().getPositions(null));
        assertSame(ib, dup.getIndexBuffer(0));
        assertNull(dup.getAppearance(0));
     }

     public void testDeprectedNewMeshSingle()
     {
         VertexBuffer vb = new VertexBuffer();
         IndexBuffer ib = new IndexBuffer(IndexBuffer.TRIANGLES, 1, 0);
         Appearance a = new Appearance();
         
         Mesh m = new Mesh(vb, ib, a);

         assertSame(vb, m.getVertexBuffer());
         assertEquals(1, m.getSubmeshCount());
         assertSame(ib, m.getIndexBuffer(0));
         assertSame(a, m.getAppearance(0));
     }

     public void testDeprectedNewMeshSingleArray()
     {
         VertexBuffer vb = new VertexBuffer();
         IndexBuffer ib = new IndexBuffer(IndexBuffer.TRIANGLES, 1, 0);
         Appearance a = new Appearance();

         Mesh m = new Mesh(vb, new IndexBuffer[]{ib}, new Appearance[]{a});

         assertSame(vb, m.getVertexBuffer());
         assertEquals(1, m.getSubmeshCount());
         assertSame(ib, m.getIndexBuffer(0));
         assertSame(a, m.getAppearance(0));
     }
}
