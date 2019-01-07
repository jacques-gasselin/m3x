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
import com.jogamp.opengl.awt.GLJPanel;
import java.awt.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Transformable;
import javax.microedition.m3g.World;
import javax.microedition.m3g.opengl.GLRenderTarget;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import m3x.awt.BaseFrame;
import m3x.microedition.m3g.TransformController;
import m3x.microedition.m3g.XMLLoader;

/**
 * @author jgasseli
 */
public class FileViewer extends BaseFrame
{
    private static final long serialVersionUID = 1L;

    private FileViewerCanvas canvas;
    private JTree treeView;
    private TreeNode root;

    private static boolean isRenderingEnabled(Node node)
    {
        while (node != null)
        {
            if (!node.isRenderingEnabled())
            {
                return false;
            }
            node = node.getParent();
        }
        return true;
    }

    private final class FileViewerCanvas extends GLJPanel
    {
        private static final long serialVersionUID = 1L;
        
        private final Background background;
        private final AbstractRenderTarget renderTarget;

        private final Camera camera;
        private final TransformController cameraController;

        private final Transform transform = new Transform();

        private Object3D[] roots;

        private float hue;
        
        public FileViewerCanvas()
        {
            renderTarget = new GLRenderTarget(this, true);
            background = new Background();
            background.setColor(0x1f1f1f);

            camera = new Camera();
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);
        }

        protected void setRoots(Object3D[] roots)
        {
            this.roots = roots;
        }
        
        public Object3D[] getRoots()
        {
            return roots;
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
                g3d.resetLights();

                transform.setIdentity();
                //copy to avoid concurrency issues
                final Object3D[] rootsArray = this.roots;
                if (rootsArray != null)
                {
                    for (Object3D root : rootsArray)
                    {
                        if (root instanceof World)
                        {
                            final World world = (World) root;
                            //override the camera
                            final Camera oldCamera = world.getActiveCamera();
                            world.setActiveCamera(camera);
                            camera.setTransform(cameraController.getTransform());
                            world.addChild(camera);
                            g3d.render((Node)world, transform);
                            world.removeChild(camera);
                            world.setActiveCamera(oldCamera);
                        }
                        else if (root instanceof Light)
                        {
                            final Light light = (Light) root;
                            final Transform lightTransform = new Transform();
                            light.getCompositeTransform(lightTransform);
                            g3d.addLight(light, lightTransform);
                        }
                        else if (root instanceof Node)
                        {
                            final Node rootNode = (Node) root;
                            final Object3D[] lights = rootNode.findAll(Light.class);
                            if (lights.length > 0)
                            {
                                final Transform lightTransform = new Transform();
                                for (int i = 0; i < lights.length; ++i)
                                {
                                    final Light light = (Light) lights[i];
                                    if (isRenderingEnabled(light))
                                    {
                                        light.getTransformTo(rootNode, lightTransform);
                                        g3d.addLight(light, lightTransform);
                                    }
                                }
                            }
                            
                            g3d.render((Node) root, transform);
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace(System.err);
            }
            finally
            {
                g3d.releaseTarget();
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
                
                if (treeView != null)
                {
                    root = null;
                    populateTreeView();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(System.out);
            }
        }
    }

    private void populateTreeViewObject3D(DefaultMutableTreeNode treeNode, Object3D obj)
    {
        if (obj.getUserID() != 0)
        {
            treeNode.add(new DefaultMutableTreeNode("userID : " + obj.getUserID(), false));
        }
    }
    
    private void populateTreeViewTransformable(DefaultMutableTreeNode treeNode, Transformable obj)
    {
        populateTreeViewObject3D(treeNode, obj);
        
        float[] v = new float[3];
        obj.getTranslation(v);
        if (v[0] != 0.0f || v[1] != 0.0f || v[2] != 0.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("translation : [ " + v[0] + ", " + v[1] + ", " + v[2] + " ]", false));
        }
        
        obj.getScale(v);
        if (v[0] != 1.0f || v[1] != 1.0f || v[2] != 1.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("scale : [ " + v[0] + ", " + v[1] + ", " + v[2] + " ]"));
        }

    }

