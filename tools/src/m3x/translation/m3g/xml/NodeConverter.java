package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;

/**
 *
 * @author jgasseli
 */
public class NodeConverter extends TransformableConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        super.toBinary(translator, originalFrom);
        //A subclass is responsible for actually creating the object.
        m3x.m3g.Node to = (m3x.m3g.Node)translator.getObject(originalFrom);
        m3x.xml.Node from = (m3x.xml.Node)originalFrom;

        to.setAlphaFactor(from.getAlphaFactor());
        to.setPickingEnabled(from.isPickingEnabled());
        to.setRenderingEnabled(from.isRenderingEnabled());
        to.setScope(from.getScope());
    }
}
