package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.Mesh.SubMesh;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.objects.SkinnedMesh.BoneReference;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class SkinnedMeshTest extends AbstractTestCase
{
  public void testSerializationAndDeseriliazation1()
  {
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    Matrix matrix = getMatrix();
    SubMesh subMesh = new SubMesh();
    subMesh.appearance = new ObjectIndex(1);
    subMesh.indexBuffer = new ObjectIndex(2);
    SubMesh[] subMeshes = new SubMesh[] { subMesh };                    
    BoneReference boneReference = new BoneReference();
    boneReference.transformNode = new ObjectIndex(1);
    boneReference.firstVertex = 0;
    boneReference.vertexCount = 1;
    boneReference.weight = 66;
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
