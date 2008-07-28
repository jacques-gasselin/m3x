package m3x.m3g;

import java.lang.reflect.Method;
import java.util.Arrays;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
  /**
   * Compares all accessors (getters) of two objects for
   * having the same values. Reflection is used to achieve this.
   * 
   * @param object1
   * @param object2
   * @throws Exception
   */
  protected void doTestAccessors(M3GSerializable object1, M3GSerializable object2) throws Exception
  {
    assertTrue(object1.getClass().equals(object2.getClass()));
    Class<? extends M3GSerializable> clazz = object1.getClass();
    Method[] methods = clazz.getMethods();

    for (int i = 0; i < methods.length; i++)
    {
      Method getter = methods[i];
      String methodName = getter.getName();
      if (methodName.startsWith("get") || methodName.startsWith("is"))
      {
        System.out.println(methodName);
        Object result1 = getter.invoke(object1, (Object[])null);
        Object result2 = getter.invoke(object2, (Object[])null);   
        if (result1 == null && result2 == null)
        {
          // ok if both are null
          assertTrue(true);
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
          Class<?> clazz1 = result1.getClass();
          Class<?> clazz2 = result2.getClass();
          if (clazz1.isArray() && clazz2.isArray())
          {
            // we have two arrays, now we need to find out
            // if they are primitive types or Object[]
            if (clazz1.equals(boolean[].class) && clazz2.equals(boolean[].class))
            {
              assertTrue(Arrays.equals((boolean[])result1, (boolean[])result2));
            }
            else
            if (clazz1.equals(byte[].class) && clazz2.equals(byte[].class))
            {
              assertTrue(Arrays.equals((byte[])result1, (byte[])result2));
            }
            else
            if (clazz1.equals(short[].class) && clazz2.equals(short[].class))
            {
              assertTrue(Arrays.equals((short[])result1, (short[])result2));
            }
            else
            if (clazz1.equals(char[].class) && clazz2.equals(char[].class))
            {
              assertTrue(Arrays.equals((char[])result1, (char[])result2));
            }
            else
            if (clazz1.equals(short[].class) && clazz2.equals(short[].class))
            {
              assertTrue(Arrays.equals((short[])result1, (short[])result2));
            }
            else
            if (clazz1.equals(int[].class) && clazz2.equals(int[].class))
            {
              assertTrue(Arrays.equals((int[])result1, (int[])result2));
            }
            else
            if (clazz1.equals(long[].class) && clazz2.equals(long[].class))
            {
              assertTrue(Arrays.equals((long[])result1, (long[])result2));
            }
            else
            if (clazz1.equals(float[].class) && clazz2.equals(float[].class))
            {
              assertTrue(Arrays.equals((float[])result1, (float[])result2));
            }
            else
            if (clazz1.equals(double[].class) && clazz2.equals(double[].class))
            {
              assertTrue(Arrays.equals((double[])result1, (double[])result2));
            }
            else
            {
              assertTrue(Arrays.equals((Object[])result1, (Object[])result2));              
            }
          }
          else
          {
            assertTrue(result1.equals(result2));
          }
        }
      }
    }
  }
  
  protected Matrix getMatrix()
  {
    float[] matrix = new float[16];
    for (int i = 0; i < matrix.length; i++)
    {
      float element = (float)Math.random() * 2.0f - 1.0f;
      matrix[i] = element;
    }
    Matrix transform = new Matrix(matrix);
    return transform;
  }

  protected UserParameter[] getUserParameters()
  {
    UserParameter[] userParameters = new UserParameter[1];
    userParameters[0] = new UserParameter();
    userParameters[0].parameterID =  1;
    userParameters[0].parameterValue = new byte[] {1, 2, 3, 4, 5, 6};
    return userParameters;
  }

  protected ObjectIndex[] getAnimationTracks()
  {
    ObjectIndex[] animationTracks = new ObjectIndex[1];
    animationTracks[0] = new ObjectIndex(1);
    return animationTracks;
  }  
}
