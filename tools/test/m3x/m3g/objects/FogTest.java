package m3x.m3g.objects;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class FogTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
    Fog fog = new Fog(animationTracks,
                      userParameters,
                      color,
                      0.1f);
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(fog);
      Fog deserialized = (Fog)M3GSupport.bytesToObject(serialized, Fog.class);
      assertTrue(fog.getColor().equals(deserialized.getColor()));
      assertTrue(fog.getDensity() == deserialized.getDensity());
      assertTrue(fog.getFar() == 0.0f);
      assertTrue(fog.getNear() == 0.0f);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }

  public void testSerializationAndDeseriliazation2()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
    Fog fog = new Fog(animationTracks,
                      userParameters,
                      color,
                      0.2f,
                      0.3f);
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(fog);
      Fog deserialized = (Fog)M3GSupport.bytesToObject(serialized, Fog.class);
      assertTrue(fog.getColor().equals(deserialized.getColor()));
      assertTrue(fog.getDensity() == 0.0f);
      assertTrue(fog.getFar() == deserialized.getFar());
      assertTrue(fog.getNear() == deserialized.getNear());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
