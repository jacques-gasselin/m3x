package m3x.translation;

import m3x.translation.m3g.XmlToBinaryTranslator;
import m3x.m3g.AnimationController;

public class AnimationControllerTranslatorTest extends TranslatorSupport
{
    public void testTranslator()
    {
        XmlToBinaryTranslator translator = new XmlToBinaryTranslator("1.0");

        m3x.xml.AnimationController ac = new m3x.xml.AnimationController();
        ac.setActiveIntervalEnd(1);
        ac.setActiveIntervalStart(2);
        ac.setReferenceSequenceTime(0.5f);
        ac.setReferenceWorldTime(3);
        ac.setSpeed(1.0f);
        ac.setWeight(2.0f);

        AnimationController m3gAC = (AnimationController) translator.getObject(ac);

        assertTrue(ac.getActiveIntervalEnd() == m3gAC.getActiveIntervalEnd());
        assertTrue(ac.getActiveIntervalStart() == m3gAC.getActiveIntervalStart());
        assertTrue(ac.getReferenceSequenceTime() == m3gAC.getReferenceSequenceTime());
        assertTrue(ac.getReferenceWorldTime() == m3gAC.getReferenceWorldTime());
        assertTrue(ac.getSpeed() == m3gAC.getSpeed());
        assertTrue(ac.getWeight() == m3gAC.getWeight());
    }
}
