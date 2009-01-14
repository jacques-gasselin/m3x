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
        m3x.xml.CompositingMode cm = from.getCompositingMode();
        if (cm == null)
        {
            if (from.getCompositingModeInstance() != null)
            {
                cm = (m3x.xml.CompositingMode)
                    from.getCompositingModeInstance().getRef();
            }
        }
        if (cm == null)
        {
            return null;
        }
        return (m3x.m3g.CompositingMode) translator.getObject(cm);
    }

    private static m3x.m3g.Fog getFog(
        XmlToBinaryTranslator translator, m3x.xml.Appearance from)
    {
        m3x.xml.Fog fog = from.getFog();
        if (fog == null)
        {
            if (from.getFogInstance() != null)
            {
                fog = (m3x.xml.Fog)
                    from.getFogInstance().getRef();
            }
        }
        if (fog == null)
        {
            return null;
        }
        return (m3x.m3g.Fog) translator.getObject(fog);
    }

}
