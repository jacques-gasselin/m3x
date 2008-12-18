package m3x.translation;


public class AnimationTrackConverter extends Object3DConverter
{
    @Override
    public void toXml(Translator translator, m3x.m3g.Object3D originalFrom)
    {
        m3x.m3g.AnimationTrack from = (m3x.m3g.AnimationTrack)originalFrom;
        m3x.xml.AnimationTrack to = new m3x.xml.AnimationTrack();
        translator.setObject(from, to);
        super.toXml(translator, from);

        //FIXME keyframe sequence.
        //FIXME animation controller.
    }

    @Override
    public void toBinary(Translator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.AnimationTrack from = (m3x.xml.AnimationTrack)originalFrom;
        m3x.m3g.AnimationTrack to = new m3x.m3g.AnimationTrack();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        //FIXME keyframe sequence.
        //FIXME animation controller.
    }
}
