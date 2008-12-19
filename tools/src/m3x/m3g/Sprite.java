package m3x.m3g;

import m3x.m3g.primitives.TypedObject;
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
 */
public class Sprite extends Node implements TypedObject
{
    private Image2D image;
    private Appearance appearance;
    private boolean isScaled;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;

    public Sprite(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Image2D image, Appearance appearance,
        boolean isScaled, int cropX, int cropY, int cropWidth, int cropHeight)
    {
        super(animationTracks, userParameters, transform, enableRendering,
              enablePicking, alphaFactor, scope);
        this.image = image;
        this.appearance = appearance;
        this.isScaled = isScaled;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
    }

    public Sprite()
    {
        super();
    }

    @Override
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.image = (Image2D)deserialiser.readReference();
        this.appearance = (Appearance)deserialiser.readReference();
        this.isScaled = deserialiser.readBoolean();
        this.cropX = deserialiser.readInt();
        this.cropY = deserialiser.readInt();
        this.cropWidth = deserialiser.readInt();
        this.cropHeight = deserialiser.readInt();
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeReference(getImage());
        serialiser.writeReference(getAppearance());
        serialiser.writeBoolean(isScaled);
        serialiser.writeInt(this.cropX);
        serialiser.writeInt(this.cropY);
        serialiser.writeInt(this.cropWidth);
        serialiser.writeInt(this.cropHeight);
    }

    public int getObjectType()
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
