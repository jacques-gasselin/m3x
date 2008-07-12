package m3x.m3g.objects;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import junit.framework.TestCase;

public class AbstractTestCase extends TestCase
{
  /**
   * Compares all accessors (getters) of two objects for
   * having the same value. Reflection is used to achieve this.
   * 
   * @param object1
   * @param object2
   * @throws Exception
   */
  protected void doTestAccessors(M3GSerializable object1, M3GSerializable object2) throws Exception
  {
    assertTrue(object1.getClass().equals(object2.getClass()));
    Class<? extends M3GSerializable> clazz = object1.getClass();
    Method[] methods = clazz.getDeclaredMethods();

    for (int i = 0; i < methods.length; i++)
    {
      Method getter = methods[i];
      String methodName = getter.getName();
      if (methodName.startsWith("get"))
      {
        Object result1 = getter.invoke(object1, (Object[])null);
        Object result2 = getter.invoke(object2, (Object[])null);        
        assertTrue(result1.equals(result2));
      }
    }
  }
  
  protected Matrix getMatrix()
  {
    float[] matrix = new float[] {1, 0, 0, 0, 
                                  0, 1, 0, 0, 
                                  0, 0, 1, 0,
                                  0, 0, 0, 1};
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
