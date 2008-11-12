package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.util.LittleEndianDataInputStream;

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

    public Material(ObjectIndex[] animationTracks,
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

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.ambientColor = new ColorRGB();
        this.ambientColor.deserialize(dataInputStream, m3gVersion);
        this.diffuseColor = new ColorRGBA();
        this.diffuseColor.deserialize(dataInputStream, m3gVersion);
        this.emissiveColor = new ColorRGB();
        this.emissiveColor.deserialize(dataInputStream, m3gVersion);
        this.specularColor = new ColorRGB();
        this.specularColor.deserialize(dataInputStream, m3gVersion);
        this.shininess = dataInputStream.readFloat();
        this.vertexColorTrackingEnabled = dataInputStream.readBoolean();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.ambientColor.serialize(dataOutputStream, m3gVersion);
        this.diffuseColor.serialize(dataOutputStream, m3gVersion);
        this.emissiveColor.serialize(dataOutputStream, m3gVersion);
        this.specularColor.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeFloat(dataOutputStream, this.shininess);
        dataOutputStream.writeBoolean(this.vertexColorTrackingEnabled);
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