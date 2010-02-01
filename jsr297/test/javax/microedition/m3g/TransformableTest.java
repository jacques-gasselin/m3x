package javax.microedition.m3g;

import junit.framework.TestCase;

/**
 * @author jgasseli
 */
public class TransformableTest extends TestCase
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

    public void testGetOrientation()
    {
        final float[] angleAxis = new float[4];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationNull()
    {
        try
        {
            g.getOrientation(null);
            fail("null angleAxis must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationZeroElements()
    {
        try
        {
            final float[] angleAxis = new float[0];
            g.getOrientation(angleAxis);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationOneElement()
    {
        try
        {
            final float[] angleAxis = new float[1];
            g.getOrientation(angleAxis);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

    }

    public void testGetOrientationTwoElements()
    {
        try
        {
            final float[] angleAxis = new float[2];
            g.getOrientation(angleAxis);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationThreeElements()
    {
        try
        {
            final float[] angleAxis = new float[3];
            g.getOrientation(angleAxis);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationFiveElements()
    {
        final float[] angleAxis = new float[5];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationQuat()
    {
        final float[] quat = new float[4];
        g.getOrientationQuat(quat);
    }

    public void testGetOrientationQuatNull()
    {
        try
        {
            g.getOrientationQuat(null);
            fail("null angleAxis must throw NPE");
        }
        catch (NullPointerException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationQuatZeroElements()
    {
        try
        {
            final float[] quat = new float[0];
            g.getOrientationQuat(quat);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

    }

    public void testGetOrientationQuatOneElement()
    {
        try
        {
            final float[] quat = new float[1];
            g.getOrientationQuat(quat);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationQuatTwoElements()
    {
        try
        {
            final float[] quat = new float[2];
            g.getOrientationQuat(quat);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationQuatThreeElements()
    {
        try
        {
            final float[] quat = new float[3];
            g.getOrientationQuat(quat);
            fail("length < 4 must throw IAE");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }

    public void testGetOrientationQuatFiveElements()
    {
        final float[] quat = new float[5];
        g.getOrientationQuat(quat);
    }
}
