package m3x.translation;

import m3x.m3g.objects.AnimationTrack;
import m3x.m3g.objects.Image2D;
import m3x.xml.AnimationTargetType;
import m3x.xml.ImageBaseColorType;

public class Image2DTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    Image2DTranslator translator = new Image2DTranslator();
    
    m3x.xml.Image2D image1 = new m3x.xml.Image2D();

    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.SectionType section = new m3x.xml.SectionType();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    image1.setAnimationTrackInstance(ati);

    image1.setFormat(ImageBaseColorType.RGB);

    image1.setWidth(320L);
    image1.setHeight(256L);
    image1.setMutable(true);
    
    translator.set(image1, m3xRoot, null);
    Image2D image2 = (Image2D)translator.toM3G();
   
    assertTrue(image1.getWidth() == image2.getWidth());
    assertTrue(image1.getHeight() == image2.getHeight());
  }
}
