package m3x.translation.m3g.xml;

import m3x.translation.m3g.XmlTranslator;


public class AnimationControllerConverter extends Object3DConverter
{
    @Override
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D originalFrom)
    {
        m3x.xml.AnimationController from = (m3x.xml.AnimationController)originalFrom;
        m3x.m3g.AnimationController to = new m3x.m3g.AnimationController();
        translator.setObject(originalFrom, to);
        super.toBinary(translator, from);

        to.setSpeed(from.getSpeed());
        to.setWeight(from.getWeight());
        to.setReferenceSequenceTime(from.getReferenceSequenceTime());
        to.setReferenceWorldTime(from.getReferenceWorldTime());
        to.setActiveIntervalStart(from.getActiveIntervalStart());
        to.setActiveIntervalEnd(from.getActiveIntervalEnd());
    }

}
