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

package m3x.microedition.m3g;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;
import m3x.Require;
import m3x.Vmath;

/**
 * <p>Utility class for doing common transform operation that otherwise requires
 * some boilerplate code.</p>
 *
 * <ul>Some of the features are:
 * <li>Project points in the world space to eye space. This is useful for many
 * screen space or eye space algorithms.</li>
 * </ul>
 * @author jgasseli
 */
public final class TransformUtils
{
    private TransformUtils()
    {

    }

    /**
     * <p>Projects a position in the local space of the coordinate system, localToCamera,
     * without the projected w value divided out. The position is transformed from
     * world space to eye space. The user must do the viewport transform separately
     * if actual screen pixel position is needed.</p>
     *
     * @param camera the camera to use for projecting
     * @param localToCamera the local coordinate system, or null to use the camera
     * coordinate system (in effect the identity transform).
     * @param position the position to project.
     * @throws NullPointerException if camera is null
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position.length < 4
     * @see #project(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform, float[])
     */
    public static void projectWithoutPerspectiveDivide(Camera camera,
            Transform localToCamera, float[] position)
    {
        Require.notNull(camera, "camera");
        Require.argumentHasCapacity(position, "position", 4);

        if (localToCamera != null)
        {
            localToCamera.transform(position);
        }

        final Transform projection = new Transform();
        camera.getProjection(projection);
        projection.transform(position);
    }

    /**
     * <p>Projects a position in the local space of the coordinate system, localToCamera,
     * with the projected w value divided out. The w coordinate is left untouched,
     * but the rest of the components (x, y, z) are affected.</p>
     *
     * <p>If this is not desired; use a method with a contract leave components
     * untouched in regards to the perspective divide.</p>
     *
     * <p>The position is transformed from local space to eye space.
     * The user must do the viewport transform separately if actual screen
     * pixel position is needed.</p>
     *
     * @param camera the camera to use for projecting
     * @param localToCamera the local coordinate system, or null to use the camera
     * coordinate system (in effect the identity transform).
     * @param position the position to project.
     * @throws NullPointerException if camera is null
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position.length < 4
     * @see #projectWithoutPerspectiveDivide(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform, float[])
     */
    public static void project(Camera camera,
            Transform localToCamera, float[] position)
    {
        projectWithoutPerspectiveDivide(camera, localToCamera, position);

        //do the perspective divide
        final float invW = 1.0f / position[3];
        Vmath.vmul3(position, invW);
    }

    /**
     * <p>Unprojects an eye space point into a ray from the near to far clipping
     * planes. As no depth can be assertained in unprojection it is the duty
     * of the user to interpolate the ray to find a desired world position.</p>
     *
     * @param camera the camera to unproject from.
     * @param cameraToLocal
     * @param x the eye space x coordinate
     * @param y the eye space y coordinate
     * @param near
     * @param far
     */
    public static void unproject(Camera camera, Transform cameraToLocal,
            float x, float y, float[] near, float[] far)
    {
        final float pX = 2 * x - 1;
        final float pY = 1 - 2 * y;

        Vmath.vload4(near, pX, pY, -1, 1);
        Vmath.vload4(far, pX, pY, 1, 1);

        unproject(camera, cameraToLocal, near, far);
    }

    public static void unproject(Camera camera, Transform cameraToLocal,
            float[] near, float[] far)
    {
        Require.notNull(camera, "camera");
        Require.argumentHasCapacity(near, "near", 4);
        Require.argumentHasCapacity(far, "far", 4);

        final Transform inverseProjection = new Transform();
        camera.getProjection(inverseProjection);
        inverseProjection.invert();

        inverseProjection.transform(near);
        inverseProjection.transform(far);

        final float invWNear = 1.0f / near[3];
        Vmath.vmul3(near, invWNear);
        near[3] = 1.0f;

        final float invWFar = 1.0f / far[3];
        Vmath.vmul3(far, invWFar);
        far[3] = 1.0f;

        if (cameraToLocal != null)
        {
            cameraToLocal.transform(near);
            cameraToLocal.transform(far);
        }
    }

    /**
     * Computes a 3D space offset corresponding to a given screen offset.
     * The 3D space offset is computed such that points at a given z depth 
     * (in eye coordinates) move a given distance in pixels along the x and y
     * directions in screen space. This is for example useful when dragging an
     * object in 3D space to ensure that the object stays under the cursor.
     * @param camera The camera the scene is viewed through.
     * @param localToCamera The transformation to eye coordinates.
     * @param point A 3D point at the z depth (in eye coordinates) at which
     *        the screen projection of point translations
     *        correspond to the given screen translation.
     * @param dxScreen The desired translation along the screen x axis.
     * @param dyScreen The desired translation along the screen y axis.
     * @param screenWidth The screen width in pixels
     * @param screenHeight The screen height in pixels
     * @return A 3 element array containing the x, y and z components of the
     *         world space offset vector.
     */
    public static float[] screenTo3DOffset(Camera camera,
                                           Transform localToCamera,
                                           float[] point,
                                           float dxScreen,
                                           float dyScreen,
                                           float screenWidth,
                                           float screenHeight)
    {
        //get frustum information from the camera
        float[] params = new float[4];
        final int projectionType = camera.getProjection(params);
        if (projectionType != Camera.PARALLEL &&
            projectionType != Camera.PERSPECTIVE)
        {
            throw new IllegalArgumentException("camera must have parallel or" +
                                               " perspective projection.");
        }
        final boolean isPersp = projectionType == Camera.PERSPECTIVE;
        final float fovy = params[0];
        final float aspectRatio = params[1];


        float[] pointInEyeCoords = new float[4];
        System.arraycopy(point, 0, pointInEyeCoords, 0, 3);
        pointInEyeCoords[3] = 1.0f;
        Transform localToCameraInv = new Transform(localToCamera);
        localToCameraInv.invert();
        localToCameraInv.transform(pointInEyeCoords);
        final float eyeCoordinateZ = pointInEyeCoords[2];

        final float h = isPersp ?
                            2.0f * (float)Math.tan(Math.PI / 180.0f * fovy / 2.0f) :
                            2.0f * fovy;
        final float w = 2.0f * aspectRatio * h;

        //compute the width and height of the frustum at the z value
        //of the lookat
        final float frustumHeight = isPersp ? eyeCoordinateZ * h : h / 2.0f;

        //compute the pixel offset relative to the screen width and height
        final float dxRel = 1.0f / screenWidth;
        final float dyRel = 1.0f / screenHeight;

        //compute the factor ensuring the world offset at the depth
        //of the lookat conincides with the screen offset
        final float factorY = frustumHeight * dyRel;
        final float factorX = aspectRatio * frustumHeight * dxRel;

        //transform the x and y to become x and y in the local camera space
        final float[] vector = new float[]{ -dxScreen * factorX, dyScreen * factorY, 0, 0 };
        localToCamera.transform(vector);

        return new float[] {vector[0], vector[1], vector[2]};
    }
}
