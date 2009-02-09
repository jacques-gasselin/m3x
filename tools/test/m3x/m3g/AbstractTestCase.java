/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
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

package m3x.m3g;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import junit.framework.AssertionFailedError;
import m3x.m3g.primitives.Serialisable;

import junit.framework.TestCase;

/**
 *
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class AbstractTestCase extends TestCase
{

    protected void assertSaveAndLoad(Object3D[] roots)
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Saver.save(out, roots, "1.0", null);

            byte[] data = out.toByteArray();

            Object3D[] loadRoots = Loader.load(data);

            for (int i = 0; i < roots.length; ++i)
            {
                roots[i].equals(loadRoots[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    protected void assertSerialiseSingle(Serialisable from, Serialisable to)
    {
        try
        {
            //Serialise
            Serialiser serialiser = new Serialiser("1.0", null);
            byte[] serialised = serialiser.serialiseSingle(from);

            //Deserialise
            Deserialiser deserialiser = new Deserialiser();
            deserialiser.deserialiseSingle(serialised, to);

            assertTrue(from.equals(to));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    protected void assertSerialiseSingle(Serialisable from)
    {
        Class cls = from.getClass();
        Serialisable to = null;
        try
        {
            to = (Serialisable)cls.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
        assertSerialiseSingle(from, to);
    }
    
    /**
     * Compares all accessors (getters) of two objects for
     * having the same values. Reflection is used to achieve this.
     *
     * @param object1
     * @param object2
     * @throws Exception
     */
    protected void doTestAccessors(Object object1, Object object2) throws AssertionFailedError
    {
        Class cls = object1.getClass();
        assertTrue(cls.equals(object2.getClass()));

        BeanInfo beanInfo;
        try
        {
            beanInfo = Introspector.getBeanInfo(cls);
        }
        catch (IntrospectionException e)
        {
            return;
        }
        
        for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors())
        {
            Method getter = prop.getReadMethod();
            if (getter != null)
            {

                Object result1, result2;
                try
                {
                    result1 = getter.invoke(object1, (Object[]) null);
                    result2 = getter.invoke(object2, (Object[]) null);
                }
                catch (InvocationTargetException e)
                {
                    continue;
                }
                catch (IllegalAccessException e)
                {
                    continue;
                }
                
                try
                {
                    handleResults(result1, result2);
                }
                catch (AssertionFailedError e)
                {
                    System.out.println("Failed on method : " + getter);
                    System.out.println("result1 : " + result1);
                    System.out.println("result2 : " + result2);
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    private void handleResults(Object result1, Object result2)
    {
        if ((result1 == null) && (result2 == null))
        {
            // ok if both are null
            return;
        }
        else
        {
            if (result1 == null)
            {
                fail("result1 is null");
            }
            if (result2 == null)
            {
                fail("result2 is null");
            }
            compareClasses(result1, result2);
        }
    }

    private void compareClasses(Object result1, Object result2)
    {
        Class cls1 = result1.getClass();
        Class cls2 = result2.getClass();
        if (cls1.isArray() && cls2.isArray())
        {
            // we have two arrays, now we need to find out
            // if they are primitive types or Object[]
            comparePrimitiveArrays(result1, result2, cls1, cls2);
        }
        else
        {
            try
            {
                if (result1 instanceof Object3D)
                {
                    doTestAccessors(result1, result2);
                }
                else
                {
                    assertTrue(result1.equals(result2));
                }
            }
            catch (AssertionFailedError e)
            {
                System.out.println(result1 + " is not equal to " + result2);
                throw e;
            }
        }
    }

    private void comparePrimitiveArrays(Object result1, Object result2,
            Class cls1, Class cls2)
    {
        if (cls1.equals(boolean[].class) && cls2.equals(boolean[].class))
        {
            assertTrue(Arrays.equals((boolean[]) result1, (boolean[]) result2));
        }
        else if (cls1.equals(byte[].class) && cls2.equals(byte[].class))
        {
            assertTrue(Arrays.equals((byte[]) result1, (byte[]) result2));
        }
        else if (cls1.equals(short[].class) && cls2.equals(short[].class))
        {
            assertTrue(Arrays.equals((short[]) result1, (short[]) result2));
        }
        else if (cls1.equals(char[].class) && cls2.equals(char[].class))
        {
            assertTrue(Arrays.equals((char[]) result1, (char[]) result2));
        }
        else if (cls1.equals(short[].class) && cls2.equals(short[].class))
        {
            assertTrue(Arrays.equals((short[]) result1, (short[]) result2));
        }
        else if (cls1.equals(int[].class) && cls2.equals(int[].class))
        {
            assertTrue(Arrays.equals((int[]) result1, (int[]) result2));
        }
        else if (cls1.equals(long[].class) && cls2.equals(long[].class))
        {
            assertTrue(Arrays.equals((long[]) result1, (long[]) result2));
        }
        else if (cls1.equals(float[].class) && cls2.equals(float[].class))
        {
            assertTrue(Arrays.equals((float[]) result1, (float[]) result2));
        }
        else if (cls1.equals(double[].class) && cls2.equals(double[].class))
        {
            assertTrue(Arrays.equals((double[]) result1, (double[]) result2));
        }
        else
        {
            //are they in return arrays?
            assertTrue(Arrays.deepEquals((Object[])result1, (Object[])result2));
        }
    }
}
