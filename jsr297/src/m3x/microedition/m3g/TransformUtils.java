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
     * <p>Projects a position in the local space of the coordinate system, localToWorld,
     * without the projected w value divided out. The position is transformed from
     * world space to eye space. The user must do the viewport transform separately
     * if actual screen pixel position is needed.</p>
     *
     * @param camera the camera to use for projecting
     * @param localToWorld the local coordinate system, or null to use the world
     * coordinate system (in effect the identity transform).
     * @param position the position to project.
     * @throws NullPointerException if camera is null
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position.length < 4
     * @see #project(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform, float[])
     */
    public static void projectWithoutPerspectiveDivide(Camera camera,
            Transform localToWorld, float[] position)
    {
        Require.notNull(camera, "camera");
        Require.argumentHasCapacity(position, "position", 4);

        if (localToWorld != null)
        {
            localToWorld.transform(position);
        }

        final Transform projection = new Transform();
        camera.getProjection(projection);
        projection.transform(position);
    }

    /**
     * <p>Projects a position in the local space of the coordinate system, localToWorld,
     * with the projected w value divided out. The w coordinate is left untouched,
     * but the rest of the components (x, y, z) are affected.</p>
     *
     * <p>If this is not desired; use a method with a contract leave components
     * untouched in regards to the perspective divide.</p>
     *
     * <p>The position is transformed from world space to eye space.
     * The user must do the viewport transform separately if actual screen
     * pixel position is needed.</p>
     *
     * @param camera the camera to use for projecting
     * @param localToWorld the local coordinate system, or null to use the world
     * coordinate system (in effect the identity transform).
     * @param position the position to project.
     * @throws NullPointerException if camera is null
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position.length < 4
     * @see #projectWithoutPerspectiveDivide(javax.microedition.m3g.Camera, javax.microedition.m3g.Transform, float[])
     */
    public static void project(Camera camera,
            Transform localToWorld, float[] position)
    {
        projectWithoutPerspectiveDivide(camera, localToWorld, position);

        //do the perspective divide
        final float invW = 1.0f / position[3];
        position[0] *= invW;
        position[1] *= invW;
        position[2] *= invW;
    }
}
