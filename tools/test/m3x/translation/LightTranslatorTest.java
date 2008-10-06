package m3x.translation;

import m3x.m3g.objects.Light;
import m3x.xml.LightType;

public class LightTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    LightTranslator translator = new LightTranslator();
   
    m3x.xml.Light light1 = new m3x.xml.Light();
    light1.setAlphaFactor(0.5f);
    light1.setAttenuationConstant(0.1f);
    light1.setAttenuationLinear(0.2f);
    light1.setAttenuationQuadratic(0.3f);
    light1.setIntensity(0.4f);
    light1.setMode(LightType.SPOT);
    light1.setPickingEnabled(true);
    light1.setRenderingEnabled(false);
    light1.setScope(1);
    light1.setSpotAngle(0.5f);
    light1.setSpotExponent(0.6f);
    
    light1.getColor().clear();
    light1.getColor().add((short)1);
    light1.getColor().add((short)2);
    light1.getColor().add((short)3);
    
    this.fillTransformableData(light1);
    
    translator.set(light1, null, null);
    
    Light light2 = (Light)translator.toM3G();
    
    assertTrue(light1.getAttenuationConstant() == light2.getAttenuationConstant());
    assertTrue(light1.getAttenuationLinear() == light2.getAttenuationLinear());
    assertTrue(light1.getAttenuationQuadratic() == light2.getAttenuationQuadratic());
    assertTrue(light1.getIntensity() == light2.getIntensity());
    assertTrue(light1.getSpotAngle() == light2.getSpotAngle());
    assertTrue(light1.getSpotExponent() == light2.getSpotExponent());
  }
}
