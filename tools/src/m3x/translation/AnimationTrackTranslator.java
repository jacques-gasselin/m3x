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

public class AnimationTrackTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.AnimationTrack at = (m3x.xml.AnimationTrack)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    int keyframeSequence = searchObjectIndex(this.m3xRoot, at.getKeyframeSequenceInstance());
    int animationController = searchObjectIndex(this.m3xRoot, at.getAnimationControllerInstance());
    try
    {
      this.m3gObject = new m3x.m3g.objects.AnimationTrack(animationTracks, 
          userParameters, 
          new ObjectIndex(keyframeSequence),
          new ObjectIndex(animationController),
          (int)at.getUserID());
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
