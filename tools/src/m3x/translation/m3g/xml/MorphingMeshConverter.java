package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class MorphingMeshConverter extends MeshConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.MorphingMesh to = new m3x.m3g.MorphingMesh();
        setFromXML(translator, to, (m3x.xml.MorphingMesh)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.MorphingMesh to, m3x.xml.MorphingMesh from)
    {
        super.setFromXML(translator, to, from);
        //TODO
    }
}
