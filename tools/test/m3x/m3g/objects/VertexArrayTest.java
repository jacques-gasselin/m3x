package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class VertexArrayTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    byte[] components1 = new byte[] {1, 2, 3, 4, 5, 6};
    VertexArray array = new VertexArray(animationTracks,
                                        userParameters,
                                        components1,
                                        false);                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(array);
      VertexArray deserialized = (VertexArray)M3GSupport.bytesToObject(serialized, VertexArray.class);
      this.doTestAccessors(array, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
    short[] components2 = new short[] {1, 2, 3, 4, 5, 6};
    array = new VertexArray(animationTracks,
                            userParameters,
                            components2,
                            true);                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(array);
      VertexArray deserialized = (VertexArray)M3GSupport.bytesToObject(serialized, VertexArray.class);
      this.doTestAccessors(array, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    } 
  }
}
