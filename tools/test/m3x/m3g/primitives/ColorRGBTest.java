package m3x.m3g.primitives;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.AbstractTestCase;
import m3x.m3g.primitives.Section;

public class ColorRGBTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex index = new ObjectIndex(666);
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(index);
      ObjectIndex deserialized = (ObjectIndex)M3GSupport.bytesToObject(serialized,  ObjectIndex.class);
      this.doTestAccessors(index, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
