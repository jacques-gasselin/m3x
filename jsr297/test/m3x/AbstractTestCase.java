package m3x;

import javax.microedition.m3g.Transform;
import junit.framework.AssertionFailedError;

/**
 * @author jgasseli
 */
public abstract class AbstractTestCase extends junit.framework.TestCase
{
    private static final String toString(final float[] matrix)
    {
        StringBuilder b = new StringBuilder();
        for (int j = 0; j < 4; ++j)
        {
            if (j == 0)
            {
                b.append("[");
            }
            else
            {
                b.append(" ");
            }
            
            for (int i = 0; i < 4; ++i)
            {
                b.append(" ").append(matrix[j * 4 + i]).append(",");
            }

            if (j == 3)
            {
                b.append(" ]\n");
            }
            else
            {
                b.append("\n");
            }
        }
        return b.toString();
    }

    private static final String toString(final Transform transform)
    {
        final float[] mat = new float[16];
        transform.get(mat);
        return toString(mat);
    }
    
    public static final void assertEquals(final float[] expected, final float[] actual, float delta)
    {
        assertNotSame(expected, actual);
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.length, actual.length);

        final int length = expected.length;
        for (int i = 0; i < length; ++i)
        {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    public static final void assertEquals(final Transform expected, final Transform actual)
    {
        assertNotSame(expected, actual);
        assertNotNull(expected);
        assertNotNull(actual);

        final float[] expectedMat = new float[16];
        expected.get(expectedMat);
        
        final float[] actualMat = new float[16];
        actual.get(actualMat);

        try
        {
            assertEquals(expectedMat, actualMat, 0.001f);
        }
        catch (AssertionFailedError e)
        {
            StringBuilder b = new StringBuilder();
            b.append("expected:\n").append(toString(expected));
            b.append("but was:\n").append(toString(actual));
            throw new AssertionFailedError(b.toString());
        }
    }
    
    public static final void assertEquals(final float[] expected, final Transform actual)
    {
        assertNotNull(actual);

        float[] mat = new float[16];
        actual.get(mat);
        assertEquals(expected, mat, 0.001f);
    }

    public static final void assertIdentity(final Transform actual)
    {
        assertNotNull(actual);

        final Transform identity = new Transform();
        assertEquals(identity, actual);
    }
    
    public static final void assertInverse(final Transform original,
            final Transform inverse)
    {
        assertNotSame(original, inverse);
        assertNotNull(original);
        assertNotNull(inverse);
        
        final Transform transform = new Transform(original);
        transform.postMultiply(inverse);

        try
        {
            assertIdentity(transform);
        }
        catch (AssertionFailedError e)
        {
            StringBuilder b = new StringBuilder();
            b.append("original:\n").append(toString(original));
            b.append("multiplied by :\n").append(toString(inverse));
            b.append("is not identity, but:\n").append(toString(transform));
            throw new AssertionFailedError(b.toString());
        }
    }
}
