package m3x.translation;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;
import m3x.xml.NodeType;
import m3x.xml.Object3DType;

public class Texture2DTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Texture2D texture = (m3x.xml.Texture2D)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    int textureIndex = this.searchObjectIndex(this.m3xRoot, texture.getImage2D().getId());
    
    float x, y, z;
    x = texture.getTranslation().get(0).floatValue();
    y = texture.getTranslation().get(1).floatValue();
    z = texture.getTranslation().get(2).floatValue();    
    Vector3D translation = new Vector3D(x, y, z);
    
    x = texture.getScale().get(0).floatValue();
    y = texture.getScale().get(1).floatValue();
    z = texture.getScale().get(2).floatValue();    
    Vector3D scale = new Vector3D(x, y, z);
    
    float orientationAngle = texture.getOrientation().getAngle().floatValue();
    
    x = texture.getOrientation().getValue().get(0).floatValue();
    y = texture.getOrientation().getValue().get(1).floatValue();
    z = texture.getOrientation().getValue().get(2).floatValue();    
    Vector3D orientationAxis = new Vector3D(x, y, z);
    
    byte r = texture.getBlendColor().get(0).byteValue();
    byte g = texture.getBlendColor().get(1).byteValue();
    byte b = texture.getBlendColor().get(2).byteValue();
    ColorRGB blendColor = new ColorRGB(r, g, b);
    
    this.m3gObject = new m3x.m3g.objects.Texture2D(animationTracks, 
          userParameters,
          translation,
          scale,
          orientationAngle,
          orientationAxis,
          new ObjectIndex(textureIndex),
          blendColor,
          texture.getBlending().ordinal(),
          texture.getWrappingS().ordinal(),
          texture.getWrappingT().ordinal(),
          texture.getLevelFilter().ordinal(),
          texture.getImageFilter().ordinal());
    return this.m3gObject;
  }

  public Object3DType toXML()
  {
    return null;
  }
}
