package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#Sprite<br>
  ObjectIndex   image;<br>
  ObjectIndex   appearance;<br>
  <br>
  Boolean       isScaled;<br>
  <br>
  Int32         cropX;<br>
  Int32         cropY;<br>
  Int32         cropWidth;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Sprite extends Node implements SectionSerialisable
{
    private Image2D image;
    private Appearance appearance;
    private boolean isScaled;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;

    public Sprite()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.image = (Image2D)deserialiser.readReference();
        this.appearance = (Appearance)deserialiser.readReference();
        this.isScaled = deserialiser.readBoolean();
        this.cropX = deserialiser.readInt();
        this.cropY = deserialiser.readInt();
        this.cropWidth = deserialiser.readInt();
        this.cropHeight = deserialiser.readInt();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getImage());
        serialiser.writeReference(getAppearance());
        serialiser.writeBoolean(isScaled);
        serialiser.writeInt(this.cropX);
        serialiser.writeInt(this.cropY);
        serialiser.writeInt(this.cropWidth);
        serialiser.writeInt(this.cropHeight);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.SPRITE;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public Appearance getAppearance()
    {
        return this.appearance;
    }

    public boolean isScaled()
    {
        return this.isScaled;
    }

    public int getCropX()
    {
        return this.cropX;
    }

    public int getCropY()
    {
        return this.cropY;
    }

    public int getCropWidth()
    {
        return this.cropWidth;
    }

    public int getCropHeight()
    {
        return this.cropHeight;
    }
}
