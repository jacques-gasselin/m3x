package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class LightConverter extends NodeConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Light to = new m3x.m3g.Light();
        setFromXML(translator, to, (m3x.xml.Light)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Light to, m3x.xml.Light from)
    {
        super.setFromXML(translator, to, from);
        //TODO
    }
}
