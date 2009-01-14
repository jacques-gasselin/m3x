package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 *
 * @author jgasseli
 */
public abstract class IndexBufferConverter extends Object3DConverter
{
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.IndexBuffer to, m3x.xml.IndexBuffer from)
    {
        super.setFromXML(translator, to, from);
        //TODO
    }
}
