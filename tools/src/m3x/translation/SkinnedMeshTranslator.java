package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.Mesh.SubMesh;
import m3x.m3g.objects.SkinnedMesh.BoneReference;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Object3DType;
import m3x.xml.MeshType.Submesh;
import m3x.xml.SkinnedMesh.Bone;

public class SkinnedMeshTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.SkinnedMesh sm = (m3x.xml.SkinnedMesh)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    m3x.xml.TransformableType transformable = (m3x.xml.TransformableType)sm;
    Matrix transform = getM3GTransformMatrix(transformable);
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
    
    int vertexBufferIndex = searchObjectIndex(this.m3xRoot, sm.getVertexBufferInstance());
    List<Submesh> list = sm.getSubmesh();
    SubMesh[] subMeshes = new SubMesh[list.size()];
    for (int i = 0; i < subMeshes.length; i++)
    {
      int indexBufferIndex = searchObjectIndex(this.m3xRoot, sm.getSubmesh().get(i).getTriangleStripArrayInstance());
      int appearanceIndex = searchObjectIndex(this.m3xRoot, sm.getSubmesh().get(i).getAppearanceInstance());
      
      SubMesh subMesh = new SubMesh(new ObjectIndex(indexBufferIndex), new ObjectIndex(appearanceIndex));
      subMeshes[i] = subMesh;
    }
    int skeletonIndex = searchObjectIndex(this.m3xRoot, sm.getGroup());
    
    try
    {
      this.m3gObject = new m3x.m3g.objects.SkinnedMesh(animationTracks, 
          userParameters, 
          transform,
          sm.isRenderingEnabled(), 
          sm.isPickingEnabled(),
          (byte)(sm.getAlphaFactor() * 255.0f + 0.5f),
          sm.getScope(), 
          new ObjectIndex(vertexBufferIndex),
          subMeshes,
          new ObjectIndex(skeletonIndex),
          toM3G(sm.getBone()));
    }
    catch (FileFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
    return this.m3gObject;
  }

  private BoneReference[] toM3G(List<Bone> bones)
  {
    BoneReference[] references = new BoneReference[bones.size()];
    for (int i = 0; i < references.length; i++)
    {
      Bone bone = bones.get(i);
      references[i] = new BoneReference(new ObjectIndex(searchObjectIndex(this.m3xRoot, bone.getRef())),
                                        bone.getFirstVertex().intValue(),
                                        bone.getVertexCount().intValue(),
                                        bone.getWeight().intValue());
    }
    return references;
  }

  public Object3DType toXML()
  {
    return null;
  }
}
