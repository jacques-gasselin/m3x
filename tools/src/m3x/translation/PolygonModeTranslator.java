package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.PolygonMode;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.NodeType;
import m3x.xml.Object3DType;
import m3x.xml.PolygonCullingModeType;
import m3x.xml.PolygonShadingModeType;
import m3x.xml.PolygonWindingModeType;

public class PolygonModeTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.PolygonMode polygonMode = (m3x.xml.PolygonMode)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
  
    try
    {
      this.m3gObject = new m3x.m3g.objects.PolygonMode(animationTracks, 
            userParameters, 
            toM3G(polygonMode.getCulling()),
            toM3G(polygonMode.getShading()),
            toM3G(polygonMode.getWinding()),
            polygonMode.isTwoSidedLightingEnabled().booleanValue(),
            polygonMode.isLocalCameraLightingEnabled().booleanValue(),
            polygonMode.isPerspectiveCorrectionEnabled().booleanValue());
    }
    catch (FileFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
    return this.m3gObject;
  }

  private int toM3G(PolygonWindingModeType winding)
  {
    if (winding.toString().equals(PolygonWindingModeType.CW))
    {
      return PolygonMode.WINDING_CW;
    }
    if (winding.toString().equals(PolygonWindingModeType.CCW))
    {
      return PolygonMode.WINDING_CCW;
    }
    throw new IllegalArgumentException(winding.toString());
  }

  private int toM3G(PolygonShadingModeType shading)
  {
    if (shading.toString().equals(PolygonShadingModeType.SMOOTH))
    {
      return PolygonMode.SHADE_SMOOTH;
    }
    if (shading.toString().equals(PolygonShadingModeType.FLAT))
    {
      return PolygonMode.SHADE_FLAT;
    }
    throw new IllegalArgumentException(shading.toString());
  }

  private int toM3G(PolygonCullingModeType culling)
  {
    if (culling.toString().equals(PolygonCullingModeType.FRONT))
    {
      return PolygonMode.CULL_FRONT;
    }
    if (culling.toString().equals(PolygonCullingModeType.BACK))
    {
      return PolygonMode.CULL_BACK;
    }
    if (culling.toString().equals(PolygonCullingModeType.NONE))
    {
      return PolygonMode.CULL_NONE;
    }
    throw new IllegalArgumentException(culling.toString());
  }

  public Object3DType toXML()
  {
    return null;
  }
}
