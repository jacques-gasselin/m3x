package m3x.translation;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Object3DType;

public class WorldTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }
    // do translation
    m3x.xml.World world = (m3x.xml.World)this.m3xObject;
    ObjectIndex[] animationTracks = new m3x.m3g.primitives.ObjectIndex[1];
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    //this.m3gObject = new m3x.m3g.objects.World(animationTracks, userParameters, transform, enableRendering, enablePicking, alphaFactor, scope, children, activeCamera, background);
    return this.m3gObject;
  }

  public Object3DType toXML()
  {
    return null;
  }
}
