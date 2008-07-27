package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;

public class ColorRGBATest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ColorRGB color = new ColorRGB(10, 20, 30);
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(color);
      ColorRGB deserialized = (ColorRGB)M3GSupport.bytesToObject(serialized, ColorRGB.class);
      this.doTestAccessors(color, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