    private void populateTreeViewNode(DefaultMutableTreeNode treeNode, Node obj)
    {
        populateTreeViewTransformable(treeNode, obj);
        
        if (obj.getScope() != ~0)
        {
            treeNode.add(new DefaultMutableTreeNode("scope : " + obj.getScope()));
        }

        if (obj.getAlphaFactor() != 1.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("alphaFactor : " + obj.getAlphaFactor()));
        }
    }
    
    private void populateTreeViewMesh(DefaultMutableTreeNode treeNode, Mesh obj)
    {
        populateTreeViewNode(treeNode, obj);
        
        if (obj.getVertexBuffer() != null)
        {
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(obj.getVertexBuffer());
            treeNode.add(n);
            populateTreeViewObject3D(n, obj.getVertexBuffer());
        }
        
        if (obj.getSubmeshCount() > 0)
        {
            for (int i = 0; i < obj.getSubmeshCount(); ++i)
            {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode("submeshes[" + i + "]");
                treeNode.add(n);

                DefaultMutableTreeNode ib = new DefaultMutableTreeNode(obj.getIndexBuffer(i));
                n.add(ib);

                DefaultMutableTreeNode a = new DefaultMutableTreeNode(obj.getAppearance(i));
                n.add(a);
            }
        }

        if (obj.getAlphaFactor() != 1.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("alphaFactor : " + obj.getAlphaFactor()));
        }
    }

    private void populateTreeViewGroup(DefaultMutableTreeNode treeNode, Group g)
    {
        populateTreeViewNode(treeNode, g);
        
        if (g.getChildCount() > 0)
        {
            for (int i = 0; i < g.getChildCount(); ++i)
            {
                Node child = g.getChild(i);
                DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(child);
                treeNode.add(childTreeNode);
                if (child instanceof Group)
                {
                    populateTreeViewGroup(childTreeNode, (Group) child);
                }
                else if (child instanceof Mesh)
                {
                    populateTreeViewMesh(childTreeNode, (Mesh) child);
                }
            }
        }
    }
    
    private void populateTreeView()
    {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("scene");
        
        if (canvas.getRoots() != null)
        {
            for (Object3D root : canvas.getRoots())
            {
                DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(root);
                top.add(rootTreeNode);
                if (root instanceof Group)
                {
                    populateTreeViewGroup(rootTreeNode, (Group) root);
                }
                else if (root instanceof Mesh)
                {
                    populateTreeViewMesh(rootTreeNode, (Mesh) root);
                }
                else if (root instanceof Node)
                {
                    populateTreeViewNode(rootTreeNode, (Node) root);
                }
                else
                {
                    populateTreeViewObject3D(rootTreeNode, root);
                }
            }
        }
        
        root = top;
        
        treeView.setModel(new DefaultTreeModel(root));
    }

    private void toggleTreeView()
    {
        if (treeView == null)
        {
            getContentPane().removeAll();
            
            treeView = new JTree();
            treeView.setSize(getWidth() / 5, getHeight());
            canvas.setSize((getWidth() * 4 / 5), getHeight());

            populateTreeView();
            
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.setBorder(BorderFactory.createEmptyBorder());
            split.setResizeWeight(0.8);
            
            JScrollPane treeScroll = new JScrollPane(treeView);
            
            split.setLeftComponent(canvas);
            split.setRightComponent(treeScroll);
            
            split.setSize(getWidth(), getHeight());
            split.setDividerLocation(0.8);

            getContentPane().add(split);
        }
        else
        {
            getContentPane().removeAll();
            canvas.setSize(getWidth(), getHeight());
            getContentPane().add(canvas);
            
            treeView = null;
        }
        validate();
    }
    
    private void initFileMenu(Menu menu)
    {
        MenuItem openItem = new MenuItem("Open",
                new MenuShortcut(KeyEvent.VK_O));
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                openAction();
            }
        });
        menu.add(openItem);
    }

    private void initWindowMenu(Menu menu)
    {
        MenuItem toggleFullscreenItem = new MenuItem("Toggle Fullscreen",
                new MenuShortcut(KeyEvent.VK_F));
        toggleFullscreenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                toggleFullscreen();
            }
        });

        MenuItem toggleTreeViewItem = new MenuItem("Toggle TreeView",
                new MenuShortcut(KeyEvent.VK_T));
        toggleTreeViewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                toggleTreeView();
            }
        });
        
        menu.add(toggleFullscreenItem);
        menu.add(toggleTreeViewItem);
    }

    private void initMenu()
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
    
    private ScheduledExecutorService frameService;
    
    private final JPanel contentPane = new JPanel();
    
    private JPanel getContentPane()
    {
        return contentPane;
    }
    
    FileViewer()
    {
        super("FileViewer");
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                add(getContentPane());
                getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
                
                canvas = new FileViewerCanvas();
                getContentPane().add(canvas);

                initMenu();
                
                frameService = Executors.newSingleThreadScheduledExecutor();
                frameService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run()
                    {
                        canvas.repaint();
                    }
                }, 8, 8, TimeUnit.MILLISECONDS);
            }
        });
    }

    @Override
    protected void close()
    {
        super.close();
        frameService.shutdown();
    }
    
    public static void main(String[] args)
    {
        BaseFrame frame = new FileViewer();
        frame.present(false);
    }
}
