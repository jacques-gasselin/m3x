package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;
import m3x.m3g.util.Object3DReferences;

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
        serialiser.writeByte(getLayer());
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

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getCompositingMode());
        queue.add(getFog());
        queue.add(getPolygonMode());
        queue.add(getMaterial());
        for (int i = 0; i < getTextureCount(); ++i)
        {
            queue.add(getTexture(i));
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
        if (this.textures == null)
        {
            return 0;
        }
        return this.textures.length;
    }

    private final void requireTextureInRange(int index)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index < 0");
        }
        if (index >= getTextureCount())
        {
            throw new IllegalArgumentException("index > getTextureCount()");
        }
    }

    public Texture2D getTexture(int index)
    {
        requireTextureInRange(index);
        
        return this.textures[index];
    }

    public void setCompositingMode(CompositingMode compositingMode)
    {
        this.compositingMode = compositingMode;
    }

    public void setFog(Fog fog)
    {
        this.fog = fog;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public void setPolygonMode(PolygonMode polygonMode)
    {
        this.polygonMode = polygonMode;
    }

    public void setTextureCount(int count)
    {
        if (count == 0)
        {
            this.textures = null;
        }
        else
        {
            this.textures = new Texture2D[count];
        }
    }

    public void setTexture(int index, Texture2D texture)
    {
        requireTextureInRange(index);

        this.textures[index] = texture;
    }

}
