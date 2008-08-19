package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.NodeType;
import m3x.xml.Object3DType;

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
            polygonMode.getCulling().ordinal(),
            polygonMode.getShading().ordinal(),
            polygonMode.getWinding().ordinal(),
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

  public Object3DType toXML()
  {
    return null;
  }
}
