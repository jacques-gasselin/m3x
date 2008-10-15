package m3x.translation;

import m3x.m3g.objects.Texture2D;

public class Texture2DTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    Texture2DTranslator translator = new Texture2DTranslator();
    
    m3x.xml.Texture2D t1 = new m3x.xml.Texture2D();
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    t1.setAnimationTrackInstance(ati);
    
    m3x.xml.Image2D image = new m3x.xml.Image2D();
    section.getObjects().add(image);
    m3x.xml.Image2DInstance imageInstance = new m3x.xml.Image2DInstance();
    imageInstance.setRef(image);
    t1.setImage2DInstance(imageInstance);   
    
    translator.set(t1, m3xRoot, null);
    try
    {
      Texture2D t2 = (Texture2D)translator.toM3G();
      fail("Control should not go here.");
    }
    catch (Exception e)
    {
      //TODO: should fail
    }
  }
}
