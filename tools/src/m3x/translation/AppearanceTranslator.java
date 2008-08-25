package m3x.translation;


import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Object3DType;
import m3x.xml.Texture2DInstance;

public class AppearanceTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Appearance appearance = (m3x.xml.Appearance)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
   
    int compositingModeIndex = searchObjectIndex(this.m3xRoot, appearance.getCompositingModeInstance().getRef());
    int fogIndex = searchObjectIndex(this.m3xRoot, appearance.getFogInstance().getRef());
    int polygonModeIndex = searchObjectIndex(this.m3xRoot, appearance.getPolygonModeInstance().getRef());
    int materialIndex = searchObjectIndex(this.m3xRoot, appearance.getMaterialInstance().getRef());
    List<Texture2DInstance> list = appearance.getTexture2DInstance();
    ObjectIndex[] textureIndices = new ObjectIndex[list.size()];
    for (int i = 0; i < list.size(); i++)
    {
      Texture2DInstance texture = list.get(i);
      textureIndices[i] = new ObjectIndex(searchObjectIndex(this.m3xRoot, texture.getRef()));
    }

    this.m3gObject = new m3x.m3g.objects.Appearance(animationTracks, 
          userParameters, 
          appearance.getLayer().byteValue(),
          new ObjectIndex(compositingModeIndex),
          new ObjectIndex(fogIndex),
          new ObjectIndex(polygonModeIndex),
          new ObjectIndex(materialIndex),
          textureIndices);

    return this.m3gObject;
  }

  public Object3DType toXML()
  {
    return null;
  }
}
