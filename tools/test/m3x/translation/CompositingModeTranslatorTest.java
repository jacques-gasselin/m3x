package m3x.translation;

import m3x.m3g.CompositingMode;
import m3x.xml.CompositingModeType;

public class CompositingModeTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    CompositingModeTranslator translator = new CompositingModeTranslator();
        
    m3x.xml.CompositingMode c1 = new m3x.xml.CompositingMode();
    c1.setAlphaThreshold(0.1f);
    c1.setAlphaWriteEnabled(false);
    c1.setBlending(CompositingModeType.ALPHA);
    c1.setColorWriteEnabled(true);
    c1.setDepthOffsetFactor(0.1f);
    c1.setDepthOffsetUnits(1.0f);
    c1.setDepthTestEnabled(true);
    c1.setDepthWriteEnabled(false);
       
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    c1.setAnimationTrackInstance(ati);

    translator.set(c1, m3xRoot, null);
    CompositingMode c2 = (CompositingMode)translator.toM3G();
    
    //TODO: shouldn't alpha threshold be an integer
    //assertTrue(c1.getAlphaThreshold() == c2.getAlphaThreshold());
    assertTrue(c1.getDepthOffsetFactor() == c2.getDepthOffsetFactor());
    assertTrue(c1.getDepthOffsetUnits() == c2.getDepthOffsetUnits());
    assertTrue(c1.isAlphaWriteEnabled() == c2.isAlphaWriteEnabled());
    assertTrue(c1.isColorWriteEnabled() == c2.isColorWriteEnabled());
    assertTrue(c1.isDepthTestEnabled() == c2.isDepthTestEnabled());
    assertTrue(c1.isDepthWriteEnabled() == c2.isDepthWriteEnabled());
  }
}
