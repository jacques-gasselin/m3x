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

package m3x;

import java.util.Arrays;

/**
 * @author jgasseli
 */
public final class Require
{
    /**
     * Static utility class, should never be instantiated
     */
    private Require()
    {
        
    }

    public static final void notNull(Object value, String name)
    {
        if (value == null)
        {
            throw new NullPointerException(name + " is null");
        }
    }

    public static final void argumentNotEmpty(Object[] array, String name)
    {
        notNull(array, name);
        if (array.length == 0)
        {
            throw new NullPointerException(name + " is empty");
        }
    }

    public static final void argumentNotEmpty(int[] array, String name)
    {
        notNull(array, name);
        
        if (array.length == 0)
        {
            throw new IllegalArgumentException(name + " is empty");
        }
    }

    /**
     * Checks the precondition that the given array is not null and has a length
     * of at least capacity elements.
     * 
     * @param array the array to check
     * @param name the name of the array to display on error
     * @param capacity the capacity the array must have
     * @throws NullPointerException if {@code array is null}
     * @throws IllegalArgumentException if {@code array.length < capacity}
     */
    public static final void argumentHasCapacity(float[] array, String name, int capacity)
    {
        notNull(array, name);
        if (array.length < capacity)
        {
            throw new IllegalArgumentException(name + ".length < " + capacity);
        }
    }

    public static final void argumentNotNegative(float value, String name)
    {
        if (Float.isNaN(value))
        {
            throw new IllegalArgumentException(name + " is NaN");
        }

        if (value < 0)
        {
            throw new IllegalArgumentException(name + " is negative");
        }
    }

    public static final void argumentNotNegative(int value, String name)
    {
        if (value < 0)
        {
            throw new IllegalArgumentException(name + " is negative");
        }
    }

    public static final void indexNotNegative(int value, String name)
    {
        if (value < 0)
        {
            throw new IndexOutOfBoundsException(name + " is negative");
        }
    }

    public static final void indexInRange(int index, int limit)
    {
        indexInRange(index, "index", limit);
    }

    public static final void indexInRange(int index, String name, int limit)
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
        
    public static final void argumentInRange(int value, String name, int minValue, int maxValue)
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

    public static final void argumentInRange(float value, String name, float minValue, float maxValue)
    {
        if (Float.isNaN(value))
        {
            throw new IllegalArgumentException(name + " is NaN");
        }

        if (value < minValue)
        {
            throw new IllegalArgumentException(name + " < " + minValue);
        }
        if (value > maxValue)
        {
            throw new IllegalArgumentException(name + " > " + maxValue);
        }
    }

    public static final void argumentIn(int value, String name, int[] list)
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

    public static final void argumentInEnum(int value, String name, int enumMin, int enumMax)
    {
        argumentInRange(value, name, enumMin, enumMax);
    }

    public static final void argumentGreaterThan(int value, String name, int limit)
    {
        if (value <= limit)
        {
            throw new IllegalArgumentException(name + " <= " + limit);
        }
    }

    public static final void argumentGreaterThan(float value, String name, float limit)
    {
        if (Float.isNaN(value))
        {
            throw new IllegalArgumentException(name + " is NaN");
        }

        if (Float.isNaN(limit))
        {
            throw new IllegalArgumentException("limit is NaN");
        }

        if (value <= limit)
        {
            throw new IllegalArgumentException(name + " <= " + limit);
        }
    }

    public static final void argumentGreaterThanZero(int value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " <= 0");
        }
    }

    public static final void argumentGreaterThanZero(float value, String name)
    {
        if (Float.isNaN(value))
        {
            throw new IllegalArgumentException(name + " is NaN");
        }
        
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " <= 0");
        }
    }

}
