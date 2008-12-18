package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * See http://java2me.org/m3g/file-format.html#Appearance<br>
  Byte          layer;<br>
  ObjectIndex   compositingMode;<br>
  ObjectIndex   fog;<br>
  ObjectIndex   polygonMode;<br>
  ObjectIndex   material;<br>
  ObjectIndex[] textures;<br>

 * @author jsaarinen
 */
public class Appearance extends Object3D implements M3GTypedObject
{
    private int layer;
    private CompositingMode compositingMode;
    private Fog fog;
    private PolygonMode polygonMode;
    private Material material;
    private Texture2D[] textures;

    public Appearance(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, int layer, CompositingMode compositingMode,
        Fog fog, PolygonMode polygonMode, Material material,
        Texture2D[] textures)
    {
        super(animationTracks, userParameters);
        this.layer = layer;
        this.compositingMode = compositingMode;
        this.fog = fog;
        this.polygonMode = polygonMode;
        this.material = material;
        this.textures = textures;
    }

    public Appearance()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.layer = deserialiser.readByte();
        this.compositingMode = (CompositingMode)deserialiser.readObjectReference();
        this.fog = (Fog)deserialiser.readObjectReference();
        this.polygonMode = (PolygonMode)deserialiser.readObjectReference();
        this.material = (Material)deserialiser.readObjectReference();
        int texturesLength = deserialiser.readInt();
        this.textures = new Texture2D[texturesLength];
        for (int i = 0; i < this.textures.length; i++)
        {
            this.textures[i] = (Texture2D)deserialiser.readObjectReference();
        }
    }

    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.writeByte(this.layer);
        this.compositingMode.serialize(dataOutputStream, m3gVersion);
        this.fog.serialize(dataOutputStream, m3gVersion);
        this.polygonMode.serialize(dataOutputStream, m3gVersion);
        this.material.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.textures.length);
        for (int i = 0; i < this.textures.length; i++) {
            this.textures[i].serialize(dataOutputStream, m3gVersion);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.APPEARANCE;
    }

    public int getLayer()
    {
        return this.layer;
    }

    public CompositingMode getCompositingMode()
    {
        return this.compositingMode;
    }

    public Fog getFog()
    {
        return this.fog;
    }

    public PolygonMode getPolygonMode()
    {
        return this.polygonMode;
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public Texture2D[] getTextures()
    {
        return this.textures;
    }
}
