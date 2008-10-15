package m3x.translation;

import m3x.m3g.objects.Group;

public class GroupTranslatorText extends TranslatorSupport
{
  public void testTranslator()
  {
    GroupTranslator translator = new GroupTranslator();
    
    m3x.xml.Group group1 = new m3x.xml.Group();
    group1.setAlphaFactor(0.5f);
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    group1.setAnimationTrackInstance(ati);
   
    this.fillTransformableData(group1);
    
    group1.setPickingEnabled(false);
    group1.setRenderingEnabled(true);
    group1.setScope(10);
    
    translator.set(group1, null, null);
    Group group2 = (Group)translator.toM3G();
    
    assertTrue(group1.isPickingEnabled() == group2.isEnablePicking());
    assertTrue(group1.isRenderingEnabled() == group2.isEnableRendering());
    assertTrue((byte)(group1.getAlphaFactor() * 255.0f + 0.5) == group2.getAlphaFactor());
    assertTrue(group1.getScope() == group2.getScope());
  }
}
