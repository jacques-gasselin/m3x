package m3x.translation;

import m3x.m3g.objects.AnimationTrack;
import m3x.xml.AnimationTargetType;

/**
 * TODO: add comp. of values between m3x and m3g if possible.
 * 
 * @author jsaarinen
 */
public class AnimationTrackTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    AnimationTrackTranslator translator = new AnimationTrackTranslator();
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    at.setProperty(AnimationTargetType.ALPHA);

    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationController ac = new m3x.xml.AnimationController();
    section.getObjects().add(ac);
    m3x.xml.AnimationControllerInstance aci = new m3x.xml.AnimationControllerInstance();
    aci.setRef(ac);
    at.setAnimationControllerInstance(aci);
    
    m3x.xml.KeyframeSequence ks = new m3x.xml.KeyframeSequence();
    section.getObjects().add(ks);
    m3x.xml.KeyframeSequenceInstance ksi = new m3x.xml.KeyframeSequenceInstance();
    ksi.setRef(ks);
    at.setKeyframeSequenceInstance(ksi);
    
    translator.set(at, m3xRoot, null);
    try
    {
      AnimationTrack m3gAT = (AnimationTrack)translator.toM3G();
      fail("Control should not arrive here.");
    }
    catch (Exception e)
    {
      //TODO: this is ok for now
    }
  }
}
