package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlToBinaryConverter;
import m3x.translation.m3g.XmlToBinaryTranslator;

public abstract class Object3DConverter extends XmlToBinaryConverter
{
    protected final void setFromXML(XmlToBinaryTranslator translator, m3x.m3g.Object3D to, m3x.xml.Object3D from)
    {
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
