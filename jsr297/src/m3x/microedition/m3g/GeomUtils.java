/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Texture2D;
import m3x.Require;

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

    public static interface MeshEvaluator1D
    {
        /**
         * Evaluate a mesh over the 1D interpolant s.
         *
         * @param s the interpolant in the s-axis in texture space, in the range
         * [0, 1]
         * @param position
         * @param poffset
         */
        public void evaluate(double s, float[] position, int poffset);
    }

    public static interface MeshEvaluator2D
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

    public static final Mesh createMesh(MeshEvaluator1D evaluator,
                                        int subdiv)
    {
        if (evaluator == null)
        {
            throw new NullPointerException("evaluator is null");
        }
        if (subdiv < 2)
        {
            throw new IllegalArgumentException();
        }
        if (subdiv > 65535)
        {
            throw new IllegalArgumentException();
        }

        int vertexCount = subdiv;
        VertexBuffer vb = new VertexBuffer();
        VertexArray positionArray = new VertexArray(vertexCount, 3, VertexArray.FLOAT);

        float stepS = 1.0f / (subdiv - 1);
        final float[] positions = new float[vertexCount * 3];
        for (int i = 0; i < subdiv; ++i)
        {
            final float s = i * stepS;
            evaluator.evaluate(s, positions, i * 3);
        }
        positionArray.set(0, vertexCount, positions);

        vb.setPositions(positionArray, 1.0f, null);
        final int lineSegmentCount = vertexCount - 1;
        final int[] indices = new int[lineSegmentCount * 2];
        //System.out.println("lineSegmentCount = " + lineSegmentCount);
        int vertIdx = 0;
        for (int i = 0; i < indices.length; i += 2)
        {
            indices[i] = vertIdx ;
            indices[i + 1] = vertIdx + 1;
            vertIdx++;
            //System.out.println(indices[i]);
            //System.out.println(indices[i + 1]);
        }

        IndexBuffer ib =
                new IndexBuffer(IndexBuffer.LINES,
                                lineSegmentCount,
                                indices);
        Mesh m = new Mesh(1, 0);
        Appearance a = new Appearance();
        m.setVertexBuffer(vb);
        m.setAppearance(0, a);
        m.setIndexBuffer(0, ib);

        return m;
    }

    public static final Mesh createMesh(MeshEvaluator2D evaluator,
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

    private static final class PlaneMeshEvaluator implements MeshEvaluator2D
    {
        private final float xScale;
        private final float yScale;

        private PlaneMeshEvaluator(float xScale, float yScale)
        {
            this.xScale = xScale;
            this.yScale = yScale;
        }

        public void evaluate(double s, double t, float[] position, int poffset,
                float[] normal, int noffset)
        {
            //bottom to top
            position[poffset + 0] = (float) (xScale * (s - 0.5));
            position[poffset + 1] = (float) (yScale * (t - 0.5));
            position[poffset + 2] = 0;

            normal[noffset + 0] = 0;
            normal[noffset + 1] = 0;
            normal[noffset + 2] = 1;
        }
    }

    public static final Mesh createPlane(float xScale, float yScale, int rows, int colums)
    {
        final MeshEvaluator2D eval = new PlaneMeshEvaluator(xScale, yScale);
        return createMesh(eval, rows, colums);
    }

    private static final class SphereMeshEvaluator implements MeshEvaluator2D
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

        final MeshEvaluator2D eval = new SphereMeshEvaluator(radius);
        return createMesh(eval, stacks + 1, slices + 1);
    }

    private static final class ScreenQuadMeshEvaluator implements MeshEvaluator2D
    {
        private final float xScale;
        private final float yScale;

        private ScreenQuadMeshEvaluator(Texture2D texture)
        {
            final Image2D image = texture.getImage2D();
            this.xScale = image.getWidth();
            this.yScale = image.getHeight();
        }

        public void evaluate(double s, double t, float[] position, int poffset,
                float[] normal, int noffset)
        {
            //bottom to top
            position[poffset + 0] = (float) (xScale * s);
            position[poffset + 1] = (float) (yScale * t);
            position[poffset + 2] = 0;

            normal[noffset + 0] = 0;
            normal[noffset + 1] = 0;
            normal[noffset + 2] = 1;
        }
    }

    public static final Mesh createScreenQuad(Texture2D texture)
    {
        final MeshEvaluator2D eval = new ScreenQuadMeshEvaluator(texture);
        final Mesh m = createMesh(eval, 2, 2);
        final Appearance a = m.getAppearance(0);
        a.setTexture(0, texture);
        return m;
    }

    private static final class LineMeshEvaluator implements MeshEvaluator1D
    {
        private float[] startPoint;
        private float[] endPoint;

        private LineMeshEvaluator(float[] startPoint, float[] endPoint)
        {
            if (startPoint == null || endPoint == null)
            {
                throw new IllegalArgumentException("start and/or end point " +
                        "is null");
            }

            if (startPoint.length < 3 || endPoint.length < 3)
            {
                throw new IllegalArgumentException();
            }

            this.startPoint = new float[3];
            this.endPoint = new float[3];
            System.arraycopy(startPoint, 0, this.startPoint, 0, 3);
            System.arraycopy(endPoint, 0, this.endPoint, 0, 3);
        }
        
        public void evaluate(double s, float[] position, int poffset)
        {
            if (position.length < 3)
            {
                throw new IllegalArgumentException();
            }

            for (int i = 0; i < 3; i++)
            {
                position[poffset + i] = startPoint[i] +
                        (float) s * (endPoint[i] - startPoint[i]);
            }
        }
    }

    /**
     * Creates a Mesh consisting of a single line segment of a given color.
     * @param startPos The position of the start vertex (in local coordinates)
     * @param endPos The position of the end vertex (in local coordinates)
     * @param r The R component of the color of the line segment.
     * @param g The G component of the color of the line segment.
     * @param b The B component of the color of the line segment.
     * @return The line segment mesh.
     */
    public static final Mesh createLineMesh(float[] startPos, float[] endPos,
                                            float r, float g, float b)
    {
        final MeshEvaluator1D eval = new LineMeshEvaluator(startPos, endPos);
        final Mesh m = createMesh(eval, 2);

        VertexArray colorsArray = new VertexArray(2, 3, VertexArray.FLOAT);
        float[] colors = {r, g, b, r, g, b};
        colorsArray.set(0, 2, colors);
        m.getVertexBuffer().setColors(colorsArray);
        return m;
    }

    /**
     * Creates a Mesh visualizing a pick ray.
     * The vertex positions are relative to the coordinate frame of the camera.
     * @param camera
     * @param x X coordinate of the point on the viewport plane through which
     *          to cast the ray. See Group.pick.
     * @param y Y coordinate of the point on the viewport plane through which
     *          to cast the ray. See Group.pick.
     * @return The pickray Mesh.
     * @throws NullPointerException if camera is null
     * @throws IllegalArgumentException if x or y is not in the range [0, 1]
     * @see Group#pick(int, float, float, javax.microedition.m3g.Camera, javax.microedition.m3g.RayIntersection)
     */
    public static Mesh createPickRayMesh(Camera camera, float x, float y)
    {
        Require.notNull(camera, "camera");
        Require.argumentInRange(x, "x", 0, 1);
        Require.argumentInRange(y, "y", 0, 1);

        final float[] near = new float[4];
        final float[] far = new float[4];
        TransformUtils.unproject(camera, null, x, y, near, far);
        
        return createLineMesh(near, far, 1.0f, 1.0f, 0.0f);
    }

    /**
     * Creates a Mesh visualizing the frustum of a given camera.
     * The vertex positions are relative to the coordinate frame of the camera.
     * @param camera
     * @return a new frustum mesh
     */
    public static Mesh createViewFrustumMesh(Camera camera)
    {
        float[] params = new float[4];
        final int projectionType = camera.getProjection(params);

        if (projectionType != Camera.PARALLEL &&
            projectionType != Camera.PERSPECTIVE)
        {
            throw new IllegalArgumentException("camera must have a parallel " +
                    "or perspective projection.");
        }

        final boolean isParallel = projectionType == Camera.PARALLEL;
        final float fovy = params[0];
        final float aspectRatio = params[1];
        final float near = params[2];
        final float far = params[3];

        final float h = isParallel ? fovy : (float)Math.tan(0.5 * fovy * Math.PI / 180.0f);
        final float w = aspectRatio * h;
        VertexBuffer vb = new VertexBuffer();
        VertexArray positionVertexArray =
                new VertexArray(8, 3, VertexArray.FLOAT);
        float[] positions = new float[8 * 3];

        //near clipping plane verts
        float halfHeight = isParallel ? h / 2.0f : h * near;
        float halfWidth = isParallel ? w / 2.0f : w * near;
        
        positions[0] = -halfWidth;
        positions[1] = -halfHeight;
        positions[2] = -near;

        positions[3] = -halfWidth;
        positions[4] = halfHeight;
        positions[5] = -near;

        positions[6] = halfWidth;
        positions[7] = halfHeight;
        positions[8] = -near;

        positions[9] = halfWidth;
        positions[10] = -halfHeight;
        positions[11] = -near;

        //far clipping plane verts
        halfHeight = isParallel ? h / 2.0f : h * far;
        halfWidth = isParallel ? w / 2.0f : w * far;
        positions[12] = -halfWidth;
        positions[13] = -halfHeight;
        positions[14] = -far;

        positions[15] = -halfWidth;
        positions[16] = halfHeight;
        positions[17] = -far;

        positions[18] = halfWidth;
        positions[19] = halfHeight;
        positions[20] = -far;

        positions[21] = halfWidth;
        positions[22] = -halfHeight;
        positions[23] = -far;

        positionVertexArray.set(0, 8, positions);

        VertexArray colorVertexArray = new VertexArray(8, 3, VertexArray.FLOAT);
        float[] colors =
        {
            //red near plane
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,
            0.7f, 0.0f, 0.0f,

            //green far plane
            0.0f, 0.7f, 0.0f,
            0.0f, 0.7f, 0.0f,
            0.0f, 0.7f, 0.0f,
            0.0f, 0.7f, 0.0f,
        };
        colorVertexArray.set(0, 8, colors);

        int[] indices =
        {
            //near plane rectangle
            0, 1,
            1, 2,
            2, 3,
            3, 0,
            //near plane cross
            0, 2,
            1, 3,
            //far plane rectangle
            4, 5,
            5, 6,
            6, 7,
            7, 4,
            //far plane cross
            4, 6,
            5, 7,
            //lines connecting near and far planes
            0, 4,
            1, 5,
            2, 6,
            3, 7,
        };

        IndexBuffer ib = new IndexBuffer(IndexBuffer.LINES, 16, indices);
        vb.setPositions(positionVertexArray, 1.0f, null);
        vb.setColors(colorVertexArray);
        Mesh frustumMesh = new Mesh(1, 0);
        Appearance a = new Appearance();
        frustumMesh.setAppearance(0, a);
        frustumMesh.setVertexBuffer(vb);
        frustumMesh.setIndexBuffer(0, ib);

        return frustumMesh;
    }

    public static Mesh createCubeMesh(float size, float r, float g, float b, boolean wireFrame)
    {
        VertexBuffer vb = new VertexBuffer();

        VertexArray positionVertexArray = new VertexArray(8, 3, VertexArray.FLOAT);
        float[] positions =
        {
            -size, -size, -size,
            size, -size, -size,
            size, -size, size,
            -size, -size, size,

            -size, size, -size,
            size, size, -size,
            size, size, size,
            -size, size, size
        };

        VertexArray colorVertexArray = new VertexArray(8, 3, VertexArray.FLOAT);
        float[] colors =
        {
            r, g, b, r, g, b,
            r, g, b, r, g, b,
            r, g, b, r, g, b,
            r, g, b, r, g, b,
        };
        colorVertexArray.set(0, 8, colors);

        int[] indices = null;
        positionVertexArray.set(0, 8, positions);
        vb.setPositions(positionVertexArray, 1.0f, null);
        vb.setColors(colorVertexArray);

        IndexBuffer ib = null;

        if (wireFrame)
        {
            indices = new int[]
            {
                0, 1,
                1, 2,
                2, 3,
                3, 0,

                0, 4,
                1, 5,
                2, 6,
                3, 7,

                4, 5,
                5, 6,
                6, 7,
                7, 4,
            };
            ib = new IndexBuffer(IndexBuffer.LINES,
                                         12, indices);
        }
        else
        {
            indices = new int[]
            {
                0, 1, 2,
                1, 2, 3,

                4, 1, 0,
                0, 1, 5,

                2, 3, 6,
                7, 2, 3,

                0, 2, 4,


                4, 5, 6,
                5, 6, 7
            };
            new IndexBuffer(IndexBuffer.TRIANGLES,
                                         12, indices);
            throw new UnsupportedOperationException();
        }
        
        Mesh cubeMesh = new Mesh(1, 0);
        cubeMesh.setVertexBuffer(vb);
        cubeMesh.setIndexBuffer(0, ib);
        Appearance a = new Appearance();
        cubeMesh.setAppearance(0, a);

        return cubeMesh;
    }

    /**
     * Creates a mesh consisting of three line segments representing
     * the positive x, y and z coordinate axes, colored red, green and blue
     * respectively.
     * @param axisLength The length of each coordinate axis.
     * @return The coordinate axis mesh.
     */
    public static Mesh createCoordinateFrameMesh(float axisLength)
    {
        VertexBuffer vb = new VertexBuffer();
        VertexArray positionsArray = new VertexArray(6, 3, VertexArray.FLOAT);
        float[] positions = {0.0f, 0.0f, 0.0f,
                             axisLength, 0.0f, 0.0f,
                             0.0f, 0.0f, 0.0f,
                             0.0f, axisLength, 0.0f,
                             0.0f, 0.0f, 0.0f,
                             0.0f, 0.0f, axisLength};
        positionsArray.set(0, 6, positions);

        VertexArray colorsArray = new VertexArray(6, 3, VertexArray.FLOAT);
        float[] colors = {1.0f, 0.0f, 0.0f,
                          1.0f, 0.0f, 0.0f,
                          0.0f, 1.0f, 0.0f,
                          0.0f, 1.0f, 0.0f,
                          0.0f, 0.0f, 1.0f,
                          0.0f, 0.0f, 1.0f};
        colorsArray.set(0, 6, colors);

        vb.setPositions(positionsArray, 1.0f, null);
        vb.setColors(colorsArray);

        int[] indices = {0, 1, 2, 3, 4, 5};
        IndexBuffer ib = new IndexBuffer(IndexBuffer.LINES, 3, indices);

        Appearance a = new Appearance();
        Mesh axisMesh = new Mesh(1, 0);
        axisMesh.setAppearance(0, a);
        axisMesh.setVertexBuffer(vb);
        axisMesh.setIndexBuffer(0, ib);
        return axisMesh;
    }

}
