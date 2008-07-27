package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;

public class Texture2DTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();                           
    try
    {   
      Texture2D texture = new Texture2D(animationTracks,
                                        userParameters,
                                        new Vector3D(0.1f, 0.2f, 0.3f),
                                        new Vector3D(0.4f, 0.5f, 0.6f),
                                        (float)Math.PI,
                                        new Vector3D(0.7f, 0.8f, 0.9f),
                                        new ObjectIndex(1),
                                        new ColorRGB(10, 20, 30),
                                        Texture2D.FUNC_DECAL,
                                        Texture2D.WRAP_CLAMP,
                                        Texture2D.WRAP_REPEAT,
                                        Texture2D.FILTER_LINEAR,
                                        Texture2D.FILTER_BASE_LEVEL);
                                        
      byte[] serialized = M3GSupport.objectToBytes(texture);
      Texture2D deserialized = (Texture2D)M3GSupport.bytesToObject(serialized, Texture2D.class);
      this.doTestAccessors(texture, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
