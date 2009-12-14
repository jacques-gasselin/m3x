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

package camera.opengl;

import java.awt.Graphics;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.microedition.m3g.GeomUtils;
import m3x.microedition.m3g.TransformController;
import m3x.microedition.m3g.awt.BlenderTurntableCameraController;
import util.DemoFrame;

/**
 * @author jgasseli
 */
public class CameraControllerDemo extends DemoFrame
{
    private final class CameraControllerCanvas extends GLCanvas
            implements Runnable
    {
        private static final int NO_LIGHT_SCOPE = 1;
        private static final int LIGHT0_SCOPE = 2;

        private Background background;
        private AbstractRenderTarget renderTarget;

        private Light light;
        private Mesh lightSphere;
        private final Transform lightTransform = new Transform();
        private Mesh sphere;
        private Camera camera;
        private TransformController cameraController;

        private float angle;
        private final Transform transform = new Transform();

        public CameraControllerCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            final int lightColor = 0xffefcfcf;
            light = new Light();
            light.setScope(LIGHT0_SCOPE);
            light.setMode(Light.OMNI);
            light.setColor(lightColor);
            light.setAttenuation(1.0f, 0.125f, 0.0f);
            lightTransform.setIdentity();
            lightTransform.postTranslate(0, 1, 1);

            {
                lightSphere = GeomUtils.createSphere(0.02f, 12, 12);
                lightSphere.setScope(NO_LIGHT_SCOPE);
                lightSphere.getVertexBuffer().setDefaultColor(lightColor);
            }

            {
                sphere = GeomUtils.createSphere(0.5f, 48, 48);
                sphere.getVertexBuffer().setDefaultColor(0xff3f3f3f);
                final Appearance a = sphere.getAppearance(0);
                final Material m = new Material();
                m.setColor(Material.DIFFUSE, 0xff7f6f7f);
                m.setColor(Material.SPECULAR, 0xff8f6f4f);
                m.setShininess(20);
                a.setMaterial(m);
                final PolygonMode pm = new PolygonMode();
                pm.setLocalCameraLightingEnable(true);
                a.setPolygonMode(pm);
            }

            camera = new Camera();
            camera.setScope(NO_LIGHT_SCOPE | LIGHT0_SCOPE);
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);

            new Thread(this).start();
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            Graphics3D g3d = Graphics3D.getInstance();

            try
            {
                g3d.bindTarget(renderTarget);
                camera.setPerspective(50,
                        getWidth() / (float)getHeight(),
                        0.1f, 10.0f);
                g3d.setViewport(0, 0, getWidth(), getHeight());
                cameraController.update(1.0 / getRefreshRate());
                g3d.setCamera(camera, cameraController.getTransform());

                g3d.clear(background);

                g3d.resetLights();
                g3d.addLight(light, lightTransform);
                
                transform.setIdentity();
                transform.postRotate(-90, 1, 0, 0);
                g3d.render(sphere.getVertexBuffer(),
                        sphere.getIndexBuffer(0),
                        sphere.getAppearance(0),
                        transform,
                        sphere.getScope());

                g3d.render(lightSphere.getVertexBuffer(),
                        lightSphere.getIndexBuffer(0),
                        lightSphere.getAppearance(0),
                        lightTransform,
                        lightSphere.getScope());
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            finally
            {
                g3d.releaseTarget();
            }
        }

        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(1000 / getRefreshRate());
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                repaint();
            }
        }
    }

    CameraControllerDemo()
    {
        super("CameraControllerDemo");
        add(new CameraControllerCanvas());
    }

    public static void main(String[] args)
    {
        DemoFrame frame = new CameraControllerDemo();
        frame.present(false);
    }
}
