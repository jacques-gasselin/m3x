package m3x.translation;


import m3x.m3g.Object3D;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;
;

public class MaterialTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Material material = (m3x.xml.Material) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        ColorRGB ambient = translateColorRGB(material.getAmbientColor());
        ColorRGBA diffuse = translateColorRGBA(material.getDiffuseColor());
        ColorRGB emissive = translateColorRGB(material.getEmissiveColor());
        ColorRGB specular = translateColorRGB(material.getSpecularColor());

        this.setBinaryObject(new m3x.m3g.Material(animationTracks, userParameters, ambient, diffuse, emissive, specular, material.getShininess().floatValue(), material.isVertexColorTrackingEnabled().booleanValue()));

        return this.getBinaryObject();
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
