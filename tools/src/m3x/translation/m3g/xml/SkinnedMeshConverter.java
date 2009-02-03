package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class SkinnedMeshConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.SkinnedMesh to = new m3x.m3g.SkinnedMesh();
        setFromXML(translator, to, (m3x.xml.SkinnedMesh)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.SkinnedMesh to, m3x.xml.SkinnedMesh from)
    {
        super.setFromXML(translator, to, from);

        to.setSkeleton(getSkeleton(translator, from));

        for (m3x.xml.SkinnedMesh.Bone xmlBone : from.getBones())
        {
            m3x.m3g.Node bone = (m3x.m3g.Node)
                translator.getReference((m3x.xml.Node) xmlBone.getRef());
            to.addTransform(bone, xmlBone.getWeight(),
                xmlBone.getFirstVertex(), xmlBone.getVertexCount());
        }
    }

    private static m3x.m3g.Group getSkeleton(
        XmlToBinaryTranslator translator, m3x.xml.SkinnedMesh from)
    {
        return (m3x.m3g.Group) translator.getReference(from.getSkeleton());
    }

}
