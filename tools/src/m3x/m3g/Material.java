package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.DataOutputStream;
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
 */
public class Material extends Object3D implements M3GTypedObject
{
    private ColorRGB ambientColor;
    private ColorRGBA diffuseColor;
    private ColorRGB emissiveColor;
    private ColorRGB specularColor;
    private float shininess;
    private boolean vertexColorTrackingEnabled;

    public Material(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, ColorRGB ambientColor,
        ColorRGBA diffuseColor, ColorRGB emissiveColor, ColorRGB specularColor,
        float shininess, boolean vertexColorTrackingEnabled)
    {
        super(animationTracks, userParameters);
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.emissiveColor = emissiveColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
        this.vertexColorTrackingEnabled = vertexColorTrackingEnabled;
    }

    public Material()
    {
        super();
    }

    @Override
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.ambientColor = new ColorRGB();
        this.ambientColor.deserialize(deserialiser);
        this.diffuseColor = new ColorRGBA();
        this.diffuseColor.deserialize(deserialiser);
        this.emissiveColor = new ColorRGB();
        this.emissiveColor.deserialize(deserialiser);
        this.specularColor = new ColorRGB();
        this.specularColor.deserialize(deserialiser);
        this.shininess = deserialiser.readFloat();
        this.vertexColorTrackingEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        this.ambientColor.serialize(serialiser);
        this.diffuseColor.serialize(serialiser);
        this.emissiveColor.serialize(serialiser);
        this.specularColor.serialize(serialiser);
        serialiser.writeFloat(this.shininess);
        serialiser.writeBoolean(this.vertexColorTrackingEnabled);
    }

    public int getObjectType()
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
