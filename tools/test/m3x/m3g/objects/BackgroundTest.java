package m3x.m3g.objects;

import junit.framework.TestCase;
import java.io.*;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class BackgroundTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
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
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(background);
      Background deserialized = (Background)M3GSupport.bytesToObject(serialized, Background.class);
      assertTrue(background.getBackgroundColor().equals(deserialized.getBackgroundColor()));
      assertTrue(background.getBackgroundImage().getIndex() == deserialized.getBackgroundImage().getIndex());
      assertTrue(background.getBackgroundImageModeX() == deserialized.getBackgroundImageModeX());
      assertTrue(background.getBackgroundImageModeY() == deserialized.getBackgroundImageModeY());
      assertTrue(background.getCropX() == deserialized.getCropX());
      assertTrue(background.getCropY() == deserialized.getCropY());   
      assertTrue(background.getCropWidth() == deserialized.getCropWidth());
      assertTrue(background.getCropHeight() == deserialized.getCropHeight());
      assertTrue(background.isColorClearEnabled() == deserialized.isColorClearEnabled());
      assertTrue(background.isDepthClearEnabled() == deserialized.isDepthClearEnabled());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
