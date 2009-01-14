package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryTranslator;

/**
 *
 * @author jgasseli
 */
public abstract class TransformableConverter extends Object3DConverter
{
    protected final void setFromXML(XmlToBinaryTranslator translator,
        m3x.m3g.Transformable to, m3x.xml.Transformable from)
    {
        super.setFromXML(translator, to, from);
        to.setScale(from.getScale());
        to.setOrientation(from.getOrientation().getAngle(), from.getOrientation().getValue());
        to.setTranslation(from.getTranslation());
        to.setTransform(from.getTransform());
    }
}
