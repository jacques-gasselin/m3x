package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class MaterialConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Material to = new m3x.m3g.Material();
        setFromXML(translator, to, (m3x.xml.Material)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Material to, m3x.xml.Material from)
    {
        super.setFromXML(translator, to, from);
        //TODO
    }
}
