package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Mesh.SubMesh;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class MeshTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix matrix = getMatrix();
    SubMesh subMesh = new SubMesh(new ObjectIndex(1), new ObjectIndex(2));
    SubMesh[] subMeshes = new SubMesh[] { subMesh };                    
                            
    try
    {   
      Mesh mesh = new Mesh(animationTracks,
          userParameters,
          matrix,
          true,
          true,
          (byte)128,
          0,
          new ObjectIndex(1),
          subMeshes);
      byte[] serialized = M3GSupport.objectToBytes(mesh);
      Mesh deserialized = (Mesh)M3GSupport.bytesToObject(serialized, Mesh.class);
      this.doTestAccessors(mesh, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}
