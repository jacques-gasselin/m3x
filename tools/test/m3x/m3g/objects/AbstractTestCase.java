package m3x.m3g.objects;

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
   * TODO: make this work (with annotations) if possible with reflection 
   * @param object1
   * @param object2
   * @throws Exception
   */
  protected void doTestAccessors(M3GSerializable object1, M3GSerializable object2) throws Exception
  {
    Class<? extends M3GSerializable> clazz1 = object1.getClass();
    Method[] methods1 = clazz1.getClass().getMethods();
    Class<? extends M3GSerializable> clazz2 = object2.getClass();
    Method[] methods2 = clazz2.getClass().getMethods();
    for (int i = 0; i < methods1.length; i++)
    {
      Method getter1 = methods1[i];
      Method getter2 = methods2[i];
      String methodName = getter1.getName();
      if (methodName.startsWith("get"))
      {
        assertTrue(getter1.invoke(object1, null).equals(getter2.invoke(object2, null)));
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
