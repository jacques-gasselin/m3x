package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class PolygonModeTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
                                             
    try
    {   
      PolygonMode polygonMode = new PolygonMode(animationTracks,
          userParameters,
          PolygonMode.CULL_BACK,
          PolygonMode.SHADE_SMOOTH,
          PolygonMode.WINDING_CW,
          true,
          false,
          true);
      byte[] serialized = M3GSupport.objectToBytes(polygonMode);
      PolygonMode deserialized = (PolygonMode)M3GSupport.bytesToObject(serialized, PolygonMode.class);
      this.doTestAccessors(polygonMode, deserialized);
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
      PolygonMode polygonMode = new PolygonMode(animationTracks,
          userParameters,
          -1,
          PolygonMode.SHADE_SMOOTH,
          PolygonMode.WINDING_CW,
          true,
          false,
          true);
      byte[] serialized = M3GSupport.objectToBytes(polygonMode);
      PolygonMode deserialized = (PolygonMode)M3GSupport.bytesToObject(serialized, PolygonMode.class);
      this.doTestAccessors(polygonMode, deserialized);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException wasn't thrown!");
  }

  public void testFileFormatException2()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
                                             
    try
    {   
      PolygonMode polygonMode = new PolygonMode(animationTracks,
          userParameters,
          PolygonMode.CULL_FRONT,
          -1,
          PolygonMode.WINDING_CW,
          true,
          false,
          true);
      byte[] serialized = M3GSupport.objectToBytes(polygonMode);
      PolygonMode deserialized = (PolygonMode)M3GSupport.bytesToObject(serialized, PolygonMode.class);
      this.doTestAccessors(polygonMode, deserialized);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException wasn't thrown!");
  }

  public void testFileFormatException3()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
                                             
    try
    {   
      PolygonMode polygonMode = new PolygonMode(animationTracks,
          userParameters,
          PolygonMode.CULL_FRONT,
          PolygonMode.SHADE_FLAT,
          -1,
          true,
          false,
          true);
      byte[] serialized = M3GSupport.objectToBytes(polygonMode);
      PolygonMode deserialized = (PolygonMode)M3GSupport.bytesToObject(serialized, PolygonMode.class);
      this.doTestAccessors(polygonMode, deserialized);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException wasn't thrown!");
  }
}
