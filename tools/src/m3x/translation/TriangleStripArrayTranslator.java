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

public class TriangleStripArrayTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.TriangleStripArray tsa = (m3x.xml.TriangleStripArray)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    int[] indices = new int[tsa.getIndices().size()];
    for (int i = 0; i < indices.length; i++)
    {
      indices[i] = tsa.getIndices().get(i);
    }
    int[] stripLengths = new int[tsa.getStripLengths().size()];
    for (int i = 0; i < stripLengths.length; i++)
    {
      stripLengths[i] = tsa.getStripLengths().get(i);
    }

    this.m3gObject = new m3x.m3g.objects.TriangleStripArray(animationTracks, 
          userParameters, 
          indices,
          stripLengths);
    return this.m3gObject;
  }

  public Object3DType toXML()
  {
    return null;
  }
}
