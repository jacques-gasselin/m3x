package m3x;

import javax.microedition.m3g.Transform;

/**
 * @author jgasseli
 */
public abstract class AbstractTestCase extends junit.framework.TestCase
{
    public static final void assertEquals(float[] expected, float[] actual, float delta)
    {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.length, actual.length);

        final int length = expected.length;
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    public static final void assertEquals(float[] expected, Transform actual)
    {
        assertNotNull(actual);

        float[] mat = new float[16];
        actual.get(mat);
        assertEquals(expected, mat, 0.001f);
    }

    public static final void assertIsIdentity(Transform actual)
    {
        assertNotNull(actual);

        final float[] expected = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
        };
        assertEquals(expected, actual);
    }
    
    public static final void assertIsInverse(Transform original, Transform inverse)
    {
        assertNotNull(original);
        assertNotNull(inverse);
        
        final Transform transform = new Transform(original);
        transform.postMultiply(inverse);
        assertIsIdentity(transform);
    }
}
