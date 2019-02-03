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
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.microedition.m3g.AnimationTrack;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.AppearanceBase;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.SkinnedMesh;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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
        private final GLRenderTarget renderTarget;
        private PrintStream traceStream;

        private final Camera camera;
        private final TransformController cameraController;

        private final Transform transform = new Transform();

        private Object3D[] roots;

        private int animationWorldTime = -1;
        private float hue;
        
        public FileViewerCanvas()
        {
            renderTarget = new GLRenderTarget(this, true);
            background = new Background();
            background.setColor(0x1f1f1f);

            camera = new Camera();
            cameraController = new BlenderTurntableCameraController(camera, this,
                    0, 0, 3);
            
            traceStream = null;
        }
        
        public void setTraceStream(PrintStream stream)
        {
            traceStream = stream;
        }
        
        public boolean isAnimating()
        {
            return animationWorldTime >= 0;
        }
        
        public void setAnimating(boolean enabled)
        {
            if (enabled)
            {
                animationWorldTime = 0;
            }
            else
            {
                animationWorldTime = -1;
            }
        }

        protected void setRoots(Object3D[] roots)
        {
            this.roots = roots;
        }
        
        public Object3D[] getRoots()
        {
            return roots;
        }
        
        private void addAllLights(Node node, Graphics3D g3d)
        {
            final Transform lightTransform = new Transform();

            final HashSet<Node> closedList = new HashSet<Node>();
            final ArrayList<Node> openList = new ArrayList<Node>();

            //since all operations only touch the end of the list;
            //this is a depth first search. Breath first search would
            //require the front to be removed for each iteration
            openList.add(node);
            //this is an exhaustive search
            while (openList.size() > 0)
            {
                final Node candidate = openList.remove(openList.size() - 1);
                //skip objects already visited
                if (!closedList.contains(candidate))
                {
                    //count it as visited now
                    closedList.add(candidate);
                    //is it renderable
                    if (candidate.isRenderingEnabled())
                    {
                        //is it a light
                        if (candidate instanceof Light)
                        {
                            final Light light = (Light) candidate;
                            light.getTransformTo(node, lightTransform);
                            g3d.addLight(light, lightTransform);
                        }
                        else if (candidate instanceof Group)
                        {
                            final Group group = (Group) candidate;
                            //add the children from the candidate to the open list
                            final int count = group.getChildCount();
                            for (int i = 0; i < count; ++i)
                            {
                                openList.add(group.getChild(i));
                            }
                        }
                        else if (candidate instanceof SkinnedMesh)
                        {
                            final SkinnedMesh mesh = (SkinnedMesh) candidate;
                            if (mesh.getSkeleton() != null)
                            {
                                openList.add(mesh.getSkeleton());
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            //copy to avoid concurrency issues
            final Object3D[] rootsArray = this.roots;
            
            if (isAnimating())
            {
                if (rootsArray != null)
                {
                    for (Object3D root : rootsArray)
                    {
                        root.animate(animationWorldTime);
                        animationWorldTime += 8;
                    }
                }
            }
            
            Graphics3D g3d = Graphics3D.getInstance();

            try
            {
                if (traceStream != null)
                {
                    renderTarget.setTraceStream(traceStream);
                    // a single frame only
                    traceStream = null;
                }
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
                            addAllLights(world, g3d);
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
            renderTarget.setTraceStream(null);
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

    private static void populateTreeViewObject3D(DefaultMutableTreeNode treeNode, Object3D obj)
    {
        if (obj.getUserID() != 0)
        {
            treeNode.add(new DefaultMutableTreeNode("userID : " + obj.getUserID(), false));
        }
        
        for (int i = 0; i < obj.getAnimationTrackCount(); ++i)
        {
            populateTreeView(treeNode, obj.getAnimationTrack(i));
        }
    }
    
    private static void populateTreeViewAnimationTrack(DefaultMutableTreeNode treeNode, AnimationTrack obj)
    {
        populateTreeViewObject3D(treeNode, obj);
        
        populateTreeView(treeNode, obj.getController());
        populateTreeView(treeNode, obj.getKeyframeSequence());
    }

    private static void populateTreeViewCompositingMode(DefaultMutableTreeNode treeNode, CompositingMode obj)
    {
        if (obj.getBlending() != CompositingMode.REPLACE)
        {
            treeNode.add(new DefaultMutableTreeNode("blending : " + obj.getBlending(), false));
        }

        if (obj.getAlphaThreshold() > 0.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("alphaThreshold : " + obj.getAlphaThreshold(), false));
        }

        populateTreeViewObject3D(treeNode, obj);
        
        populateTreeView(treeNode, obj.getBlender());
        populateTreeView(treeNode, obj.getStencil());
    }
    
    private static void populateTreeViewPolygonMode(DefaultMutableTreeNode treeNode, PolygonMode obj)
    {
        if (obj.getShading() != PolygonMode.SHADE_SMOOTH)
        {
            treeNode.add(new DefaultMutableTreeNode("shading : " + obj.getShading(), false));
        }
        
        if (obj.getCulling() != PolygonMode.CULL_BACK)
        {
            treeNode.add(new DefaultMutableTreeNode("culling : " + obj.getCulling(), false));
        }
        
        populateTreeViewObject3D(treeNode, obj);
        
    }
    
    private static void populateTreeViewAppearanceBase(DefaultMutableTreeNode treeNode, AppearanceBase obj)
    {
        populateTreeViewObject3D(treeNode, obj);
        
        if (obj.getLayer() != 0)
        {
            treeNode.add(new DefaultMutableTreeNode("layer : " + obj.getLayer(), false));
        }
        
        populateTreeView(treeNode, obj.getCompositingMode());
        populateTreeView(treeNode, obj.getPolygonMode());
    }

    private static void populateTreeViewAppearance(DefaultMutableTreeNode treeNode, Appearance obj)
    {
        populateTreeViewAppearanceBase(treeNode, obj);
        
        populateTreeView(treeNode, obj.getFog());
        populateTreeView(treeNode, obj.getMaterial());
        populateTreeView(treeNode, obj.getPointSpriteMode());
        
        for (int i = 0; i < i; ++i)
        {
            if (obj.getTexture(i) != null)
            {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode("texture[" + i + "]");
                treeNode.add(n);
                populateTreeView(n, obj.getTexture(i));
            }
        }
    }
    
    private static void populateTreeViewTransformable(DefaultMutableTreeNode treeNode, Transformable obj)
    {
        float[] v = new float[4];
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

        obj.getOrientation(v);
        if (v[0] != 0.0f)
        {
            treeNode.add(new DefaultMutableTreeNode("orientation : [ " + v[0] + ", " + v[1] + ", " + v[2] + ", " + v[3] + " ]"));
        }
        
        populateTreeViewObject3D(treeNode, obj);
    }

    private static void populateTreeViewNode(DefaultMutableTreeNode treeNode, Node obj)
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
    
    private static void populateTreeViewMesh(DefaultMutableTreeNode treeNode, Mesh obj)
    {
        populateTreeViewNode(treeNode, obj);
        
        populateTreeView(treeNode, obj.getVertexBuffer());
        
        if (obj.getSubmeshCount() > 0)
        {
            for (int i = 0; i < obj.getSubmeshCount(); ++i)
            {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode("submeshes[" + i + "]");
                treeNode.add(n);

                populateTreeView(n, obj.getIndexBuffer(i));
                populateTreeView(n, obj.getAppearance(i));
            }
        }
    }
    
    private static void populateTreeViewSkinnedMesh(DefaultMutableTreeNode treeNode, SkinnedMesh obj)
    {
        populateTreeViewMesh(treeNode, obj);
        
        populateTreeView(treeNode, obj.getSkeleton());
    }
    
    private static String lightModeString(int mode)
    {
        switch (mode)
        {
            case Light.DIRECTIONAL:
                return "Directional";
            case Light.OMNI:
                return "Omni";
            default:
                return Integer.toString(mode);
        }
    }
    
    private static void populateTreeViewLight(DefaultMutableTreeNode treeNode, Light obj)
    {
        if (obj.getColor() != 0xffffff)
        {
            treeNode.add(new DefaultMutableTreeNode("color : " + obj.getColor()));
        }
        
        if (obj.getMode() != Light.DIRECTIONAL)
        {
            treeNode.add(new DefaultMutableTreeNode("mode : " + lightModeString(obj.getMode())));
        }

        populateTreeViewNode(treeNode, obj);
    }
    
    private static void populateTreeViewGroup(DefaultMutableTreeNode treeNode, Group g)
    {
        populateTreeViewNode(treeNode, g);
        
        if (g.getChildCount() > 0)
        {
            for (int i = 0; i < g.getChildCount(); ++i)
            {
                Node child = g.getChild(i);
                populateTreeView(treeNode, child);
            }
        }
    }

    private static void populateTreeViewWorld(DefaultMutableTreeNode treeNode, World obj)
    {
        populateTreeView(treeNode, obj.getBackground());

        populateTreeViewGroup(treeNode, obj);
    }
    
    private static void populateTreeView(DefaultMutableTreeNode parent, Object3D obj)
    {
        if (obj == null)
        {
            return;
        }
        
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(obj);
        parent.add(treeNode);
        
        if (obj instanceof World)
        {
            populateTreeViewWorld(treeNode, (World) obj);
        }
        else if (obj instanceof Group)
        {
            populateTreeViewGroup(treeNode, (Group) obj);
        }
        else if (obj instanceof SkinnedMesh)
        {
            populateTreeViewSkinnedMesh(treeNode, (SkinnedMesh) obj);
        }
        else if (obj instanceof Mesh)
        {
            populateTreeViewMesh(treeNode, (Mesh) obj);
        }
        else if (obj instanceof Light)
        {
            populateTreeViewLight(treeNode, (Light) obj);
        }
        else if (obj instanceof Node)
        {
            populateTreeViewNode(treeNode, (Node) obj);
        }
        else if (obj instanceof AnimationTrack)
        {
            populateTreeViewAnimationTrack(treeNode, (AnimationTrack) obj);
        }
        else if (obj instanceof Appearance)
        {
            populateTreeViewAppearance(treeNode, (Appearance) obj);
        }
        else if (obj instanceof CompositingMode)
        {
            populateTreeViewCompositingMode(treeNode, (CompositingMode) obj);
        }
        else if (obj instanceof PolygonMode)
        {
            populateTreeViewPolygonMode(treeNode, (PolygonMode) obj);
        }
        else
        {
            populateTreeViewObject3D(treeNode, obj);
        }
    }
    
    private void populateTreeView()
    {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("scene");
        
        if (canvas.getRoots() != null)
        {
            for (Object3D root : canvas.getRoots())
            {
                populateTreeView(top, root);
            }
        }
        
        root = top;
        
        treeView.setModel(new DefaultTreeModel(root));
    }

    private static class TreeCellRenderer extends DefaultTreeCellRenderer
    {
        static final long serialVersionUID = 1L;
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode v = (DefaultMutableTreeNode) value;
            if (!(v.getUserObject() instanceof Object3D))
            {
                setIcon(null);
            }
            return this;
        }
        
    }
    
    private void toggleTreeView()
    {
        if (treeView == null)
        {
            getContentPane().removeAll();
            
            treeView = new JTree();
            treeView.setCellRenderer(new TreeCellRenderer());
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
    
    private void toggleGLTrace()
    {
        canvas.setTraceStream(System.out);
    }
    
    private void toggleAnimation()
    {
        canvas.setAnimating(!canvas.isAnimating());
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

    private void initDebugMenu(Menu menu)
    {
        MenuItem toggleTraceItem = new MenuItem("GL trace single frame",
                new MenuShortcut(KeyEvent.VK_G));
        toggleTraceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                toggleGLTrace();
            }
        });
        
        menu.add(toggleTraceItem);
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

        Menu debugMenu = new Menu("Debug");
        menuBar.add(debugMenu);
        initDebugMenu(debugMenu);
        
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
                
                canvas.addKeyListener(new KeyAdapter()
                {
                    @Override
                    public void keyPressed(KeyEvent e)
                    {
                        super.keyPressed(e);
                        
                        if (' ' == e.getKeyChar())
                        {
                            toggleAnimation();
                        }
                    }
                });
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
