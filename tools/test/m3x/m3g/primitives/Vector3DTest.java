package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;

public class Vector3DTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    Vector3D vector = new Vector3D(0.1f, 0.2f, 0.3f);
                                                                    
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(vector);
      Vector3D deserialized = (Vector3D)M3GSupport.bytesToObject(serialized, Vector3D.class);
      this.doTestAccessors(vector, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
