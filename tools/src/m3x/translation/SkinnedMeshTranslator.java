package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.Object3D;
import m3x.m3g.Mesh.SubMesh;
import m3x.m3g.SkinnedMesh.BoneReference;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.MeshType.Submesh;
import m3x.xml.SkinnedMesh.Bone;

public class SkinnedMeshTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.SkinnedMesh sm = (m3x.xml.SkinnedMesh) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) sm;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int vertexBufferIndex = searchObjectIndex(this.getXmlRootObject(), sm.getVertexBufferInstance());
        List<Submesh> list = sm.getSubmesh();
        SubMesh[] subMeshes = new SubMesh[list.size()];
        for (int i = 0; i < subMeshes.length; i++)
        {
            int indexBufferIndex = searchObjectIndex(this.getXmlRootObject(), sm.getSubmesh().get(i).getTriangleStripArrayInstance());
            int appearanceIndex = searchObjectIndex(this.getXmlRootObject(), sm.getSubmesh().get(i).getAppearanceInstance());

            SubMesh subMesh = new SubMesh(new ObjectIndex(indexBufferIndex), new ObjectIndex(appearanceIndex));
            subMeshes[i] = subMesh;
        }
        int skeletonIndex = searchObjectIndex(this.getXmlRootObject(), sm.getGroup());

        try
        {
            this.setBinaryObject(new m3x.m3g.SkinnedMesh(animationTracks, userParameters, transform, sm.isRenderingEnabled(), sm.isPickingEnabled(), (byte) (sm.getAlphaFactor() * 255.0f + 0.5f), sm.getScope(), new ObjectIndex(vertexBufferIndex), subMeshes, new ObjectIndex(skeletonIndex), toM3G(sm.getBone())));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private BoneReference[] toM3G(List<Bone> bones)
    {
        BoneReference[] references = new BoneReference[bones.size()];
        for (int i = 0; i < references.length; i++)
        {
            Bone bone = bones.get(i);
            references[i] = new BoneReference(new ObjectIndex(searchObjectIndex(this.getXmlRootObject(), bone.getRef())),
                bone.getFirstVertex().intValue(),
                bone.getVertexCount().intValue(),
                bone.getWeight().intValue());
        }
        return references;
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}