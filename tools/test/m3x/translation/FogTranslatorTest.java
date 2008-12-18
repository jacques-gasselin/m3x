package m3x.translation;

import m3x.m3g.Fog;
import m3x.xml.FogEquationType;

public class FogTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    /*FogTranslator translator = new FogTranslator();

    m3x.xml.Fog m3xFog = new m3x.xml.Fog();
    m3xFog.setFar(1.0f);
    m3xFog.setMode(FogEquationType.LINEAR);
    m3xFog.setNear(0.1f);
    m3xFog.setUserID(666);
    m3xFog.getColor().clear();
    m3xFog.getColor().add((short)1);
    m3xFog.getColor().add((short)2);
    m3xFog.getColor().add((short)3);
    
    translator.set(m3xFog, null, null);
    Fog m3gFog = (Fog)translator.toM3G();
    assertColorRGBEquals(m3gFog.getColor(), m3xFog.getColor());
    assertTrue(m3gFog.getFar() == m3xFog.getFar().floatValue());
    assertTrue(m3gFog.getNear() == m3xFog.getNear().floatValue());
    assertTrue(m3gFog.getMode() == Fog.MODE_LINEAR);
    
    m3xFog = new m3x.xml.Fog();
    m3xFog.setMode(FogEquationType.EXPONENTIAL);
    m3xFog.setDensity(0.6f);
    m3xFog.setUserID(666);
    m3xFog.getColor().clear();
    m3xFog.getColor().add((short)4);
    m3xFog.getColor().add((short)5);
    m3xFog.getColor().add((short)6);
    
    translator.set(null);
    translator.set(m3xFog, null, null);
    m3gFog = (Fog)translator.toM3G();
    assertTrue(m3gFog.getDensity() == m3xFog.getDensity());
    assertTrue(m3gFog.getMode() == Fog.MODE_EXPONENTIAL);*/
  }
}
