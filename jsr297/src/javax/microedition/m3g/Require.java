package javax.microedition.m3g;

import java.util.Arrays;

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

    static final void argumentNotNegative(float value, String name)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException(name + " is negative");
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
        indexInRange(index, "index", limit);
    }

    static final void indexInRange(int index, String name, int limit)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException(name + " is negative");
        }
        if (index >= limit)
        {
            throw new IndexOutOfBoundsException(name + " >= " + limit);
        }
    }
        
    static final void argumentInRange(int value, String name, int minValue, int maxValue)
    {
        if (value < minValue)
        {
            throw new IllegalArgumentException(name + " < " + minValue);
        }
        if (value > maxValue)
        {
            throw new IllegalArgumentException(name + " > " + maxValue);
        }
    }

    static final void argumentInRange(float value, String name, float minValue, float maxValue)
    {
        if (value < minValue)
        {
            throw new IllegalArgumentException(name + " < " + minValue);
        }
        if (value > maxValue)
        {
            throw new IllegalArgumentException(name + " > " + maxValue);
        }
    }

    static final void argumentIn(int value, String name, int[] list)
    {
        for (int l : list)
        {
            if (value == l)
            {
                return;
            }
        }

        throw new IllegalArgumentException(name + " is not in " +
                Arrays.toString(list));
    }

    static final void argumentInEnum(int value, String name, int enumMin, int enumMax)
    {
        argumentInRange(value, name, enumMin, enumMax);
    }

    static final void argumentGreaterThan(int value, String name, int limit)
    {
        if (value <= limit)
        {
            throw new IllegalArgumentException(name + " <= " + limit);
        }
    }

    static final void argumentGreaterThan(float value, String name, float limit)
    {
        if (value <= limit)
        {
            throw new IllegalArgumentException(name + " <= " + limit);
        }
    }

    static final void argumentGreaterThanZero(int value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " <= 0");
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
