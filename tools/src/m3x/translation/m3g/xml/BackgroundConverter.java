package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class BackgroundConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Background to = new m3x.m3g.Background();
        setFromXML(translator, to, (m3x.xml.Background)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Background to, m3x.xml.Background from)
    {
        super.setFromXML(translator, to, from);
        //TODO implement
    }
}
