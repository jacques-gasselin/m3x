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

package loader.opengl;

import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.microedition.m3g.TransformController;
import m3x.microedition.m3g.awt.BlenderTurntableCameraController;
import util.DemoFrame;

/**
 * @author jgasseli
 */
public abstract class AbstractLoaderDemo extends DemoFrame
{
    protected final class LoaderCanvas extends GLCanvas
            implements Runnable
    {
        private Background background;
        private AbstractRenderTarget renderTarget;

        private Camera camera;
        private TransformController cameraController;

        private final Transform transform = new Transform();

        private Object3D[] roots;

        public LoaderCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            camera = new Camera();
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);

            new Thread(this).start();
        }

        protected void load(InputStream stream)
        {
            try
            {
                roots = Loader.load(stream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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

                transform.setIdentity();
                if (roots != null)
                {
                    for (Object3D root : roots)
                    {
                        if (root instanceof Node)
                        {
                            g3d.render((Node) root, transform);
                        }
                    }
                }
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

    protected LoaderCanvas canvas;

    AbstractLoaderDemo()
    {
        super();
        canvas = new LoaderCanvas();
        add(canvas);
    }
}
