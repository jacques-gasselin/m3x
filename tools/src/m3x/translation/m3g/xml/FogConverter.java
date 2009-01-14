package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class FogConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Fog to = new m3x.m3g.Fog();
        setFromXML(translator, to, (m3x.xml.Fog)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Fog to, m3x.xml.Fog from)
    {
        super.setFromXML(translator, to, from);
        //TODO implement
    }
}
