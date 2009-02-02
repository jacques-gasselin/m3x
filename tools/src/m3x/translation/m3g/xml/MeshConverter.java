package m3x.translation.m3g.xml;

import java.util.List;
import m3x.translation.m3g.XmlToBinaryTranslator;
import m3x.xml.MeshType;


public class MeshConverter extends NodeConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Mesh to = new m3x.m3g.Mesh();
        setFromXML(translator, to, (m3x.xml.Mesh)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Mesh to, m3x.xml.Mesh from)
    {
        super.setFromXML(translator, to, from);
        to.setVertexBuffer(getVertexBuffer(translator, from));
        
        List<MeshType.Submesh> submeshes = from.getSubmesh();
        to.setSubmeshCount(submeshes.size());
        for (int i = 0; i < submeshes.size(); ++i)
        {
            final MeshType.Submesh submesh = submeshes.get(i);
            to.setAppearance(i, getAppearance(translator, submesh));
            to.setIndexBuffer(i, getIndexBuffer(translator, submesh));
        }
    }

    private static m3x.m3g.VertexBuffer getVertexBuffer(
        XmlToBinaryTranslator translator, m3x.xml.Mesh from)
    {
        m3x.xml.VertexBuffer vb = getObjectOrInstance(
            from.getVertexBuffer(), from.getVertexBufferInstance());
        return (m3x.m3g.VertexBuffer) translator.getReference(vb);
    }

    private static m3x.m3g.Appearance getAppearance(
        XmlToBinaryTranslator translator, MeshType.Submesh from)
    {
        m3x.xml.Appearance a = getObjectOrInstance(
            from.getAppearance(), from.getAppearanceInstance());
        return (m3x.m3g.Appearance) translator.getReference(a);
    }

    private static m3x.m3g.IndexBuffer getIndexBuffer(
        XmlToBinaryTranslator translator, MeshType.Submesh from)
    {
        m3x.xml.IndexBuffer ib = getObjectOrInstance(
            from.getTriangleStripArray(), from.getTriangleStripArrayInstance());
        return (m3x.m3g.IndexBuffer) translator.getReference(ib);
    }

}
