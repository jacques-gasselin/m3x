package m3x.translation;

import m3x.m3g.CompositingMode;
import m3x.m3g.PolygonMode;
import m3x.xml.CompositingModeBlendType;
import m3x.xml.PolygonCullingModeType;
import m3x.xml.PolygonShadingModeType;
import m3x.xml.PolygonWindingModeType;

public class PolygonModeTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    /*PolygonModeTranslator translator = new PolygonModeTranslator();
        
    m3x.xml.PolygonMode pm1 = new m3x.xml.PolygonMode();
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    pm1.setAnimationTrackInstance(ati);

    pm1.setCulling(PolygonCullingModeType.BACK);
    pm1.setLocalCameraLightingEnabled(true);
    pm1.setPerspectiveCorrectionEnabled(false);
    pm1.setShading(PolygonShadingModeType.SMOOTH);
    pm1.setTwoSidedLightingEnabled(true);
    pm1.setWinding(PolygonWindingModeType.CW);
    translator.set(pm1, m3xRoot, null);
    PolygonMode pm2 = (PolygonMode)translator.toM3G();
   
    assertEquals(toM3G(pm1.getCulling()), pm2.getCulling());
    assertEquals(toM3G(pm1.getShading()), pm2.getShading());
    assertEquals(toM3G(pm1.getWinding()), pm2.getWinding());
    assertTrue(pm1.isLocalCameraLightingEnabled() == pm2.isLocalCameraLightingEnabled());
    assertTrue(pm1.isPerspectiveCorrectionEnabled() == pm2.isPerspectiveCorrectionEnabled());
    assertTrue(pm1.isTwoSidedLightingEnabled() == pm2.isTwoSidedLightingEnabled());*/
  }

  /*private int toM3G(PolygonCullingModeType culling)
  {
    if (culling.equals(PolygonCullingModeType.BACK))
    {
      return PolygonMode.CULL_BACK;
    }
    else
    if (culling.equals(PolygonCullingModeType.FRONT))
    {
      return PolygonMode.CULL_FRONT;
    }
    else
    if (culling.equals(PolygonCullingModeType.NONE))
    {
      return PolygonMode.CULL_NONE;
    }
    throw new IllegalArgumentException(culling.toString());
  }
  
  private int toM3G(PolygonWindingModeType winding)
  {
    if (winding.equals(PolygonWindingModeType.CW))
    {
      return PolygonMode.WINDING_CW;
    }
    else
    if (winding.equals(PolygonWindingModeType.CCW))
    {
      return PolygonMode.WINDING_CCW;
    }
    throw new IllegalArgumentException(winding.toString());
  }

  private int toM3G(PolygonShadingModeType polygonShadingModeType)
  {
    if (polygonShadingModeType.equals(PolygonShadingModeType.FLAT))
    {
      return PolygonMode.SHADE_FLAT;
    }
    else
    if (polygonShadingModeType.equals(PolygonShadingModeType.SMOOTH))
    {
      return PolygonMode.SHADE_SMOOTH;
    }
    throw new IllegalArgumentException(polygonShadingModeType.toString());
  }*/
}
