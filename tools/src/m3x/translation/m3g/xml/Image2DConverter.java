package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;

public class Image2DConverter extends Object3DConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Image2D to = new m3x.m3g.Image2D();
        setFromXML(translator, to, (m3x.xml.Image2D)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Image2D to, m3x.xml.Image2D from)
    {
        super.setFromXML(translator, to, from);
        to.setFormat(from.getFormat().value());
        to.setMutable(from.isMutable());
        to.setSize(from.getWidth(), from.getHeight());
        to.setPixels(from.getPixels());
        to.setPalette(from.getPalette());
    }
}
