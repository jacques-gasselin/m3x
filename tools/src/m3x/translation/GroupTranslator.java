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

public class GroupTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Group group = (m3x.xml.Group)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    m3x.xml.TransformableType transformable = (m3x.xml.TransformableType)group;
    Matrix transform = getM3GTransformMatrix(transformable);
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    List<NodeType> childNodes = group.getChildNodes();
    List<ObjectIndex> childObjectIndices = new ArrayList<ObjectIndex>();
    for (NodeType node : childNodes)
    {
      Object toBeFound = node.getId();
      int index = searchObjectIndex(this.m3xRoot, toBeFound);
      childObjectIndices.add(new ObjectIndex(index));
    }
    ObjectIndex[] children = childObjectIndices.toArray(new ObjectIndex[childObjectIndices.size()]);
    
    try
    {
      this.m3gObject = new m3x.m3g.objects.Group(animationTracks, 
          userParameters, 
          transform,
          group.isRenderingEnabled(),
          group.isPickingEnabled(),
          (byte)(group.getAlphaFactor() * 255.0f + 0.5f),
          group.getScope(),
          children);
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
