package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class AppearanceConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Appearance to = new m3x.m3g.Appearance();
        setFromXML(translator, to, (m3x.xml.Appearance)from);
        return to;
    }
    
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Appearance to, m3x.xml.Appearance from)
    {
        super.setFromXML(translator, to, from);
        to.setCompositingMode(getCompositingMode(translator, from));
        to.setFog(getFog(translator, from));
        //FIXME Polygon mode.
    }

    private static m3x.m3g.CompositingMode getCompositingMode(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.CompositingMode cm = getObjectOrInstance(
            from.getCompositingMode(), from.getCompositingModeInstance());
        return (m3x.m3g.CompositingMode) translator.getObject(cm);
    }

    private static m3x.m3g.Fog getFog(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.Fog fog = getObjectOrInstance(from.getFog(), from.getFogInstance());
        return (m3x.m3g.Fog) translator.getObject(fog);
    }

}
