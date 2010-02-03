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

package picking;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.RayIntersection;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.microedition.m3g.GeomUtils;
import m3x.awt.BaseFrame;
import m3x.microedition.m3g.TransformUtils;
import m3x.microedition.m3g.awt.BlenderTurntableCameraController;

/**
 * @author ferrumski
 */
public class PickingDemo extends BaseFrame
{
    class PickingDemoCanvas extends GLCanvas
            implements Runnable, MouseListener, MouseMotionListener
    {
        Camera camera;
        Mesh lookatMarker;
        Mesh sphere1;
        Mesh sphere2;
        Mesh sphere3;
        Mesh cube1;
        Mesh cube2;
        Mesh cube3;
        Mesh viewFrustumMesh;
        Mesh pickRayMesh;

        Group topGroup;
        
        BlenderTurntableCameraController cameraController;

        int xMouse, yMouse;
        
        private AbstractRenderTarget renderTarget;

        private PickingDemoCanvas()
        {
            addMouseListener(this);
            addMouseMotionListener(this);

            //a top group to hold all meshes
            topGroup = new Group();
            
            //create some geometry and add it to the top group
            sphere1 = GeomUtils.createSphere(0.2f, 12, 12);
            sphere1.translate(-1.0f, 0.0f, 0.0f);

            sphere2 = GeomUtils.createSphere(0.35f, 12, 12);
            sphere2.translate(0.0f, -1.0f, 0.0f);

            sphere3 = GeomUtils.createSphere(0.4f, 12, 12);
            sphere3.translate(1.0f, 0.0f, 0.0f);

            cube1 = GeomUtils.createCubeMesh(0.1f, 0.5f, 0.0f, 0.0f, true);
            cube1.translate(0.0f, 1.0f, 1.0f);

            cube2 = GeomUtils.createCubeMesh(0.2f, 0.5f, 0.5f, 0.0f, true);
            cube2.translate(0.0f, 1.0f, -1.0f);
            
            cube3 = GeomUtils.createCubeMesh(0.3f, 0.5f, 0.0f, 5.0f, true);
            cube3.translate(1.0f, 1.0f, -1.0f);

            topGroup.addChild(sphere1);
            topGroup.addChild(sphere2);
            topGroup.addChild(sphere3);

            topGroup.addChild(cube1);
            topGroup.addChild(cube2);
            topGroup.addChild(cube3);

            //create a coordinate frame mesh showing the coordinate frame of the
            //top group
            topGroup.addChild(GeomUtils.createCoordinateFrameMesh(1.0f));

            //create a camera and add it to the top group
            camera = new Camera();
            topGroup.addChild(camera);

            //create a mesh marking the camera pivot point, aka the lookat.
            lookatMarker = GeomUtils.createCubeMesh(0.01f, 1.0f, 1.0f, 0.0f, true);
            topGroup.addChild(lookatMarker);

            cameraController = 
                    new BlenderTurntableCameraController(camera, this, 0.0f, 10.0f, 4.0f);
            renderTarget = new GLRenderTarget(this);
            new Thread(this).start();
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            //System.out.println("paint");

            Graphics3D g3d = Graphics3D.getInstance();

            try
            {
                g3d.bindTarget(renderTarget);
                camera.setPerspective(50,
                        getWidth() / (float)getHeight(),
                        0.1f, 10.0f);
                //camera.setParallel(4,
                //        getWidth() / (float)getHeight(),
                //        0.1f, 10.0f);
                cube1.postRotate(1.0f, 1.0f, 0.0f, 0.0f);
                cube2.postRotate(1.0f, 0.0f, 1.0f, 0.0f);
                cube3.postRotate(1.0f, 0.0f, 0.0f, 1.0f);

                g3d.setViewport(0, 0, getWidth(), getHeight());
                cameraController.update(0);
                float[] lookatPos = new float[3];
                cameraController.getLookAtPosition(lookatPos);
                lookatMarker.setTranslation(lookatPos[0], lookatPos[1], lookatPos[2]);
                Transform cameraTransform = cameraController.getTransform();
                g3d.setCamera(camera, cameraTransform);

                g3d.clear(null);


                g3d.render(topGroup, null);

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

        @Override
        public void mouseExited(MouseEvent e)
        {

        }

        @Override
        public void mouseEntered(MouseEvent e)
        {

        }

        @Override
        public void mouseReleased(MouseEvent e)
        {

        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            xMouse = e.getX();
            yMouse = e.getY();
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            RayIntersection ri = new RayIntersection();
            float x = e.getX() / (float)getWidth();
            float y = e.getY() / (float)getHeight();
            topGroup.pick(0, x, y, camera, ri);
            //System.out.println("intersected node: " + ri.getIntersected());

            //create a pick ray visualization mesh
            if (pickRayMesh != null)
            {
                topGroup.removeChild(pickRayMesh);
            }
            pickRayMesh = GeomUtils.createPickRayMesh(camera, x, y);
            pickRayMesh.setTransform(cameraController.getTransform());
            topGroup.addChild(pickRayMesh);

            //create a snapshot of the camera frustum
            //and attach it to the scene graph
            if (viewFrustumMesh != null)
            {
                topGroup.removeChild(viewFrustumMesh);
            }
            viewFrustumMesh = GeomUtils.createViewFrustumMesh(camera);
            viewFrustumMesh.setTransform(cameraController.getTransform());

            topGroup.addChild(viewFrustumMesh);
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {

        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            //if dragging with the left mouse button down...
            if (e.getButton() == MouseEvent.BUTTON1)
            {
                int dx = e.getX() - xMouse;
                xMouse = e.getX();
                int dy = e.getY() - yMouse;
                yMouse = e.getY();
                
                //test dragging of one of the spheres
                float[] t = new float[4];
                cube2.getTranslation(t);
                t[3] = 1.0f;

                /*
                Transform tr = new Transform(cameraController.getTransform());
                tr.invert();
                float[] l = new float[3];
                cameraController.getLookAtPosition(l);
                //tr.postTranslate(-l[0], -l[1], -l[2]);
                tr.transform(t);
                 *
                 */

                //sort out the transformations to pass
                //throw new UnsupportedOperationException();

                final Transform localToCamera = new Transform(cameraController.getTransform());
                localToCamera.invert();
                TransformUtils.screenToLocalOffset(camera,
                        localToCamera, t,
                        dx, dy, getWidth(), getHeight());
                cube2.setTranslation(t[0], t[1], t[2]);
                
            }
        }
    }

    PickingDemo()
    {
        super("PickingDemo");
        add(new PickingDemoCanvas());
    }

    public static void main(String[] args)
    {

        BaseFrame frame = new PickingDemo();
        frame.present(false);
    }
}
