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

    public static final Mesh createSphere(float radius, int slices, int stacksPlus1)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("negative or 0 radius not allowed");
        }
        if (slices <= 1)
        {
            throw new IllegalArgumentException("less than 1 slice not allowed");
        }
        if (stacksPlus1 <= 0)
        {
            throw new IllegalArgumentException("less than 1 stack not allowed");
        }
        
        final int stacks = stacksPlus1 - 1;
        final int vertexCount = 2 + stacks * slices;

        if (vertexCount > 65535)
        {
            throw new IllegalArgumentException("total vertex count, 2 + " +
                    "(stacks - 1) + slices, will be greater than 65535");
        }

        VertexBuffer vb = new VertexBuffer();
        
        VertexArray normals = new VertexArray(vertexCount, 3, VertexArray.FLOAT);
        VertexArray texcoords = new VertexArray(vertexCount, 2, VertexArray.FLOAT);

        final double stepU = 1.0 / (slices);
        final double startU = 0;
        final double stepV = 1.0 / (stacks + 1);
        final double startV = stepV;
        final double stepAngleZ = Math.PI / (stacks + 1);
        final double startAngleZ = stepAngleZ;
        final double startAngle = 0;
        final double stepAngle = 2.0 * Math.PI / (slices);

        //create the bottom pole vertex
        {
            final float[] startNormal = new float[]{0, 0, -1};
            final float[] startUV = new float[]{0, 0};
            normals.set(0, 1, startNormal);
            texcoords.set(0, 1, startUV);
        }
        
        //create the top pole vertex
        {
            final float[] endNormal = new float[]{0, 0, 1};
            final float[] endUV = new float[]{0, 1};
            normals.set(vertexCount - 1, 1, endNormal);
            texcoords.set(vertexCount - 1, 1, endUV);
        }

        //create the stacks and slices
        final float[] stackNormals = new float[3 * slices];
        final float[] stackUVs = new float[2 * slices];
        for (int stack = 0; stack < stacks; ++stack)
        {
            final double angleZ = startAngleZ + stepAngleZ * stack;
            final double texcoordV = startV + stepV * stack;
            //bottom to top
            final double normalZ = -Math.cos(angleZ);
            final double radiusXY = Math.sin(angleZ);
            for (int slice = 0; slice < slices; ++slice)
            {
                final double angle = startAngle + stepAngle * slice;
                final double normalX = Math.cos(angle) * radiusXY;
                final double normalY = Math.sin(angle) * radiusXY;
                final double texcoordU = startU + stepU * slice;

                stackNormals[slice * 3 + 0] = (float) normalX;
                stackNormals[slice * 3 + 1] = (float) normalY;
                stackNormals[slice * 3 + 2] = (float) normalZ;

                stackUVs[slice * 2 + 0] = (float) texcoordU;
                stackUVs[slice * 2 + 1] = (float) texcoordV;
            }

            normals.set(1 + stack * slices, slices, stackNormals);
            texcoords.set(1 + stack * slices, slices, stackUVs);
        }

        vb.setPositions(normals, radius, null);
        vb.setNormals(normals);
        vb.setTexCoords(0, texcoords, 1.0f, null);

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
        if (true)
        {
            final int bottomStartVertex = 0;
            final int topStartVertex = 1;
            for (int slice = 0; slice < slices ; ++slice)
            {
                //CCW
                indices[index + 0] = topStartVertex + slice;
                indices[index + 1] = bottomStartVertex;
                indices[index + 2] = topStartVertex + (slice + 1) % slices;
                index += 3;
            }
        }
        
        //each slice
        if (true)
        {
            for (int stack = 0; stack < (stacks - 1); ++stack)
            {
                //bottom to top
                final int bottomStartVertex = 1 + stack * slices;
                final int topStartVertex = 1 + (stack + 1) * slices;
                //one quad at a time
                for (int slice = 0; slice < slices; ++slice)
                {
                    final int nextSlice = (slice + 1) % slices;
                    //upper-left triangle CCW
                    indices[index + 0] = topStartVertex + slice;
                    indices[index + 1] = bottomStartVertex + slice;
                    indices[index + 2] = topStartVertex + nextSlice;
                    index += 3;
                    //lower-right triangle CCW
                    indices[index + 0] = topStartVertex + nextSlice;
                    indices[index + 1] = bottomStartVertex + slice;
                    indices[index + 2] = bottomStartVertex + nextSlice;
                    index += 3;
                }
            }
        }

        //the top cap
        if (true)
        {
            final int bottomStartVertex = 1 + (stacks - 1) * slices;
            final int topStartVertex = vertexCount - 1;
            for (int slice = 0; slice < slices; ++slice)
            {
                //CCW
                indices[index + 0] = topStartVertex;
                indices[index + 1] = bottomStartVertex + slice;
                indices[index + 2] = bottomStartVertex + (slice + 1) % slices;
                index += 3;
            }
        }

        IndexBuffer ib = new IndexBuffer(IndexBuffer.TRIANGLES, triangleCount, indices);
        Appearance a = new Appearance();

        Mesh m = new Mesh(1, 0);
        m.setVertexBuffer(vb);
        m.setAppearance(0, a);
        m.setIndexBuffer(0, ib);

        return m;
    }
}
