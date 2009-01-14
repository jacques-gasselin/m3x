package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 *
 * @author jgasseli
 */
public abstract class NodeConverter extends TransformableConverter
{
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Node to, m3x.xml.Node from)
    {
        super.setFromXML(translator, to, from);
        to.setAlphaFactor(from.getAlphaFactor());
        to.setPickingEnabled(from.isPickingEnabled());
        to.setRenderingEnabled(from.isRenderingEnabled());
        to.setScope(from.getScope());
    }
}
