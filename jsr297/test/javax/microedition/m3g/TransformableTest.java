package javax.microedition.m3g;

import m3x.AbstractTestCase;

/**
 * @author jgasseli
 */
public class TransformableTest extends AbstractTestCase
{
    private Group g;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        g = new Group();
    }

    public void testGetCompositeTransform()
    {
        final Transform t = new Transform();
        g.getCompositeTransform(t);
    }

    public void testGetCompositeTransformNull()
    {
        try
        {
            g.getCompositeTransform(null);
            fail("null transform must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    private final void getOrientation(float[] v)
    {
        try
        {
            g.getOrientation(v);
            assertNotNull("null angleAxis must throw NPE", v);
            assertTrue("length < 4 must throw IAE",
                    v.length >= 4);
        }
        catch (NullPointerException e)
        {
            assertNull(v);
            assertNotNull(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(v);
            assertFalse("length >= 4 must not throw IAE",
                    v.length >= 4);
            assertNotNull(e.getMessage());
        }
    }

    private final void getOrientationQuat(float[] v)
    {
        try
        {
            g.getOrientationQuat(v);
            assertNotNull("null quat must throw NPE", v);
            assertTrue("length < 4 must throw IAE",
                    v.length >= 4);
        }
        catch (NullPointerException e)
        {
            assertNull(v);
            assertNotNull(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(v);
            assertFalse("length >= 4 must not throw IAE",
                    v.length >= 4);
            assertNotNull(e.getMessage());
        }
    }
    
    public void testGetOrientation()
    {
        getOrientation(new float[4]);
    }

    public void testGetOrientationNull()
    {
        getOrientation(null);
    }

    public void testGetOrientationZeroElements()
    {
        getOrientation(new float[0]);
    }

    public void testGetOrientationOneElement()
    {
        getOrientation(new float[1]);
    }

    public void testGetOrientationTwoElements()
    {
        getOrientation(new float[2]);
    }

    public void testGetOrientationThreeElements()
    {
        getOrientation(new float[3]);
    }

    public void testGetOrientationFiveElements()
    {
        getOrientation(new float[5]);
    }

    public void testGetOrientationQuat()
    {
        final float[] quat = new float[4];
        g.getOrientationQuat(quat);
    }

    public void testGetOrientationQuatNull()
    {
        getOrientationQuat(null);
    }

    public void testGetOrientationQuatZeroElements()
    {
        getOrientationQuat(new float[0]);
    }

    public void testGetOrientationQuatOneElement()
    {
        getOrientationQuat(new float[1]);
    }

    public void testGetOrientationQuatTwoElements()
    {
        getOrientationQuat(new float[2]);
    }

    public void testGetOrientationQuatThreeElements()
    {
        getOrientationQuat(new float[3]);
    }

    public void testGetOrientationQuatFiveElements()
    {
        getOrientationQuat(new float[5]);
    }
}
