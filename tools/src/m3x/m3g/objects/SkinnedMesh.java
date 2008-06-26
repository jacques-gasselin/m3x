package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Mesh.SubMesh;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class SkinnedMesh extends Mesh implements M3GTypedObject
{
  public class BoneReference implements M3GSerializable
  {
    public ObjectIndex transformNode;
    public int firstVertex;
    public int vertexCount;
    public int weight;
    
    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
      this.transformNode.serialize(dataOutputStream, m3gVersion);
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.firstVertex));
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.vertexCount));
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.weight));
    }
  }
  
  private final ObjectIndex skeleton;
  private final int transformReferenceCount;
  private final BoneReference[] boneReferences;
  
  public SkinnedMesh(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, Matrix transform,
      boolean enableRendering, boolean enablePicking, byte alphaFactor,
      int scope, ObjectIndex vertexBuffer, SubMesh[] subMeshes,
      ObjectIndex skeleton,
      BoneReference[] boneReferences)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope, vertexBuffer, subMeshes);
    assert(skeleton != null);
    assert(boneReferences != null);
    this.skeleton = skeleton;
    this.transformReferenceCount = boneReferences.length;
    this.boneReferences = boneReferences;
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.SKINNED_MESH;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    this.skeleton.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.transformReferenceCount));
    for (BoneReference boneReference : this.boneReferences)
    {
      boneReference.serialize(dataOutputStream, m3gVersion);
    }
  }
}
