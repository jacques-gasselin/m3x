package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryConverter;
import m3x.translation.m3g.XmlTranslator;
import m3x.translation.m3g.Translator;

public abstract class Object3DConverter 
        implements XmlToBinaryConverter
{
    public final void toBinary(Translator translator, Object fromObject)
    {
        toBinary((XmlTranslator)translator, (m3x.xml.Object3D)fromObject);
    }

    public void toBinary(XmlTranslator translator, m3x.xml.Object3D from)
    {
        //A subclass is responsible for actually creating the object.
        m3x.m3g.Object3D to = translator.getObject(from);

        //user id
        to.setUserID(from.getUserID());

        //animation tracks
        for (m3x.xml.AnimationTrack xmlAT : from.getAnimationTrack())
        {
            m3x.m3g.AnimationTrack m3gAT = (m3x.m3g.AnimationTrack)translator.getReference(xmlAT);
            to.addAnimationTrack(m3gAT);
        }
    }
}
