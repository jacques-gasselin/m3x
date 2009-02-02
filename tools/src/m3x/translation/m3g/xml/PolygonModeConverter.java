package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


public class PolygonModeConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.PolygonMode to = new m3x.m3g.PolygonMode();
        setFromXML(translator, to, (m3x.xml.PolygonMode)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.PolygonMode to, m3x.xml.PolygonMode from)
    {
        super.setFromXML(translator, to, from);

        to.setCulling(from.getCulling().value());
        to.setShading(from.getShading().value());
        to.setWinding(from.getWinding().value());
        to.setLocalCameraLightingEnabled(from.isLocalCameraLightingEnabled());
        to.setPerspectiveCorrectionEnabled(from.isPerspectiveCorrectionEnabled());
        to.setTwoSidedLightingEnabled(from.isTwoSidedLightingEnabled());

    }
}
