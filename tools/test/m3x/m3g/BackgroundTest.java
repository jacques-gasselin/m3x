package m3x.m3g;

import m3x.m3g.Background;
import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

public class BackgroundTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    try
    {   
      Background background = new Background(animationTracks,
          userParameters,
          new ColorRGBA(1, 2, 3, 4),
          new ObjectIndex(1),
          Background.MODE_BORDER,
          Background.MODE_REPEAT,
          0,
          0,
          128,
          128,
          true,
          true);
      byte[] serialized = M3GSupport.objectToBytes(background);
      Background deserialized = (Background)M3GSupport.bytesToObject(serialized, Background.class);
      this.doTestAccessors(background, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testFileFormatException1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    try
    {   
      Background background = new Background(animationTracks,
          userParameters,
          new ColorRGBA(1, 2, 3, 4),
          new ObjectIndex(1),
          -1,
          Background.MODE_REPEAT,
          0,
          0,
          128,
          128,
          true,
          true);
      byte[] serialized = M3GSupport.objectToBytes(background);
      Background deserialized = (Background)M3GSupport.bytesToObject(serialized, Background.class);
      this.doTestAccessors(background, deserialized);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException not thrown!");
  }
  
  public void testFileFormatException2()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    try
    {   
      Background background = new Background(animationTracks,
          userParameters,
          new ColorRGBA(1, 2, 3, 4),
          new ObjectIndex(1),
          Background.MODE_BORDER,
          -1,
          0,
          0,
          128,
          128,
          true,
          true);
      byte[] serialized = M3GSupport.objectToBytes(background);
      Background deserialized = (Background)M3GSupport.bytesToObject(serialized, Background.class);
      this.doTestAccessors(background, deserialized);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException not thrown!");
  }  
}
