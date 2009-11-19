/**
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

package m3x.m3g;

/**
 * 
 * @author jgasseli
 */
public class SkinnedMeshTest extends AbstractTestCase
{
    public void testSaveAndLoad()
    {
        VertexArray pos = new VertexArray(3, 3, VertexArray.BYTE);
        pos.set(0, 1, new byte[]{0, 0, 0});
        pos.set(1, 1, new byte[]{0, 1, 0});
        pos.set(2, 1, new byte[]{1, 0, 0});
        VertexBuffer vb = new VertexBuffer();
        vb.setPositions(pos, 1.0f, null);

        IndexBuffer ib = new TriangleStripArray(0, new int[]{3});
        Appearance ap = new Appearance();
        Group skeleton = new Group();
        SkinnedMesh mesh = new SkinnedMesh(vb, ib, ap, skeleton);

        Object3D[] roots = new Object3D[]{ mesh };
        assertSaveAndLoad(roots);
    }
}
