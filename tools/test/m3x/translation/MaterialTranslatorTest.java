package m3x.translation;

import m3x.m3g.Background;
import m3x.m3g.KeyframeSequence;
import m3x.m3g.Material;
import m3x.xml.BackgroundRepeatType;
import m3x.xml.KeyframeInterpolationType;
import m3x.xml.KeyframePlaybackType;
import m3x.xml.Keyframes;

/**
 * TODO: add tests
 * 
 * @author jsaarinen
 */
public class MaterialTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    MaterialTranslator translator = new MaterialTranslator();
        
    m3x.xml.Material material1 = new m3x.xml.Material();
    material1.getAmbientColor().add((short)1);
    material1.getAmbientColor().add((short)2);
    material1.getAmbientColor().add((short)3);
    material1.getDiffuseColor().add((short)4);
    material1.getDiffuseColor().add((short)5);
    material1.getDiffuseColor().add((short)6);
    material1.getDiffuseColor().add((short)13);;
    material1.getEmissiveColor().add((short)7);
    material1.getEmissiveColor().add((short)8);
    material1.getEmissiveColor().add((short)9);
    material1.getSpecularColor().add((short)10);
    material1.getSpecularColor().add((short)11);
    material1.getSpecularColor().add((short)12);
    material1.setShininess(2.0f);
    material1.setVertexColorTrackingEnabled(true);
       
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    material1.setAnimationTrackInstance(ati);

    translator.set(material1, m3xRoot, null);
    Material material2 = (Material)translator.toM3G();
   
    assertColorRGBEquals(material2.getAmbientColor(), material1.getAmbientColor());
    assertColorRGBAEquals(material2.getDiffuseColor(), material1.getDiffuseColor());
    assertColorRGBEquals(material2.getEmissiveColor(), material1.getEmissiveColor());
    assertColorRGBEquals(material2.getSpecularColor(), material1.getSpecularColor());
    assertTrue(material2.getShininess() == material1.getShininess());
    assertTrue(material2.isVertexColorTrackingEnabled() == material1.isVertexColorTrackingEnabled());
  }
}
