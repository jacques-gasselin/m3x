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

    public static interface MeshEvaluator
    {
        /**
         * Evaluate a mesh over the 2D interpolants s and t.
         *
         * @param s the interpolant in the s-axis in texture space, in the range
         * [0, 1]
         * @param t the interpolant in the t-axis in texture space, in the range
         * [0, 1]
         * @param position
         * @param poffset
         * @param normal
         * @param noffset
         */
        public void evaluate(double s, double t, float[] position, int poffset,
                float[] normal, int noffset);
    }

    private static final class SphereMeshEvaluator implements MeshEvaluator
    {
        private final float radius;

        private SphereMeshEvaluator(float radius)
        {
            this.radius = radius;
        }

        public void evaluate(double s, double t, float[] position, int poffset,
                float[] normal, int noffset)
        {
            final double angleZ = Math.PI * t;
            //bottom to top
            final double normalZ = -Math.cos(angleZ);
            final double radiusXY = Math.sin(angleZ);
            final double angle = 2.0 * Math.PI * s;
            final double normalX = Math.cos(angle) * radiusXY;
            final double normalY = Math.sin(angle) * radiusXY;

            position[poffset + 0] = (float) normalX * radius;
            position[poffset + 1] = (float) normalY * radius;
            position[poffset + 2] = (float) normalZ * radius;

            normal[noffset + 0] = (float) normalX;
            normal[noffset + 1] = (float) normalY;
            normal[noffset + 2] = (float) normalZ;
        }
    }

    public static final Mesh createMesh(MeshEvaluator evaluator,
            int rows, int columns)
    {
        if (evaluator == null)
        {
            throw new NullPointerException("evaluator is null");
        }
        
        if (rows < 2)
        {
            throw new IllegalArgumentException("less than 2 rows not allowed");
        }
        if (columns < 2)
        {
            throw new IllegalArgumentException("less than 2 columns not allowed");
        }

        final int vertexCount = rows * columns;
        if (vertexCount > 65535)
        {
            throw new IllegalArgumentException("total vertex count will be" +
                    " greater than 65535");
        }

        VertexBuffer vb = new VertexBuffer();

        VertexArray positionArray = new VertexArray(vertexCount, 3, VertexArray.FLOAT);
        VertexArray normalArray = new VertexArray(vertexCount, 3, VertexArray.FLOAT);
        VertexArray texcoordArray = new VertexArray(vertexCount, 2, VertexArray.FLOAT);

        final double stepS = 1.0 / (columns - 1);
        final double stepT = 1.0 / (rows - 1);

        final float[] positions = new float[columns * 3];
        final float[] normals = new float[columns * 3];
        final float[] texcoords = new float[columns * 2];
        for (int row = 0; row < rows; ++row)
        {
            final double t = stepT * row;
            for (int column = 0; column < columns; ++column)
            {
                final double s = stepS * column;
                final int offset = column * 3;
                evaluator.evaluate(s, t, positions, offset, normals, offset);
                texcoords[column * 2 + 0] = (float) s;
                texcoords[column * 2 + 1] = (float) t;
            }

            final int firstVertex = row * columns;
            positionArray.set(firstVertex, columns, positions);
            normalArray.set(firstVertex, columns, normals);
            texcoordArray.set(firstVertex, columns, texcoords);
        }

        vb.setPositions(positionArray, 1.0f, null);
        vb.setNormals(normalArray);
        vb.setTexCoords(0, texcoordArray, 1.0f, null);

        //tie up the indices
        final int triangleCount =
                //between each row there is a quad strip
                (rows - 1) * (columns - 1) * 2;

        //discrete triangles
        final int[] indices = new int[triangleCount * 3];
        int index = 0;

        //each slice
        for (int row = 0; row < (rows - 1); ++row)
        {
            //bottom to top
            final int bottomStartVertex = row * columns;
            final int topStartVertex = (row + 1) * columns;
            //one quad at a time
            for (int column = 0; column < (columns - 1); ++column)
            {
                final int nextColumn = column + 1;
                //upper-left triangle CCW
                indices[index + 0] = topStartVertex + column;
                indices[index + 1] = bottomStartVertex + column;
                indices[index + 2] = topStartVertex + nextColumn;
                index += 3;
                //lower-right triangle CCW
                indices[index + 0] = topStartVertex + nextColumn;
                indices[index + 1] = bottomStartVertex + column;
                indices[index + 2] = bottomStartVertex + nextColumn;
                index += 3;
            }
        }

        IndexBuffer ib = new IndexBuffer(IndexBuffer.TRIANGLES,
                triangleCount, indices);
        Appearance a = new Appearance();

        Mesh m = new Mesh(1, 0);
        m.setVertexBuffer(vb);
        m.setAppearance(0, a);
        m.setIndexBuffer(0, ib);

        return m;
    }

    public static final Mesh createSphere(float radius, int slices, int stacks)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("negative or 0 radius not allowed");
        }
        if (slices <= 1)
        {
            throw new IllegalArgumentException("less than 1 slice not allowed");
        }
        if (stacks <= 1)
        {
            throw new IllegalArgumentException("less than 1 stack not allowed");
        }

        final MeshEvaluator eval = new SphereMeshEvaluator(radius);
        return createMesh(eval, stacks + 1, slices + 1);
    }
}
