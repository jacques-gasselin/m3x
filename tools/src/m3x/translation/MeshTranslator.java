package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.Mesh.SubMesh;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.MeshType.Submesh;

public class MeshTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Mesh mesh = (m3x.xml.Mesh) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        m3x.xml.TransformableType transformable = (m3x.xml.TransformableType) mesh;
        Matrix transform = getM3GTransformMatrix(transformable);
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int vertexBufferIndex = searchObjectIndex(this.getXmlRootObject(), mesh.getVertexBufferInstance());
        List<Submesh> list = mesh.getSubmesh();
        SubMesh[] subMeshes = new SubMesh[list.size()];
        for (int i = 0; i < subMeshes.length; i++)
        {
            int indexBufferIndex = searchObjectIndex(this.getXmlRootObject(), mesh.getSubmesh().get(i).getTriangleStripArrayInstance().getRef());
            int appearanceIndex = searchObjectIndex(this.getXmlRootObject(), mesh.getSubmesh().get(i).getAppearanceInstance().getRef());

            SubMesh subMesh = new SubMesh(new ObjectIndex(indexBufferIndex), new ObjectIndex(appearanceIndex));
            subMeshes[i] = subMesh;
        }
        try
        {
            this.setBinaryObject(new m3x.m3g.objects.Mesh(animationTracks, userParameters, transform, mesh.isRenderingEnabled(), mesh.isPickingEnabled(), (byte) (mesh.getAlphaFactor() * 255.0f + 0.5f), mesh.getScope(), new ObjectIndex(vertexBufferIndex), subMeshes));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
