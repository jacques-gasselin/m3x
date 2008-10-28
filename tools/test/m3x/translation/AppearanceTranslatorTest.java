package m3x.translation;

import m3x.m3g.Appearance;

/**
 * TODO: add more tests
 * 
 * @author jsaarinen
 */
public class AppearanceTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    AppearanceTranslator translator = new AppearanceTranslator();
    
    m3x.xml.Appearance app1 = new m3x.xml.Appearance();
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    app1.setAnimationTrackInstance(ati);
    
    m3x.xml.CompositingMode cm = new m3x.xml.CompositingMode();
    section.getObjects().add(cm);
    m3x.xml.CompositingModeInstance cmi = new m3x.xml.CompositingModeInstance();
    cmi.setRef(cm);
    app1.setCompositingModeInstance(cmi);
    
    m3x.xml.Fog f = new m3x.xml.Fog();
    section.getObjects().add(f);
    m3x.xml.FogInstance fi = new m3x.xml.FogInstance();
    fi.setRef(f);
    app1.setFogInstance(fi);
        
    app1.setLayer(2);
   
    m3x.xml.Material m = new m3x.xml.Material();
    section.getObjects().add(m);
    m3x.xml.MaterialInstance mi = new m3x.xml.MaterialInstance();
    mi.setRef(m);
    app1.setMaterialInstance(mi);
    
    translator.set(app1, m3xRoot, null);
    
    m3x.xml.PolygonMode p = new m3x.xml.PolygonMode();
    section.getObjects().add(p);
    m3x.xml.PolygonModeInstance pi = new m3x.xml.PolygonModeInstance();
    pi.setRef(p);
    app1.setPolygonModeInstance(pi);
    
    Appearance app2 = (Appearance)translator.toM3G();
    
    assertTrue(app1.getLayer() == app2.getLayer());
  }
}
