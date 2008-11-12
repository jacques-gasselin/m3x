package m3x.m3g;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.Mesh.SubMesh;
import m3x.m3g.Object3D.UserParameter;
import m3x.m3g.SkinnedMesh.BoneReference;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class SkinnedMeshTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix matrix = getMatrix();
    SubMesh subMesh = new SubMesh(new ObjectIndex(1), new ObjectIndex(2));
    SubMesh[] subMeshes = new SubMesh[] { subMesh };                    
    BoneReference boneReference = new BoneReference(new ObjectIndex(1), 0, 1, 66);
    BoneReference[] boneReferences = new BoneReference[] {boneReference};                    
    
    try
    {   
      SkinnedMesh mesh = new SkinnedMesh(animationTracks,
                                         userParameters,
                                         matrix,
                                         true,
                                         true,
                                         (byte)128,
                                         0,
                                         new ObjectIndex(1),
                                         subMeshes,
                                         new ObjectIndex(1),
                                         boneReferences);
      byte[] serialized = M3GSupport.objectToBytes(mesh);
      SkinnedMesh deserialized = (SkinnedMesh)M3GSupport.bytesToObject(serialized, SkinnedMesh.class);
      this.doTestAccessors(mesh, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }    
  }
}