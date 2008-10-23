package m3x.translation;

import m3x.m3g.Background;
import m3x.xml.BackgroundRepeatType;

/**
 * TODO: add tests
 * 
 * @author jsaarinen
 */
public class BackgroundTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    BackgroundTranslator translator = new BackgroundTranslator();
        
    m3x.xml.Background b1 = new m3x.xml.Background();
    b1.setBackgroundImageModeX(BackgroundRepeatType.BORDER);
    b1.setBackgroundImageModeY(BackgroundRepeatType.REPEAT);
    b1.setColorClearEnabled(false);
    b1.setCropHeight(100);
    b1.setCropWidth(200);
    b1.setCropX(50);
    b1.setCropY(120);
    b1.setDepthClearEnabled(true);
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    b1.setAnimationTrackInstance(ati);

    m3x.xml.Image2D i = new m3x.xml.Image2D();
    section.getObjects().add(i);
    m3x.xml.Image2DInstance ii = new m3x.xml.Image2DInstance();
    ii.setRef(i);
    b1.setImage2DInstance(ii);
    
    b1.getBackgroundColor().clear();
    b1.getBackgroundColor().add((short)1);
    b1.getBackgroundColor().add((short)2);
    b1.getBackgroundColor().add((short)3);
    b1.getBackgroundColor().add((short)4);

    translator.set(b1, m3xRoot, null);
    Background b2 = (Background)translator.toM3G();
   
    // TODO: requires work in the translator class to map value() => an integer
    //assertTrue(Integer.parseInt(b1.getBackgroundImageModeX().value()) == b2.getBackgroundImageModeX());
    //assertTrue(Integer.parseInt(b1.getBackgroundImageModeY().value()) == b2.getBackgroundImageModeY());
    assertTrue(b1.getCropHeight() == b2.getCropHeight());
    assertTrue(b1.getCropWidth() == b2.getCropWidth());
    assertTrue(b1.getCropX() == b2.getCropX());
    assertTrue(b1.getCropY() == b2.getCropY());
    assertTrue(b1.isColorClearEnabled() == b2.isColorClearEnabled());
    assertTrue(b1.isDepthClearEnabled() == b2.isDepthClearEnabled());
  }
}
