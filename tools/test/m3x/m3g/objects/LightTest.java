package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class LightTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix transform = getMatrix();
    ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
    try
    {   
      Light light = new Light(animationTracks, 
          userParameters, 
          transform, 
          true, 
          true, 
          (byte) 66, 
          1, 
          0.1f, 
          0.2f, 
          0.3f, 
          color, 
          Light.MODE_SPOT, 
          0.5f, 
          0.2f, 
          2.0f);
      byte[] serialized = M3GSupport.objectToBytes(light);
      Light deserialized = (Light)M3GSupport.bytesToObject(serialized, Light.class);
      assertTrue(light.getAttenuationConstant() == deserialized.getAttenuationConstant());
      assertTrue(light.getAttenuationLinear() == deserialized.getAttenuationLinear());
      assertTrue(light.getAttenuationQuadratic() == deserialized.getAttenuationQuadratic());
      assertTrue(light.getColor().equals(deserialized.getColor()));
      assertTrue(light.getIntensity() == deserialized.getIntensity());
      assertTrue(light.getMode() == deserialized.getMode());
      assertTrue(light.getSpotAngle() == deserialized.getSpotAngle());
      assertTrue(light.getSpotExponent() == deserialized.getSpotExponent());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
