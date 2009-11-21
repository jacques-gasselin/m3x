package javax.microedition.m3g;

import junit.framework.TestCase;

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
        assertNull(g3d.getCamera(null));
    }

    public void testGetCamera()
    {
        Transform t = new Transform();
        assertNull(g3d.getCamera(t));
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

    public void testSetCameraNonIvertibleZero()
    {
        Camera c = new Camera();
        Transform t = new Transform();
        float[] m = {
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0 };
        t.set(m);
        try
        {
            g3d.setCamera(c, t);
            fail("must throw AE");
        }
        catch (ArithmeticException e)
        {
            //correct
        }
    }

    public void testSetCamera()
    {
        Camera c = new Camera();
        Transform t = new Transform();
        g3d.setCamera(c, t);
        assertEquals(c, g3d.getCamera(t));
    }
}
