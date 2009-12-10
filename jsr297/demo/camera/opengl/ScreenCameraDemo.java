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
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.IndexBuffer;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.opengl.GLRenderTarget;
import util.DemoFrame;

/**
 * @author jgasseli
 */
public class ScreenCameraDemo extends DemoFrame
{
    private final class ScreenCameraCanvas extends GLCanvas
            implements Runnable
    {
        private Background background;
        private AbstractRenderTarget renderTarget;

        private VertexBuffer vertexBuffer;
        private IndexBuffer primitives;
        private Appearance appearance;
        private Camera camera;
        private final Transform cameraTransform = new Transform();

        private float angle;
        private final Transform transform = new Transform();

        public ScreenCameraCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            vertexBuffer = new VertexBuffer();
            vertexBuffer.setDefaultColor(0xffff0000);
            VertexArray positions = new VertexArray(4, 2, VertexArray.FLOAT);
            positions.set(0, 1, new float[]{ 100, 100 });
            positions.set(1, 1, new float[]{ 100, 500 });
            positions.set(2, 1, new float[]{ 500, 500 });
            positions.set(3, 1, new float[]{ 500, 100 });
            vertexBuffer.setPositions(positions, 1.0f, null);

            VertexArray colors = new VertexArray(4, 3, VertexArray.BYTE);
            colors.set(0, 1, new byte[]{ 0, 0, 0 });
            colors.set(1, 1, new byte[]{ (byte)255, 0, 0 });
            colors.set(2, 1, new byte[]{ 0, (byte)255, 0 });
            colors.set(3, 1, new byte[]{ 0, 0, (byte)255 });
            vertexBuffer.setColors(colors);

            primitives = new IndexBuffer(IndexBuffer.TRIANGLES, 2,
                    new int[] {
                0, 1, 2,
                2, 3, 0,
            });

            appearance = new Appearance();
            PolygonMode pm = new PolygonMode();
            pm.setCulling(PolygonMode.CULL_NONE);
            appearance.setPolygonMode(pm);

            camera = new Camera();
            camera.setScreen(0, 0, 800, 600);

            camera.getCompositeTransform(cameraTransform);
            
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
                camera.setScreen(0, 0, getWidth(), getHeight());
                g3d.setViewport(0, 0, getWidth(), getHeight());
                //flip y as the OpenGL screen has a bottom left origin
                cameraTransform.setIdentity();
                cameraTransform.postTranslate(0, getHeight(), 0);
                cameraTransform.postScale(1, -1, 1);
                g3d.setCamera(camera, cameraTransform);
                
                g3d.clear(background);

                transform.setIdentity();
                //pivot rotate about the center
                transform.postTranslate(310, 310, 0);
                transform.postRotate(angle, 0, 0, 1);
                angle += 0.25f;
                transform.postTranslate(-310, -310, 0);
                g3d.render(vertexBuffer, primitives, appearance, transform);

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

    private final class WindowAdapter extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    ScreenCameraDemo()
    {
        super("ScreenCameraDemo");
        add(new ScreenCameraCanvas());
    }

    public static void main(String[] args)
    {
        DemoFrame frame = new ScreenCameraDemo();
        frame.present(false);
    }
}
