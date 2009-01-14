package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;



public class CameraConverter extends NodeConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Camera to = new m3x.m3g.Camera();
        setFromXML(translator, to, (m3x.xml.Camera)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Camera to, m3x.xml.Camera from)
    {
        super.setFromXML(translator, to, from);
        //TODO implement
    }
}
