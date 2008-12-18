package m3x.translation;

public abstract class Object3DConverter 
        implements XmlToBinaryConverter, BinaryToXmlConverter
{
    public void toXml(Translator translator, m3x.m3g.Object3D from)
    {
        //A subclass is responsible for actually creating the object.
        m3x.xml.Object3D to = translator.getObject(from);

        //user id
        to.setUserID(from.getUserID());

        //FIXME animation tracks
    }

    public void toBinary(Translator translator, m3x.xml.Object3D from)
    {
        //A subclass is responsible for actually creating the object.
        m3x.m3g.Object3D to = translator.getObject(from);

        //user id
        to.setUserID(from.getUserID());

        //animation tracks
        for (m3x.xml.AnimationTrack xmlAT : from.getAnimationTrack())
        {
            m3x.m3g.AnimationTrack m3gAT = (m3x.m3g.AnimationTrack)translator.getObject(xmlAT);
            to.addAnimationTrack(m3gAT);
        }
    }
}
