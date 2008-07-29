package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.MorphingMesh.TargetBuffer;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class MorphingMeshTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix matrix = getMatrix();
                            
    try
    {   
      TargetBuffer morphTarget = new TargetBuffer(new ObjectIndex(1), 1.0f);
      TargetBuffer[] targets = new TargetBuffer[] { morphTarget };
      MorphingMesh mesh = new MorphingMesh(animationTracks,
                                           userParameters,
                                           matrix,
                                           true,
                                           true,
                                           (byte)128,
                                           0,
                                           targets);
      byte[] serialized = M3GSupport.objectToBytes(mesh);
      MorphingMesh deserialized = (MorphingMesh)M3GSupport.bytesToObject(serialized, MorphingMesh.class);
      this.doTestAccessors(mesh, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
