package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

public class MaterialTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ColorRGB ambient = new ColorRGB(0.1f, 0.2f, 0.3f);
    ColorRGBA diffuse = new ColorRGBA(0.4f, 0.5f, 0.6f, 0.2f);
    ColorRGB emissive = new ColorRGB(128, 128, 128);
    ColorRGB specular = new ColorRGB(0.7f, 0.8f, 0.9f);
    Material material = new Material(animationTracks,
                                     userParameters,
                                     ambient,
                                     diffuse,
                                     emissive,
                                     specular,
                                     1.5f,
                                     true);
                                     
                                     
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(material);
      Material deserialized = (Material)M3GSupport.bytesToObject(serialized, Material.class);
      this.doTestAccessors(material, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}