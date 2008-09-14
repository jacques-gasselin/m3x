package m3x.translation;


import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Light;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.LightType;
import m3x.xml.Object3DType;

public class LightTranslator extends AbstractTranslator
{
  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Light light = (m3x.xml.Light)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Matrix transform = getM3GTransformMatrix(light);
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    try
    {
      this.m3gObject = new m3x.m3g.objects.Light(animationTracks, 
          userParameters, 
          transform,
          light.isRenderingEnabled(),
          light.isPickingEnabled(),
          (byte)(light.getAlphaFactor() * 255.0f + 0.5f),
          light.getScope(),
          light.getAttenuationConstant().floatValue(),
          light.getAttenuationLinear().floatValue(),
          light.getAttenuationQuadratic().floatValue(),
          AbstractTranslator.translateColorRGB(light.getColor()),
          toM3G(light.getMode()),
          light.getIntensity().floatValue(),
          light.getSpotAngle().floatValue(),
          light.getSpotExponent().floatValue());
    }
    catch (FileFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
    return this.m3gObject;
  }

  private int toM3G(LightType mode)
  {
    if (mode.equals(LightType.AMBIENT))
    {
      return Light.MODE_AMBIENT;
    }
    if (mode.equals(LightType.DIRECTIONAL))
    {
      return Light.MODE_DIRECTIONAL;
    }
    if (mode.equals(LightType.OMNI))
    {
      return Light.MODE_OMNI;
    }
    if (mode.equals(LightType.SPOT))
    {
      return Light.MODE_SPOT;
    }    
    throw new IllegalArgumentException(mode.toString());
  }

  public Object3DType toXML()
  {
    return null;
  }
}
