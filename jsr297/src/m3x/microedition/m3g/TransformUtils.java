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
     * Returns true if the given normalized device coordinate is outside the unit
     * cube.
     * 
     * @param ndc the normalized device coordinate to check.
     * @return true if outside the unit cube, false otherwise.
     */
    public static final boolean ndcIsClipped(float[] ndc)
    {
        final float ndcX = ndc[0];
        final float ndcY = ndc[1];
        final float ndcZ = ndc[2];
        if (ndcX < -1 || ndcX > 1)
        {
            //outside x clip
            return true;
        }
        if (ndcY < -1 || ndcY > 1)
        {
            //outside y clip
            return true;
        }
        if (ndcZ < -1 || ndcZ > 1)
        {
            //outside z clip
            return true;
        }
        
        return false;
    }
    
    /**
     * <p>Transforms a normalized device coordinate into an absolute
     * screen space point. Note: this is not suitable for calculating delta
     * screen positions from delta ndc positions as for exmaple; ndc (0,0)
     * becomes screen (0.5, 0.5).</p>
     *
     * @param result the vector to store the screen space mapping in.
     * @param ndc the normalized device coordinate to map.
     */
    public static final void ndcToScreen(float[] result, float[] ndc)
    {
        final float pX = ndc[0];
        final float pY = ndc[1];
        final float x = (pX + 1) * 0.5f;
        final float y = (1 - pY) * 0.5f;

        Vmath.vload4(result, x, y, ndc[2], ndc[3]);
    }

    /**
     * <p>Transforms a normalized device coordinate into an absolute
     * screen space point. Note: this is not suitable for calculating delta
     * screen positions from delta ndc positions as for exmaple; ndc (0,0)
     * becomes screen (0.5, 0.5).</p>
     * 
     * @param result the vector to store the screen space mapping in.
     * @param ndc the normalized device coordinate to map.
     */
    public static final void ndcToScreen(float[] result, float[] ndc,
            int deviceWidth, int deviceHeight)
    {
        ndcToScreen(result, ndc);
        result[0] *= deviceWidth;
        result[1] *= deviceHeight;
    }
    
    /**
     * <p>Projects a position in the local space of the coordinate system, localToCamera,
     * without the projected w value divided out. The position is transformed from
     * world space to eye space. The user must do the viewport transform separately
     * if actual screen pixel position is needed.</p>
     *
     * <p>Note: take care to ensure you are using the correct localToCamera
     * transform. This can trip people up as it is not the transform from the
     * camera to the local object but the inverse.</p>
     *
     * <ul>Below are some examples of correct use:
     * <li>Retained mode:
     *   <div style="background-color: #efefef">
     *      <pre>
     * {@code Camera camera = new Camera();
     * camera.translate(0, 0, 6);
     *
     * Group group = new Group();
     * group.addChild(camera);
     *
     * Transform localToCamera = new Transform();
     * group.getTransformTo(camera, localToCamera);}</pre>
     *   </div>
     * </li>
     * <li>Immediate mode: (note the invert() call!)
     *   <div style="background-color: #efefef">
     *      <pre>
     * {@code Camera camera = new Camera();
     * camera.translate(0, 0, 6);
     *
     * Transform localToCamera = new Transform();
     * camera.getCompositeTransform(localToCamera);
     * localToCamera.invert();}</pre>
     *   </div>
     * </li>
     * </ul>
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
     * <p>Transforms a screen space point into a normalized device coordinate.</p>
     *
     * <p>{@code x} and {@code y} are in parametric screen coordinates with
     * the origin in the upper left corner. Thus (0, 0) signifies the upper
     * left corner, whilst (1, 1) signifies the bottom right corner.</p>
     *
     * @param result a 4D vector to place the result in.
     * @param x the screen space x coordinate, in the range [0, 1]
     * @param y the screen space y coordinate, in the range [0, 1]
     * @param ndcZ the desired z component of the screen space point when
     * transformed, in the range [-1, 1]
     */
    public static final void screenToNDC(float[] result, float x, float y, float ndcZ)
    {
        final float pX = 2 * x - 1;
        final float pY = 1 - 2 * y;

        Vmath.vload4(result, pX, pY, ndcZ, 1);
    }

    /**
     * <p>Transforms a screen space point into a normalized device coordinate.</p>
     *
     * <p>{@code x} and {@code y} are in pixel screen coordinates with
     * the origin in the upper left corner. Thus (0, 0) signifies the upper
     * left corner, whilst ({@code deviceWidth}, @{code deviceHeight})
     * signifies the bottom right corner.</p>
     *
     * @param result a 4D vector to place the result in.
     * @param x the screen space x pixel coordinate, in the range [0, {@code deviceWidth}]
     * @param y the screen space y pixel coordinate, in the range [0, {@code deviceHeight}]
     * @param ndcZ the desired z component of the screen space point when
     * transformed, in the range [-1, 1]
     * @param deviceWidth the width of the device the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @param deviceHeight the height of the device the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @see #screenToNDC(float[], float, float, float)
     */
    public static final void screenToNDC(float[] result, int x, int y,
            int deviceWidth, int deviceHeight, float ndcZ)
    {
        screenToNDC(result,
                x / ((float) deviceWidth),
                y / ((float) deviceHeight),
                ndcZ);
    }

    public static final void screenToNDC(float[] result, float[] screenPoint,
            int deviceWidth, int deviceHeight)
    {
        screenToNDC(result,
                screenPoint[0] / deviceWidth,
                screenPoint[1] / deviceHeight,
                screenPoint[2]);
    }
    /**
     * <p>Unprojects a screen space point into a ray from the near to far clipping
     * planes. As no depth can be assertained in unprojection it is the duty
     * of the user to interpolate the ray to find a desired world position.</p>
     *
     * <p>{@code x} and {@code y} are in parametric screen coordinates with
     * the origin in the upper left corner. Thus (0, 0) signifies the upper
     * left corner, whilst (1, 1) signifies the bottom right corner.</p>
     * 
     * @param camera the camera to unproject from.
     * @param cameraToLocal
     * @param x the screen space x coordinate, in the range [0, 1]
     * @param y the screen space y coordinate, in the range [0, 1]
     * @param near
     * @param far
     */
    public static void unproject(Camera camera, Transform cameraToLocal,
            float x, float y, float[] near, float[] far)
    {
        screenToNDC(near, x, y, -1);
        screenToNDC(far, x, y, 1);

        unproject(camera, cameraToLocal, near, far);
    }

    public static void unproject(Camera camera, Transform cameraToLocal,
            float[] near, float[] far)
    {
        Require.notNull(camera, "camera");
        Require.argumentHasCapacity(near, "near", 4);
        Require.argumentHasCapacity(far, "far", 4);
        Require.argumentInRange(near[2], "near.z", -1, 1);
        Require.argumentInRange(far[2], "far.z", -1, 1);

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

    public static void unproject(Camera camera, Transform cameraToLocal,
            float[] point)
    {
        Require.notNull(camera, "camera");
        Require.argumentHasCapacity(point, "near", 4);

        final Transform inverseProjection = new Transform();
        camera.getProjection(inverseProjection);
        inverseProjection.invert();

        inverseProjection.transform(point);

        final float invW = 1.0f / point[3];
        Vmath.vmul3(point, invW);
        point[3] = 1.0f;

        if (cameraToLocal != null)
        {
            cameraToLocal.transform(point);
        }
    }

    
    /**
     * <p>Computes a 3D local space offset corresponding to a given screen offset.
     * The 3D space offset is computed such that points at a given z depth 
     * (in eye coordinates) move a given distance in pixels along the x and y
     * directions in screen space. This is for example useful when dragging an
     * object in 3D space to ensure that the object stays under the cursor.</p>
     *
     * <p>Note: take care to ensure you are using the correct localToCamera
     * transform. This can trip people up as it is not the transform from the
     * camera to the local object but the inverse.</p>
     *
     * <ul>Below are some examples of correct use:
     * <li>Retained mode:
     *   <div style="background-color: #efefef">
     *      <pre>
     * {@code Camera camera = new Camera();
     * camera.translate(0, 0, 6);
     * 
     * Group group = new Group();
     * group.addChild(camera);
     *
     * Transform localToCamera = new Transform();
     * group.getTransformTo(camera, localToCamera);}</pre>
     *   </div>
     * </li>
     * <li>Immediate mode: (note the invert() call!)
     *   <div style="background-color: #efefef">
     *      <pre>
     * {@code Camera camera = new Camera();
     * camera.translate(0, 0, 6);
     *
     * Transform localToCamera = new Transform();
     * camera.getCompositeTransform(localToCamera);
     * localToCamera.invert();}</pre>
     *   </div>
     * </li>
     * </ul>
     *
     * @param camera The camera the scene is viewed through.
     * @param localToCamera The transformation to eye coordinates.
     * @param local A 3D point at the z depth (in eye coordinates) at which
     *        the screen projection of point translations
     *        correspond to the given screen translation.
     * @param dxScreen The desired translation along the screen x axis.
     * @param dyScreen The desired translation along the screen y axis.
     * @param deviceWidth The device width in pixels
     * @param deviceHeight The device height in pixels
     */
    public static void screenToLocalOffset(Camera camera,
            Transform localToCamera, float[] local,
            int dxScreen, int dyScreen,
            int deviceWidth, int deviceHeight)
    {
        //project the local space point to NDC
        final float[] ndcPoint = new float[4];
        Vmath.vmov4(ndcPoint, local);
        project(camera, localToCamera, ndcPoint);
        //apply the offset, mapped to ndc coordiantes
        ndcPoint[0] += 2 * dxScreen / ((float) deviceWidth);
        ndcPoint[1] -= 2 * dyScreen / ((float) deviceHeight);
        //check for clip space validity, disabled for now
        if (false && ndcIsClipped(ndcPoint))
        {
            return;
        }
        //clear w
        ndcPoint[3] = 1.0f;
        //unproject back to the local coordinate system
        Transform cameraToLocal = new Transform(localToCamera);
        cameraToLocal.invert();
        unproject(camera, cameraToLocal, ndcPoint);
        Vmath.vmov3(local, ndcPoint);
    }
}
