package javax.microedition.m3g;

import junit.framework.TestCase;
import quicktime.qd3d.camera.CameraData;

/**
 * @author jgasseli
 */
public class Graphics3DTest extends TestCase
{
    private Graphics3D g3d;

    @Override
    public void setUp()
    {
        g3d = Graphics3D.getInstance();
    }
    
    public void testGetInstance()
    {
        assertNotNull(g3d);
    }

    public void testGetCameraNull()
    {
        g3d.getCamera(null);
    }

    public void testGetCamera()
    {
        Transform t = new Transform();
        g3d.getCamera(t);
    }

    public void testSetCameraNullNull()
    {
        g3d.setCamera(null, null);
    }

    public void testSetCamera1stNull()
    {
        Transform t = new Transform();
        g3d.setCamera(null, t);
    }

    public void testSetCamera2ndNull()
    {
        Camera c = new Camera();
        g3d.setCamera(c, null);
    }
}
