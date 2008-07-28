package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class CompositingModeTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();                                  
    try
    {   
      CompositingMode mode = new CompositingMode(animationTracks,
                                                 userParameters,
                                                 true,
                                                 true,
                                                 true,
                                                 true,
                                                 -1,
                                                 128,
                                                 0.1f,
                                                 0.2f);
      byte[] serialized = M3GSupport.objectToBytes(mode);
      CompositingMode deserialized = (CompositingMode)M3GSupport.bytesToObject(serialized, CompositingMode.class);
      this.doTestAccessors(mode, deserialized);
    }
    catch (Exception e)
    {
      return;
    }    
    fail("FileFormatException not thrown!");
  }
  
  public void testFileFormatException()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();                                  
    try
    {   
      CompositingMode mode = new CompositingMode(animationTracks,
                                                 userParameters,
                                                 true,
                                                 true,
                                                 true,
                                                 true,
                                                 CompositingMode.MODULATE_X2,
                                                 128,
                                                 0.1f,
                                                 0.2f);
      byte[] serialized = M3GSupport.objectToBytes(mode);
      CompositingMode deserialized = (CompositingMode)M3GSupport.bytesToObject(serialized, CompositingMode.class);
      this.doTestAccessors(mode, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
