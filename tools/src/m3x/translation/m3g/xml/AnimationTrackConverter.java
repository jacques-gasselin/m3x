package m3x.translation.m3g.xml;


public class AnimationTrackConverter extends Object3DConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.AnimationTrack from = (m3x.xml.AnimationTrack)originalFrom;
        m3x.m3g.AnimationTrack to = new m3x.m3g.AnimationTrack();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        //FIXME keyframe sequence.
        //FIXME animation controller.
    }
}
