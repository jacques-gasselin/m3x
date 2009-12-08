package javax.microedition.m3g;

/**
 * @author jgasseli
 */
final class Require
{
    /**
     * Static utility class, should never be instantiated
     */
    private Require()
    {
        
    }

    static final void notNull(Object value, String name)
    {
        if (value == null)
        {
            throw new NullPointerException(name + " is null");
        }
    }

    static final void argumentNotEmpty(int[] array, String name)
    {
        notNull(array, name);
        
        if (array.length == 0)
        {
            throw new IllegalArgumentException(name + " is empty");
        }
    }

    static final void argumentHasCapacity(float[] array, String name, int capacity)
    {
        notNull(array, name);
        if (array.length < capacity)
        {
            throw new IllegalArgumentException(name + ".length < " + capacity);
        }
    }

    static final void argumentNotNegative(int value, String name)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException(name + " is negative");
        }
    }

    static final void indexNotNegative(int value, String name)
    {
        if (value < 0)
        {
            throw new IndexOutOfBoundsException(name + " is negative");
        }
    }

    static final void indexInRange(int index, int limit)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException("index is negative");
        }
        if (index >= limit)
        {
            throw new IndexOutOfBoundsException("index >= " + limit);
        }
    }

    static final void argumentInEnum(int value, String name, int enumMin, int enumMax)
    {
        if (value < enumMin)
        {
            throw new IllegalArgumentException(name + " < " + enumMin);
        }
        if (value > enumMax)
        {
            throw new IllegalArgumentException(name + " > " + enumMax);
        }
    }

    static final void argumentGreaterThan(float value, String name, float limit)
    {
        if (value <= limit)
        {
            throw new IllegalArgumentException(name + " <= " + limit);
        }
    }

    static final void argumentGreaterThanZero(float value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " <= 0");
        }
    }

}
