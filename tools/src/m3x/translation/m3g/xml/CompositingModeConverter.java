package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class CompositingModeConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.CompositingMode to = new m3x.m3g.CompositingMode();
        setFromXML(translator, to, (m3x.xml.CompositingMode)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.CompositingMode to, m3x.xml.CompositingMode from)
    {
        super.setFromXML(translator, to, from);
        
        to.setBlending(from.getBlending().value());
        to.setAlphaThreshold(from.getAlphaThreshold());
        to.setAlphaWriteEnabled(from.isAlphaWriteEnabled());
        to.setColorWriteEnabled(from.isColorWriteEnabled());
        to.setDepthOffset(from.getDepthOffsetFactor(), from.getDepthOffsetUnits());
        to.setDepthTestEnabled(from.isDepthTestEnabled());
        to.setDepthWriteEnabled(from.isDepthWriteEnabled());
    }
}
