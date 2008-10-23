package m3x.m3g;

import m3x.m3g.Fog;
import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
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
      this.doTestAccessors(fog, deserialized);
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
      this.doTestAccessors(fog, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
