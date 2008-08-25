package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.NodeType;
import m3x.xml.Object3DType;

public class VertexBufferTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.VertexBuffer vb = (m3x.xml.VertexBuffer)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    try
    {
      this.m3gObject = new m3x.m3g.objects.VertexBuffer(animationTracks, 
          userParameters, 
          
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
