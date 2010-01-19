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

package m3x.microedition.m3g.awt;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.awt.BaseFrame;
import m3x.microedition.m3g.TransformController;
import m3x.microedition.m3g.XMLLoader;

/**
 * @author jgasseli
 */
public class FileViewer extends BaseFrame
{
    private final FileViewerCanvas canvas;
    private static final GLCapabilities GL_CAPS = new GLCapabilities();
    static
    {
        GL_CAPS.setSampleBuffers(true);
        GL_CAPS.setNumSamples(4);
    }
    
    private final class FileViewerCanvas extends GLCanvas
            implements Runnable
    {
        private Background background;
        private AbstractRenderTarget renderTarget;

        private Camera camera;
        private TransformController cameraController;

        private final Transform transform = new Transform();

        private Object3D[] roots;


        public FileViewerCanvas()
        {
            super(GL_CAPS);
            
            renderTarget = new GLRenderTarget(this);
            background = new Background();
            background.setColor(0x1f1f1f);

            camera = new Camera();
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);

            new Thread(this).start();
        }

        protected void setRoots(Object3D[] roots)
        {
            this.roots = roots;
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
                        1.0f, 100.0f);
                g3d.setViewport(0, 0, getWidth(), getHeight());
                cameraController.update(1.0 / getRefreshRate());
                g3d.setCamera(camera, cameraController.getTransform());

                g3d.clear(background);

                transform.setIdentity();
                //copy to avoid concurrency issues
                final Object3D[] rootsArray = this.roots;
                if (rootsArray != null)
                {
                    for (Object3D root : rootsArray)
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
                    Thread.yield();
                }
                repaint();
            }
        }
    }

    private void openAction()
    {
        FileDialog fd = new FileDialog(this, "", FileDialog.LOAD);
        fd.setVisible(true);

        final String filename = fd.getFile();
        if (filename != null)
        {
            final File file = new File(fd.getDirectory(), filename);
            try
            {
                InputStream stream = new FileInputStream(file);
                if (filename.endsWith("m3g"))
                {
                    //probably a binary file
                    canvas.setRoots(Loader.load(stream));
                }
                else
                {
                    canvas.setRoots(XMLLoader.load(stream));
                }
                stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private final void initFileMenu(Menu menu)
    {
        MenuItem openItem = new MenuItem("Open",
                new MenuShortcut(KeyEvent.VK_O));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                openAction();
            }
        });
        menu.add(openItem);
    }

    private final void initWindowMenu(Menu menu)
    {
        MenuItem toggleFullscreenItem = new MenuItem("Toggle Fullscreen",
                new MenuShortcut(KeyEvent.VK_F));
        toggleFullscreenItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                toggleFullscreen();
            }
        });
        menu.add(toggleFullscreenItem);
    }

    private final void initMenu()
    {
        MenuBar menuBar = getMenuBar();
        if (menuBar == null)
        {
            menuBar = new MenuBar();
            setMenuBar(menuBar);
        }

        Menu fileMenu = new Menu("File");
        menuBar.add(fileMenu);
        initFileMenu(fileMenu);

        Menu windowMenu = new Menu("Window");
        menuBar.add(windowMenu);
        initWindowMenu(windowMenu);
    }

    FileViewer()
    {
        super("FileViewer");
        canvas = new FileViewerCanvas();
        add(canvas);

        initMenu();
    }

    public static void main(String[] args)
    {
        BaseFrame frame = new FileViewer();
        frame.present(false);
    }
}
