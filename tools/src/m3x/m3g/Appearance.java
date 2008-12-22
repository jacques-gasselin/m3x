package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class Appearance extends Object3D
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
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.layer = deserialiser.readByte();
        this.compositingMode = (CompositingMode)deserialiser.readReference();
        this.fog = (Fog)deserialiser.readReference();
        this.polygonMode = (PolygonMode)deserialiser.readReference();
        this.material = (Material)deserialiser.readReference();
        int texturesLength = deserialiser.readInt();
        this.textures = new Texture2D[texturesLength];
        for (int i = 0; i < this.textures.length; i++)
        {
            this.textures[i] = (Texture2D)deserialiser.readReference();
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeByte(this.layer);
        serialiser.writeReference(getCompositingMode());
        serialiser.writeReference(getFog());
        serialiser.writeReference(getPolygonMode());
        serialiser.writeReference(getMaterial());
        serialiser.writeInt(getTextureCount());
        for (int i = 0; i < getTextureCount(); ++i)
        {
            serialiser.writeReference(getTexture(i));
        }
    }

    public int getSectionObjectType()
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

    public int getTextureCount()
    {
        return this.textures.length;
    }

    public Texture2D getTexture(int index)
    {
        return this.textures[index];
    }
}
