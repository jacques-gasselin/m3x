package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class WorldTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix transform = getMatrix();
    ObjectIndex[] children = new ObjectIndex[0];
    ObjectIndex activeCamera = new ObjectIndex(1);
    ObjectIndex background = new ObjectIndex(2);                           
    try
    {   
      World world = new World(animationTracks,
          userParameters,
          transform,
          true,
          true,
          (byte)16,
          2,
          children,
          activeCamera,
          background);
      byte[] serialized = M3GSupport.objectToBytes(world);
      World deserialized = (World)M3GSupport.bytesToObject(serialized, World.class);
      assertTrue(world.getActiveCamera().getIndex() == deserialized.getActiveCamera().getIndex());
      assertTrue(world.getBackground().getIndex() == deserialized.getBackground().getIndex());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
