package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#Material<br>
  ColorRGB      ambientColor;<br>
  ColorRGBA     diffuseColor;<br>
  ColorRGB      emissiveColor;<br>
  ColorRGB      specularColor;<br>
  Float32       shininess;<br>
  Boolean       vertexColorTrackingEnabled;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Material extends Object3D
{
    private ColorRGB ambientColor;
    private ColorRGBA diffuseColor;
    private ColorRGB emissiveColor;
    private ColorRGB specularColor;
    private float shininess;
    private boolean vertexColorTrackingEnabled;

    public Material()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.ambientColor = new ColorRGB();
        this.ambientColor.deserialise(deserialiser);
        this.diffuseColor = new ColorRGBA();
        this.diffuseColor.deserialise(deserialiser);
        this.emissiveColor = new ColorRGB();
        this.emissiveColor.deserialise(deserialiser);
        this.specularColor = new ColorRGB();
        this.specularColor.deserialise(deserialiser);
        this.shininess = deserialiser.readFloat();
        this.vertexColorTrackingEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.ambientColor.serialise(serialiser);
        this.diffuseColor.serialise(serialiser);
        this.emissiveColor.serialise(serialiser);
        this.specularColor.serialise(serialiser);
        serialiser.writeFloat(this.shininess);
        serialiser.writeBoolean(this.vertexColorTrackingEnabled);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.MATERIAL;
    }

    public ColorRGB getAmbientColor()
    {
        return this.ambientColor;
    }

    public ColorRGBA getDiffuseColor()
    {
        return this.diffuseColor;
    }

    public ColorRGB getEmissiveColor()
    {
        return this.emissiveColor;
    }

    public ColorRGB getSpecularColor()
    {
        return this.specularColor;
    }

    public float getShininess()
    {
        return this.shininess;
    }

    public boolean isVertexColorTrackingEnabled()
    {
        return this.vertexColorTrackingEnabled;
    }
}
