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

package texture.opengl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GLJPanel;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.ImageBase;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Texture;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.TextureCombiner;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.awt.BaseFrame;
import m3x.microedition.m3g.GeomUtils;
import m3x.microedition.m3g.TransformController;
import m3x.microedition.m3g.awt.BlenderTurntableCameraController;

/**
 * @author jgasseli
 */
public class Dot3Demo extends BaseFrame
{
    private static final int xyzAsRGB(float x, float y, float z)
    {
        final double lengthInv = 1.0 / Math.sqrt(x* x + y * y + z * z);
        x *= lengthInv;
        y *= lengthInv;
        z *= lengthInv;
        final int r = (int) ((x * 0.5) * 255 + 128);
        final int g = (int) ((y * 0.5) * 255 + 128);
        final int b = (int) ((z * 0.5) * 255 + 128);
        return (0x80 << 24) | (r << 16) | (g << 8) | (b << 0);
    }

    private final class Dot3Canvas extends GLJPanel
            implements Runnable, KeyListener
    {
        private Background background;
        private AbstractRenderTarget renderTarget;

        private Mesh plane;
        private Camera camera;
        private TransformController cameraController;

        private final Transform transform = new Transform();

        private Image2D dot3Image;
        private Texture2D dot3Texture;

        private Image2D diffuseImage;
        private Texture2D diffuseTexture;

        private Mesh light;
        private float lightYaw;
        private boolean dot3Enabled = true;

        public Dot3Canvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            try
            {
                InputStream imageStream = getClass().getResourceAsStream(
                        "brickround_normal.png");
                dot3Image = (Image2D) Loader.loadImage(
                        ImageBase.RGB | ImageBase.LOSSLESS,
                        imageStream);
                imageStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            dot3Texture = new Texture2D(dot3Image);
            dot3Texture.setFiltering(Texture.FILTER_LINEAR,
                    Texture.FILTER_ANISOTROPIC);
            TextureCombiner dot3Combiner = new TextureCombiner();
            dot3Combiner.setFunctions(TextureCombiner.DOT3_RGB, TextureCombiner.MODULATE);
            dot3Combiner.setColorSource(0, TextureCombiner.PREVIOUS);
            dot3Combiner.setColorSource(1, TextureCombiner.TEXTURE);
            dot3Combiner.setAlphaSource(0, TextureCombiner.PREVIOUS);
            dot3Combiner.setAlphaSource(1, TextureCombiner.TEXTURE);
            dot3Combiner.setScaling(1, 1);
            dot3Texture.setCombiner(dot3Combiner);

            try
            {
                InputStream imageStream = getClass().getResourceAsStream(
                        "brickround_diffuse.png");
                diffuseImage = (Image2D) Loader.loadImage(
                        ImageBase.RGB,
                        imageStream);
                imageStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            diffuseTexture = new Texture2D(diffuseImage);
            diffuseTexture.setFiltering(Texture.FILTER_LINEAR,
                    Texture.FILTER_ANISOTROPIC);
            diffuseTexture.setBlending(Texture2D.FUNC_MODULATE);

            {
                plane = GeomUtils.createPlane(1, 1, 2, 2);
                final VertexBuffer vb = plane.getVertexBuffer();
                //reuse the texcoords from unit 0 for unit 1
                vb.setTexCoords(1, vb.getTexCoords(0, null), 1.0f, null);
            }

            light = GeomUtils.createSphere(0.0125f, 7, 7);
            
            camera = new Camera();
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);

            addKeyListener(this);
            
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

                final float lightX = (float)(0.75 * Math.cos(lightYaw));
                final float lightY = (float)(0.75 * Math.sin(lightYaw));
                final float lightZ = 0.65f;
                lightYaw += 0.0125f;

                {
                    final VertexBuffer vb = plane.getVertexBuffer();
                    //color in this case is the light vector
                    vb.setDefaultColor(xyzAsRGB(
                            lightX,
                            lightY,
                            lightZ));
                }

                {
                    final Appearance a = plane.getAppearance(0);
                    if (dot3Enabled)
                    {
                        a.setTexture(0, dot3Texture);
                    }
                    else
                    {
                        a.setTexture(0, null);
                    }
                    a.setTexture(1, diffuseTexture);
                    transform.setIdentity();
                    g3d.render(plane, transform);
                }


                transform.setIdentity();
                transform.postTranslate(lightX, lightY, lightZ);
                g3d.render(light, transform);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            finally
            {
                g3d.releaseTarget();
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            g2d.setColor(Color.WHITE);
            g2d.drawString("MMB rotates view", 15, 15);
            g2d.drawString("- hold Shift to pan.", 15, 30);
            g2d.drawString("- hold Ctrl to dolly.", 15, 45);
            g2d.drawString("- Alt+LMB emulates MMB", 15, 60);
            g2d.drawString("'t' toggles DOT3 normal map", 15, 75);
        }

        public void run()
        {
            while (!isClosed())
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

        public void keyTyped(KeyEvent e)
        {
            
        }

        public void keyPressed(KeyEvent e)
        {
            System.out.println("keyPressed: " + e);
            if (e.getKeyChar() == 't')
            {
                dot3Enabled = !dot3Enabled;
            }
        }

        public void keyReleased(KeyEvent e)
        {
            
        }
    }

    Dot3Demo()
    {
        super("Dot3Demo");
        add(new Dot3Canvas());
    }

    public static void main(String[] args)
    {
        BaseFrame frame = new Dot3Demo();
        frame.present(false);
    }
}
