package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.CompositingMode;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.CompositingModeType;
import m3x.xml.NodeType;
import m3x.xml.Object3DType;

public class CompositingModeTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.CompositingMode cm = (m3x.xml.CompositingMode)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

    try
    {
      this.m3gObject = new m3x.m3g.objects.CompositingMode(animationTracks, 
          userParameters, 
          cm.isDepthTestEnabled(),
          cm.isDepthWriteEnabled(),
          cm.isColorWriteEnabled(),
          cm.isAlphaWriteEnabled(),
          toM3G(cm.getBlending()),
          (int)(cm.getAlphaThreshold() * 255.0f + 0.5f),
          cm.getDepthOffsetFactor().floatValue(),
          cm.getDepthOffsetUnits().floatValue());
    }
    catch (FileFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
    return this.m3gObject;
  }

  private int toM3G(CompositingModeType blending)
  {
    if (blending.toString().equals(CompositingModeType.ALPHA))
    {
      return CompositingMode.ALPHA;
    }
    if (blending.toString().equals(CompositingModeType.ALPHA_ADD))
    {
      return CompositingMode.ALPHA_ADD;
    }
    if (blending.toString().equals(CompositingModeType.ALPHA_DARKEN))
    {
      // TODO: what to return here?
      //return CompositingMode.ALPHA_ADD;
      throw new IllegalArgumentException(blending.toString());
    }
    if (blending.toString().equals(CompositingModeType.ALPHA_PREMULTIPLY))
    {
      // TODO: what to return here?
      //return CompositingMode.ALPHA_ADD;
      throw new IllegalArgumentException(blending.toString());
    }
    if (blending.toString().equals(CompositingModeType.MODULATE))
    {
      return CompositingMode.MODULATE;
    }
    if (blending.toString().equals(CompositingModeType.MODULATE_INV))
    {
      // TODO: what to return here?
      //return CompositingMode.MODULATE_INV;
      throw new IllegalArgumentException(blending.toString());
    }
    if (blending.toString().equals(CompositingModeType.MODULATE_X_2))
    {
      return CompositingMode.MODULATE_X2;
    }
    if (blending.toString().equals(CompositingModeType.REPLACE))
    {
      return CompositingMode.REPLACE;
    }
    throw new IllegalArgumentException(blending.toString());
  }

  public Object3DType toXML()
  {
    return null;
  }
}
