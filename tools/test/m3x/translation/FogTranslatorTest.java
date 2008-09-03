package m3x.translation;

import m3x.m3g.objects.Fog;
import m3x.xml.FogEquationType;

public class FogTranslatorTest extends AbstractTranslatorTest
{
  public void testTranslator()
  {
    m3x.xml.Fog m3xFog = new m3x.xml.Fog();
    m3xFog.setFar(1.0f);
    m3xFog.setMode(FogEquationType.LINEAR);
    m3xFog.setNear(0.1f);
    m3xFog.setUserID(666L);
    
    FogTranslator translator = new FogTranslator();
    translator.set(m3xFog, null, null);
    Fog m3gFog = (Fog)translator.toM3G();
    //assertColorRGBEquals(m3gFog.getColor(), m3xFog.getColor());
    assertTrue(m3gFog.getFar() == m3xFog.getFar().floatValue());
    assertTrue(m3gFog.getNear() == m3xFog.getNear().floatValue());
    //assertTrue(m3gFog.getMode() == FogEquationType.LINEAR);
    
  }
}
