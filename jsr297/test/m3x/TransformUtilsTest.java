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

package m3x;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;
import m3x.microedition.m3g.TransformUtils;

/**
 * @author jgasseli
 */
public class TransformUtilsTest extends AbstractTestCase
{
    public void testProjectUnproject()
    {
        final Camera camera = new Camera();
        camera.setPerspective(60, 1, 1.0f, 100.0f);
        
        final float[] point = new float[4];
        Vmath.vload4(point, -1, 2, -12, 1);

        final Transform localToCamera = new Transform();
        camera.getCompositeTransform(localToCamera);
        
        //project the local space point to NDC
        final float[] ndcPoint = new float[4];
        Vmath.vmov4(ndcPoint, point);
        TransformUtils.project(camera, localToCamera, ndcPoint);
        //the point above is definitely in the camera frustum
        assertTrue(ndcPoint[0] >= -1 && ndcPoint[0] <= 1);
        assertTrue(ndcPoint[1] >= -1 && ndcPoint[1] <= 1);
        assertTrue(ndcPoint[2] >= -1 && ndcPoint[2] <= 1);
        //unproject the ndc to local space
        final float[] unprojPoint = new float[4];
        Vmath.vmov4(unprojPoint, ndcPoint);
        unprojPoint[3] = 1.0f;
        Transform cameraToLocal = new Transform(localToCamera);
        cameraToLocal.invert();
        TransformUtils.unproject(camera, cameraToLocal, unprojPoint);

        //confirm they are indeed the same
        assertEquals(point, unprojPoint, 0.001f);
    }

    public void testScreenToNDCAndBack()
    {
        final int width = 800;
        final int height = 600;
        final float[] screenPoint = new float[4];
        Vmath.vload4(screenPoint, 200, 100, 0, 1);
        final float[] ndcPoint = new float[4];
        TransformUtils.screenToNDC(ndcPoint, screenPoint, width, height);
        //map back to screen
        final float[] screenPoint2 = new float[4];
        TransformUtils.ndcToScreen(screenPoint2, ndcPoint, width, height);

        //confirm they are indeed the same
        assertEquals(screenPoint, screenPoint2, 0.001f);
    }

    public void testNDCToScreenAndBack()
    {
        final int width = 800;
        final int height = 600;
        final float[] ndcPoint = new float[4];
        Vmath.vload4(ndcPoint, 200, 100, 0, 1);
        final float[] screenPoint = new float[4];
        TransformUtils.ndcToScreen(screenPoint, ndcPoint, width, height);
        //map back to ndc
        final float[] ndcPoint2 = new float[4];
        TransformUtils.screenToNDC(ndcPoint2, screenPoint, width, height);

        //confirm they are indeed the same
        assertEquals(ndcPoint, ndcPoint2, 0.001f);
    }
}
