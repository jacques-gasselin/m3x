package m3x.m3g;

import m3x.m3g.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationControllerTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    AnimationController animationController = new AnimationController(animationTracks,
                                                                      userParameters,
                                                                      0.1f,
                                                                      0.2f,
                                                                      1,
                                                                      2,
                                                                      0.3f,
                                                                      6);
                                                                      
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(animationController);
      AnimationController deserialized = (AnimationController)M3GSupport.bytesToObject(serialized, AnimationController.class);
      this.doTestAccessors(animationController, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
