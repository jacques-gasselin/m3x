package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class SpriteTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Sprite sprite = new Sprite(animationTracks,
                               userParameters,
                               new ObjectIndex(1),
                               new ObjectIndex(2),
                               true,
                               0,
                               1,
                               2,
                               3);
                                     
                                     
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(sprite);
      Sprite deserialized = (Sprite)M3GSupport.bytesToObject(serialized, Sprite.class);
      this.doTestAccessors(sprite, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
