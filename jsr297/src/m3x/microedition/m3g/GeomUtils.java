/*
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

package m3x.microedition.m3g;

import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.Mesh;

/**
 * @author jgasseli
 */
public final class GeomUtils
{
    /**
     * Static utility class.
     */
    private GeomUtils()
    {
        
    }

    public static final Mesh createSphere(float radius, int slices, int stacks)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("negative or 0 radius not allowed");
        }
        if (slices <= 0)
        {
            throw new IllegalArgumentException("negative or 0 slices not allowed");
        }
        if (stacks <= 0)
        {
            throw new IllegalArgumentException("negative or 0 stacks not allowed");
        }

        VertexBuffer vb = new VertexBuffer();
        final int vertexCount = 2 + stacks * slices;
        
        VertexArray normals = new VertexArray(vertexCount, 3, VertexArray.FLOAT);

        final double stepAngleZ = Math.PI / (stacks + 1);
        final double startAngleZ = stepAngleZ;
        final double startAngle = 0;
        final double stepAngle = 2.0 * Math.PI / (slices);

        //create the bottom pole vertex
        {
            final float[] startNormal = new float[]{0, 0, -1};
            normals.set(0, 1, startNormal);
        }
        
        //create the top pole vertex
        {
            final float[] endNormal = new float[]{0, 0, 1};
            normals.set(vertexCount - 1, 1, endNormal);
        }

        //create the stacks and slices
        final float[] stackNormals = new float[3 * slices];
        for (int stack = 0; stack < stacks; ++stack)
        {
            final double angleZ = startAngleZ + stepAngleZ * stack;
            //bottom to top
            final double normalZ = -Math.cos(angleZ);
            final double radiusXY = Math.sin(angleZ);
            for (int slice = 0; slice < slices; ++slice)
            {
                final double angle = startAngle + stepAngle * slice;
                final double normalX = Math.cos(angle) * radiusXY;
                final double normalY = Math.sin(angle) * radiusXY;

                stackNormals[slice * 3 + 0] = (float) normalX;
                stackNormals[slice * 3 + 1] = (float) normalY;
                stackNormals[slice * 3 + 2] = (float) normalZ;
            }

            normals.set(1 + stack * slices, slices, stackNormals);
        }

        vb.setPositions(normals, radius, null);
        vb.setNormals(normals);

        //tie up the indices
        final int triangleCount = 
                //bottom and top fan caps
                slices * 2
                //between each stack there is a quad strip
                + (stacks - 1) * slices * 2; 

        //discrete triangles
        final int[] indices = new int[triangleCount * 3];
        int index = 0;

        //the bottom cap
        for (int slice = 0; slice < slices; ++slice)
        {
            indices[index + 0] = 0;
            indices[index + 1] = 1 + slice;
            indices[index + 2] = 1 + (slice + 1) % slices;
            index += 3;
        }

        //each slice
        //TODO

        //the top cap
        //TODO

        IndexBuffer ib = new IndexBuffer(IndexBuffer.TRIANGLES, triangleCount, indices);
        Appearance a = new Appearance();

        Mesh m = new Mesh(1, 0);
        m.setVertexBuffer(vb);
        m.setAppearance(0, a);
        m.setIndexBuffer(0, ib);

        return m;
    }
}
