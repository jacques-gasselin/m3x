package m3x.translation;


import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Texture2DInstance;

public class AppearanceTranslator extends AbstractTranslator
{
    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Appearance appearance = (m3x.xml.Appearance) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        int compositingModeIndex = searchObjectIndex(this.getXmlRootObject(), appearance.getCompositingModeInstance().getRef());
        int fogIndex = searchObjectIndex(this.getXmlRootObject(), appearance.getFogInstance().getRef());
        int polygonModeIndex = searchObjectIndex(this.getXmlRootObject(), appearance.getPolygonModeInstance().getRef());
        int materialIndex = searchObjectIndex(this.getXmlRootObject(), appearance.getMaterialInstance().getRef());
        List<Texture2DInstance> list = appearance.getTexture2DInstance();
        ObjectIndex[] textureIndices = new ObjectIndex[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            Texture2DInstance texture = list.get(i);
            textureIndices[i] = new ObjectIndex(searchObjectIndex(this.getXmlRootObject(), texture.getRef()));
        }

        this.setBinaryObject(new m3x.m3g.objects.Appearance(animationTracks, userParameters, appearance.getLayer().byteValue(), new ObjectIndex(compositingModeIndex), new ObjectIndex(fogIndex), new ObjectIndex(polygonModeIndex), new ObjectIndex(materialIndex), textureIndices));

        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
