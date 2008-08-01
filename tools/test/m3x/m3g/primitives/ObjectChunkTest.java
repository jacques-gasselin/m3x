package m3x.m3g.primitives;

import m3x.m3g.M3GSupport;
import m3x.m3g.ObjectTypes;
import m3x.m3g.AbstractTestCase;

public class ObjectChunkTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    final int N = 1024;
    byte[] object = new byte[N];
    for (int i = 0; i < N; i++)
    {
      object[i] = (byte)i;
    }
    ObjectChunk chunk = new ObjectChunk(ObjectTypes.ANIMATION_CONTROLLER, object);
                                                                    
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(chunk);
      ObjectChunk deserialized = (ObjectChunk)M3GSupport.bytesToObject(serialized, ObjectChunk.class);
      this.doTestAccessors(chunk, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
