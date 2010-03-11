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
 * <li>Project points in the local space to eye space. This is useful for many
 * screen space or eye space algorithms.</li>
 * <li>Unproject screen coordinates into rays going from the near clip to the
 * far clip.</li>
 * <li>Transform a local space point along a screen aligned plane that preserves
 * the eye space depth. Useful for pan and drag.
 * <li>Find the local space point where a screen ray intersects a user defined
 * plane. Useful for screen aligned transforms constrained on a plane.</li>
 * </ul>
 *
 * 
 * <h4><a id="localToCameraTransform">Local to Camera Transform</a></h4>
 * <p>For many of the algorithms below there is a need for a transform from the
 * local space of an object to the camera's local space. Getting this transform
 * correct is of utmost importance for the proper functioning of these algorithms.
 * </p>
 *
 * <ul>Below are some examples of correct use:
 * <li>Retained mode:
 *   <div style="border:1px solid #f0f0f0; background-color:#f8f8f8">
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
 *   <div style="border:1px solid #f0f0f0; background-color:#f8f8f8">
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
     * @param width the width of the device, in pixels.
     * @param height the height of the device, in pixels.
     */
    public static final void ndcToScreen(float[] result, float[] ndc,
            int width, int height)
    {
        ndcToScreen(result, ndc);
        result[0] *= width;
        result[1] *= height;
    }
    
    /**
     * <p>Projects a position in the local space of the coordinate system, localToCamera,
     * without the projected w value divided out. The position is transformed from
     * world space to eye space. The user must do the viewport transform separately
     * if actual screen pixel position is needed.</p>
     *
     * <p>Note: take care to ensure you are using the correct localToCamera
     * transform. This can trip people up as it is not the transform from the
     * camera to the local object but the inverse. See the section on
     * <a href="#localToCameraTransform">correct local to camera transforms</a>
     * in the class documentation for more information.</p>
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
     * with the projected w value divided out. The w value is set to 1.0
     * and the rest of the components (x, y, z) are affected as well.</p>
     *
     * <p>If this is not desired; use a method with a contract leave components
     * untouched in regards to the perspective divide.</p>
     *
     * <p>The position is transformed from local space to eye space.
     * The user must do the viewport transform separately if actual screen
     * pixel position is needed.</p>
     *
     * <p>Note: take care to ensure you are using the correct localToCamera
     * transform. This can trip people up as it is not the transform from the
     * camera to the local object but the inverse. See the section on
     * <a href="#localToCameraTransform">correct local to camera transforms</a>
     * in the class documentation for more information.</p>
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
        position[3] = 1.0f;
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
     * @param width the width of the area the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @param height the height of the area the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @see #screenToNDC(float[], float, float, float)
     */
    public static final void screenToNDC(float[] result, int x, int y,
            int width, int height, float ndcZ)
    {
        screenToNDC(result,
                x / ((float) width),
                y / ((float) height),
                ndcZ);
    }

    /**
     * <p>Transforms a screen space point into a normalized device coordinate.</p>
     *
     * <p>{@code screenPoint} x, y components are in pixel screen coordinates with
     * the origin in the upper left corner. Thus (0, 0) signifies the upper
     * left corner, whilst ({@code deviceWidth}, @{code deviceHeight})
     * signifies the bottom right corner. The third element in {@code screenPoint}
     * is the normalized device coordinate z value to be used.</p>
     *
     * @param result a 4D vector to place the result in.
     * @param screenPoint a 3D vector containing the screen space (x, y) pixel
     * coordinates, in the range [0, {@code deviceWidth}], and the normalized
     * device coordinate z value as the z component.
     * @param width the width of the area the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @param height the height of the area the pixel coordinates are relative to.
     * This is used to find the parametric screen coordiantes.
     * @see #screenToNDC(float[], int, int, int, int, float)
     */
    public static final void screenToNDC(float[] result, float[] screenPoint,
            int width, int height)
    {
        screenToNDC(result,
                screenPoint[0] / width,
                screenPoint[1] / height,
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
     * @param near the 4D result vector corresponding to the near clip point.
     * @param far the 4D result vector corresponding to the far clip point.
     * @see #unproject(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform, float[], float[])
     */
    public static void unproject(Camera camera, Transform cameraToLocal,
            float x, float y, float[] near, float[] far)
    {
        screenToNDC(near, x, y, -1);
        screenToNDC(far, x, y, 1);

        unproject(camera, cameraToLocal, near, far);
    }

    public static void unproject(Camera camera, Transform cameraToLocal,
            int x, int y, int width, int height, float[] near, float[] far)
    {
        screenToNDC(near, x, y, width, height, -1);
        screenToNDC(far, x, y, width, height, 1);

        unproject(camera, cameraToLocal, near, far);
    }
    
    /**
     * <p>Unprojects a pair of normalized device coordinate points into local
     * space points. This is most often used for getting a ray in local space
     * from a pair of ndc space points</p>
     *
     * @param camera the camera to unproject from.
     * @param cameraToLocal
     * @param near the 4D vector in ndc space that is assumed to be the nearer
     * of the two to the near clip.
     * @param far the 4D vector in ndc space that is assumed to be the nearer
     * of the two to the far clip.
     */
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

    /**
     * <p>Unprojects a normalized device coordinate point into local
     * space. This is most often used for moving a point in local space along
     * an ndc space plane.</p>
     *
     * @param camera the camera to unproject from.
     * @param cameraToLocal
     * @param point the 4D input and result. The input values are in ndc space.
     * The output values are in local space.
     */
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
     * camera to the local object but the inverse. See the section on
     * <a href="#localToCameraTransform">correct local to camera transforms</a>
     * in the class documentation for more information.</p>
     * 
     * @param camera The camera the scene is viewed through.
     * @param localToCamera The transformation to eye coordinates.
     * @param local A 3D point at the z depth (in eye coordinates) at which
     *        the screen projection of point translations
     *        correspond to the given screen translation.
     * @param dx the desired translation along the screen x axis.
     * @param dy the desired translation along the screen y axis.
     * @param width the device width in pixels
     * @param height the device height in pixels
     */
    public static void screenToLocalOffset(Camera camera,
            Transform localToCamera, float[] local,
            int dx, int dy, int width, int height)
    {
        //project the local space point to NDC
        final float[] ndcPoint = new float[4];
        Vmath.vmov4(ndcPoint, local);
        project(camera, localToCamera, ndcPoint);
        //apply the offset, mapped to ndc coordiantes
        ndcPoint[0] += 2 * dx / ((float) width);
        ndcPoint[1] -= 2 * dy / ((float) height);
        //check for clip space validity, disabled for now
        /*if (ndcIsClipped(ndcPoint))
        {
            return;
        }*/
        //unproject back to the local coordinate system
        final Transform cameraToLocal = new Transform(localToCamera);
        cameraToLocal.invert();
        unproject(camera, cameraToLocal, ndcPoint);
        Vmath.vmov3(local, ndcPoint);
    }


    /**
     * <p>Computes a 3D local space coordinate corresponding to a given screen
     * coordinate that is on the screen aligned plane that contains {@code local}.
     * The local space coordinate is computed such that points at a given
     * z depth (in eye coordinates) will still retain that z depth at
     * the new local coordinate. In effect this moves a local coordinate to
     * a corresponding position projected from the x and y coordinates in screen
     * space. This is for example useful when placing or dragging an object in
     * 3D space to ensure that the object stays under the cursor.</p>
     *
     * <p>Note: take care to ensure you are using the correct localToCamera
     * transform. This can trip people up as it is not the transform from the
     * camera to the local object but the inverse. See the section on
     * <a href="#localToCameraTransform">correct local to camera transforms</a>
     * in the class documentation for more information.</p>
     *
     * @param camera The camera the scene is viewed through.
     * @param localToCamera The transformation to eye coordinates.
     * @param local A 3D point at the z depth (in eye coordinates) at which
     *        the screen projection of point translations
     *        correspond to the given screen translation.
     * @param x the desired x coordinate in screen space.
     * @param y the desired y corrdinate in screen space.
     * @param width the device width in pixels
     * @param height the device height in pixels
     */
    public static void screenToLocal(Camera camera,
            Transform localToCamera, float[] local,
            int x, int y, int width, int height)
    {
        //project the local space point to NDC
        final float[] ndcPoint = new float[4];
        Vmath.vmov4(ndcPoint, local);
        project(camera, localToCamera, ndcPoint);
        final float ndcZ = ndcPoint[2];
        //apply the screen position, mapped to ndc coordiantes
        screenToNDC(ndcPoint,
                x, y,
                width, height,
                ndcZ);
        //check for clip space validity, disabled for now
        /*if (ndcIsClipped(ndcPoint))
        {
            return;
        }*/
        //unproject back to the local coordinate system
        final Transform cameraToLocal = new Transform(localToCamera);
        cameraToLocal.invert();
        unproject(camera, cameraToLocal, ndcPoint);
        Vmath.vmov3(local, ndcPoint);
    }

    /**
     * <p>Computes a 3D local space coordinate corresponding to a given screen
     * coordinate that is on the local space plane, {@code local}. This is for
     * example useful when placing or dragging an object in 3D space to ensure
     * that the object stays under the cursor whilst being on the plane.</p>
     *
     * <p>Note: take care to ensure you are using the correct cameraToLocal
     * transform. See the section on
     * <a href="#cameraToLocalTransform">correct camera to local transforms</a>
     * in the class documentation for more information.</p>
     *
     * @param camera The camera the scene is viewed through.
     * @param cameraToLocal The transformation from eye coordinates to local
     * coordinates.
     * @param plane a plane in local space which is used to unproject the screen
     * space coordinates onto.
     * @param x the desired x coordinate in screen space.
     * @param y the desired y corrdinate in screen space.
     * @param width the device width in pixels
     * @param height the device height in pixels
     * @param position the 4D result vector, position in local space.
     */
    public static void screenToLocalPlane(Camera camera,
            Transform cameraToLocal, float[] plane,
            int x, int y, int width, int height,
            float[] position)
    {
        //unproject the screen point to a near-to-far ray
        final float[] near = new float[4];
        final float[] far = new float[4];
        unproject(camera, cameraToLocal, x, y, width, height, near, far);
        final float nearDot = Vmath.vdot3(plane, near);
        final float farDot = Vmath.vdot3(plane, far);
        //find the intepolant for the intersection of the line:
        //near * (1 - s) + far * s
        final float s = -(nearDot + plane[3]) / (farDot - nearDot);
        Vmath.vlerp3(position, near, far, s);
        position[3] = 1.0f;
    }
}
