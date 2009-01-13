package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;

/**
 *
 * @author jgasseli
 */
public abstract class TransformableConverter extends Object3DConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        super.toBinary(translator, originalFrom);
        //A subclass is responsible for actually creating the object.
        m3x.m3g.Transformable to = (m3x.m3g.Transformable)translator.getObject(originalFrom);
        m3x.xml.Transformable from = (m3x.xml.Transformable)originalFrom;

        to.setScale(from.getScale());
        to.setOrientation(from.getOrientation().getAngle(), from.getOrientation().getValue());
        to.setTranslation(from.getTranslation());
        to.setTransform(from.getTransform());
    }
}
