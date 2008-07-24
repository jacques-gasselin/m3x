package m3x.m3g.objects;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class Image2DTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();                           
    try
    {   
      Image2D image = new Image2D(animationTracks,
          userParameters,
          Image2D.FORMAT_ALPHA,
          42,
          66,
          new byte[256 * 3],
          new byte[42 * 66]);
      byte[] serialized = M3GSupport.objectToBytes(image);
      Image2D deserialized = (Image2D)M3GSupport.bytesToObject(serialized, Image2D.class);
      this.doTestAccessors(image, deserialized);
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
