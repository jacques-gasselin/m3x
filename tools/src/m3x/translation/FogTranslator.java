package m3x.translation;


import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.FogEquationType;
import m3x.xml.Object3DType;

public class FogTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Fog fog = (m3x.xml.Fog)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
  
    if (fog.getMode().equals(FogEquationType.EXPONENTIAL))
    {
      this.m3gObject = new m3x.m3g.objects.Fog(animationTracks, 
          userParameters, 
          AbstractTranslator.translateColorRGB(fog.getColor()),
          fog.getDensity().floatValue());
    }
    else
    if (fog.getMode().equals(FogEquationType.LINEAR))
    {
      this.m3gObject = new m3x.m3g.objects.Fog(animationTracks, 
          userParameters, 
          AbstractTranslator.translateColorRGB(fog.getColor()),
          fog.getNear().floatValue(),
          fog.getFar().floatValue());
    }
    else
    if (fog.getMode().equals(FogEquationType.EXPONENTIAL_SQUARED))
    {
      throw new IllegalArgumentException("exp^2 fog is not supported by M3G 1.0.");
    }  
    return this.m3gObject;
  }
  
  public Object3DType toXML()
  {
    return null;
  }
}
