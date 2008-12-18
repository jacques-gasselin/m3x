package m3x.translation;


public class AnimationControllerConverter extends Object3DConverter
{
    @Override
    public void toXml(Translator translator, m3x.m3g.Object3D originalFrom)
    {
        m3x.m3g.AnimationController from = (m3x.m3g.AnimationController)originalFrom;
        m3x.xml.AnimationController to = new m3x.xml.AnimationController();
        translator.setObject(from, to);
        super.toXml(translator, from);

        to.setSpeed(from.getSpeed());
        to.setWeight(from.getWeight());
        to.setReferenceSequenceTime(from.getReferenceSequenceTime());
        to.setReferenceWorldTime(from.getReferenceWorldTime());
        to.setActiveIntervalStart(from.getActiveIntervalStart());
        to.setActiveIntervalEnd(from.getActiveIntervalEnd());
    }

    @Override
    public void toBinary(Translator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.AnimationController from = (m3x.xml.AnimationController)originalFrom;
        m3x.m3g.AnimationController to = new m3x.m3g.AnimationController();
        translator.setObject(from, to);
        super.toBinary(translator, from);

        to.setSpeed(from.getSpeed());
        to.setWeight(from.getWeight());
        to.setReferenceSequenceTime(from.getReferenceSequenceTime());
        to.setReferenceWorldTime(from.getReferenceWorldTime());
        to.setActiveIntervalStart(from.getActiveIntervalStart());
        to.setActiveIntervalEnd(from.getActiveIntervalEnd());
    }

}
