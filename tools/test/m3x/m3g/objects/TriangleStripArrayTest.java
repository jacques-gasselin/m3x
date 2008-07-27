package m3x.m3g.objects;

import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class TriangleStripArrayTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
    TriangleStripArray array = new TriangleStripArray(animationTracks,
                                                      userParameters,
                                                      (byte)0,
                                                      new int[] {1, 2, 3});
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(array);
      TriangleStripArray deserialized = (TriangleStripArray)M3GSupport.bytesToObject(serialized, TriangleStripArray.class);
      this.doTestAccessors(array, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    

    array = new TriangleStripArray(animationTracks,
                                   userParameters,
                                   (short)0,
                                   new int[] {1, 2, 3});
                            
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(array);
      TriangleStripArray deserialized = (TriangleStripArray)M3GSupport.bytesToObject(serialized, TriangleStripArray.class);
      this.doTestAccessors(array, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }       
  }
}
