package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;


/**
 * Translator for Texture2D object.
 * 
 * @author jsaarinen
 */
public class Texture2DConverter extends TransformableConverter
{
    @Override
    public m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from)
    {
        m3x.m3g.Texture2D to = new m3x.m3g.Texture2D();
        setFromXML(translator, to, (m3x.xml.Texture2D)from);
        return to;
    }

    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Texture2D to, m3x.xml.Texture2D from)
    {
        super.setFromXML(translator, to, from);
        //TODO
    }
}
