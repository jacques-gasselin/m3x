package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;

public class AppearanceTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Appearance appearance = new Appearance(animationTracks,
                                           userParameters,
                                           (byte)127,
                                           new ObjectIndex(1),
                                           new ObjectIndex(2),
                                           new ObjectIndex(3),
                                           new ObjectIndex(4),
                                           new ObjectIndex[] {new ObjectIndex(5)});
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(appearance);
      Appearance deserialized = (Appearance)M3GSupport.bytesToObject(serialized, Appearance.class);
      this.doTestAccessors(appearance, deserialized);
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