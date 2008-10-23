package m3x.m3g;

import m3x.m3g.Camera;
import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class CameraTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix matrix = getMatrix();
                                           
    try
    {   
      Camera camera = new Camera(animationTracks,
          userParameters,
          matrix,
          true,
          true,
          (byte)128,
          1,
          Camera.PROJECTION_TYPE_PERSPECTIVE,
          90.0f,
          1.33f,
          0.1f,
          1.0f);
      byte[] serialized = M3GSupport.objectToBytes(camera);
      Camera deserialized = (Camera)M3GSupport.bytesToObject(serialized, Camera.class);
      this.doTestAccessors(camera, deserialized);
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
    Matrix matrix = getMatrix();
                                           
    try
    {   
      Camera camera = new Camera(animationTracks,
          userParameters,
          matrix,
          true,
          true,
          (byte)128,
          1,
          -1,
          90.0f,
          1.33f,
          0.1f,
          1.0f);
      byte[] serialized = M3GSupport.objectToBytes(camera);
      Camera deserialized = (Camera)M3GSupport.bytesToObject(serialized, Camera.class);
      this.doTestAccessors(camera, deserialized);
    }
    catch (Exception e)
    {
      return;
    }    
    fail("FileFormatException not thrown!");
  }
}
