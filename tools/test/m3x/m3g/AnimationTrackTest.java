package m3x.m3g;


public class AnimationTrackTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    /*ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();                                                                    
    try
    {  
      AnimationTrack animationTrack = new AnimationTrack(animationTracks,
          userParameters,
          new ObjectIndex(1),
          new ObjectIndex(2),
          AnimationTrack.ALPHA);
      byte[] serialized = M3GSupport.objectToBytes(animationTrack);
      AnimationTrack deserialized = (AnimationTrack)M3GSupport.bytesToObject(serialized, AnimationTrack.class);
      this.doTestAccessors(animationTrack, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }*/
  }
  
  public void testFileFormatException()
  {
    /*ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters(); 
    try
    {  
      AnimationTrack animationTrack = new AnimationTrack(animationTracks,
          userParameters,
          new ObjectIndex(1),
          new ObjectIndex(2),
          -1);
      byte[] serialized = M3GSupport.objectToBytes(animationTrack);
      AnimationTrack deserialized = (AnimationTrack)M3GSupport.bytesToObject(serialized, AnimationTrack.class);
      this.doTestAccessors(animationTrack, deserialized);
    }
    catch (Exception e)
    {
      // this is what should happen
      return;
    }
    fail("FileFormatException not thrown!");*/
  }  
}
