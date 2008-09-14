package m3x.translation;

import m3x.m3g.objects.AnimationController;
import m3x.m3g.objects.Fog;
import m3x.m3g.objects.Object3D;
import m3x.xml.Object3DType;

public class AnimationControllerTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    AnimationControllerTranslator translator = new AnimationControllerTranslator();
    
    m3x.xml.AnimationController ac = new m3x.xml.AnimationController();
    ac.setActiveIntervalEnd(1);
    ac.setActiveIntervalStart(2);
    ac.setReferenceSequenceTime(0.5f);
    ac.setReferenceWorldTime(3);
    ac.setSpeed(1.0f);
    ac.setWeight(2.0f);
    
    translator.set(ac, null, null);
    AnimationController m3gAC = (AnimationController)translator.toM3G();
    
    assertTrue(ac.getActiveIntervalEnd() == m3gAC.getActiveIntervalEnd());
    assertTrue(ac.getActiveIntervalStart() == m3gAC.getActiveIntervalStart());
    assertTrue(ac.getReferenceSequenceTime() == m3gAC.getReferenceSequenceTime());
    assertTrue(ac.getReferenceWorldTime() == m3gAC.getReferenceWorldTime());
    assertTrue(ac.getSpeed() == m3gAC.getSpeed());
    assertTrue(ac.getWeight() == m3gAC.getWeight());
  }
}
