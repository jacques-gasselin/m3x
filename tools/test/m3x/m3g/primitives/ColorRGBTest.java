package m3x.m3g.primitives;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.AbstractTestCase;
import m3x.m3g.primitives.Section;

public class ColorRGBTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ColorRGBA color = new ColorRGBA(10, 20, 30, 128);
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(color);
      ColorRGBA deserialized = (ColorRGBA)M3GSupport.bytesToObject(serialized, ColorRGBA.class);
      this.doTestAccessors(color, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
