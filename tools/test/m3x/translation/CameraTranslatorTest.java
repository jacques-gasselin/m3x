package m3x.translation;

import m3x.m3g.objects.Camera;
import m3x.xml.CameraProjectionModeType;

public class CameraTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    CameraTranslator translator = new CameraTranslator();
        
    m3x.xml.Camera c1 = new m3x.xml.Camera();
    c1.setAlphaFactor(0.1f);
    c1.setAspectRatio(1.0f);
    c1.setFar(2.0f);
    c1.setFovy(90.0f);
    c1.setNear(1.0f);

    c1.setProjectionType(CameraProjectionModeType.PERSPECTIVE);
    
    fillTransformableData(c1);
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    c1.setAnimationTrackInstance(ati);

    translator.set(c1, m3xRoot, null);
    Camera c2 = (Camera)translator.toM3G();
   
    assertTrue(c1.getAspectRatio() == c2.getAspectRatio());
    assertTrue(c1.getFar() == c2.getFar());
    assertTrue(c1.getFovy() == c2.getFovy());
    assertTrue(c1.getNear() == c2.getNear());
  }
}
