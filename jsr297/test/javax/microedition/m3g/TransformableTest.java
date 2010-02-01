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
        final float[] angleAxis = new float[0];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationOneElement()
    {
        final float[] angleAxis = new float[1];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationTwoElements()
    {
        final float[] angleAxis = new float[2];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationThreeElements()
    {
        final float[] angleAxis = new float[3];
        g.getOrientation(angleAxis);
    }

    public void testGetOrientationFiveElements()
    {
        final float[] angleAxis = new float[5];
        g.getOrientation(angleAxis);
    }
}
