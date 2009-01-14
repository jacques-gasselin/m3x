package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;



public class WorldConverter extends GroupConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.World to = new m3x.m3g.World();
        setFromXML(translator, to, (m3x.xml.World)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.World to, m3x.xml.World from)
    {
        super.setFromXML(translator, to, from);
    }
}
