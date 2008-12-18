package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#Background<br>
  ColorRGBA     backgroundColor;<br>
  ObjectIndex   backgroundImage;<br>
  Byte          backgroundImageModeX;<br>
  Byte          backgroundImageModeY;<br>
  Int32         cropX;<br>
  Int32         cropY;<br>
  Int32         cropWidth;<br>
  Int32         cropHeight;<br>
  Boolean       depthClearEnabled;<br>
  Boolean       colorClearEnabled;<br>

 * @author jsaarinen
 */
public class Background extends Object3D implements M3GTypedObject
{
    public final static int MODE_BORDER = 32;
    public final static int MODE_REPEAT = 33;
    
    private ColorRGBA backgroundColor;
    private Image2D backgroundImage;
    private int backgroundImageModeX;
    private int backgroundImageModeY;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;
    private boolean depthClearEnabled;
    private boolean colorClearEnabled;

    public Background(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, ColorRGBA backgroundColor,
        Image2D backgroundImage, int backgroundImageModeX,
        int backgroundImageModeY, int cropX, int cropY, int cropWidth,
        int cropHeight, boolean depthClearEnabled, boolean colorClearEnabled)
        throws FileFormatException
    {
        super(animationTracks, userParameters);
        if (!(backgroundImageModeX == MODE_BORDER || backgroundImageModeX == MODE_REPEAT))
        {
            throw new FileFormatException("Invalid backgroudImageModeX: " + backgroundImageModeX);
        }
        if (!(backgroundImageModeY == MODE_BORDER || backgroundImageModeY == MODE_REPEAT))
        {
            throw new FileFormatException("Invalid backgroudImageModeY: " + backgroundImageModeY);
        }
        this.backgroundColor = backgroundColor;
        this.backgroundImage = backgroundImage;
        this.backgroundImageModeX = backgroundImageModeX;
        this.backgroundImageModeY = backgroundImageModeY;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.depthClearEnabled = depthClearEnabled;
        this.colorClearEnabled = colorClearEnabled;
    }

    public Background()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.backgroundColor = new ColorRGBA();
        this.backgroundColor.deserialize(deserialiser);
        this.backgroundImage = (Image2D)deserialiser.readObjectReference();
        this.backgroundImageModeX = deserialiser.readUnsignedByte();
        this.backgroundImageModeY = deserialiser.readUnsignedByte();
        this.cropX = deserialiser.readInt();
        this.cropY = deserialiser.readInt();
        this.cropWidth = deserialiser.readInt();
        this.cropHeight = deserialiser.readInt();
        this.depthClearEnabled = deserialiser.readBoolean();
        this.colorClearEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.backgroundColor.serialize(dataOutputStream, m3gVersion);
        this.backgroundImage.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.backgroundImageModeX);
        dataOutputStream.write(this.backgroundImageModeY);
        M3GSupport.writeInt(dataOutputStream, this.cropX);
        M3GSupport.writeInt(dataOutputStream, this.cropY);
        M3GSupport.writeInt(dataOutputStream, this.cropWidth);
        M3GSupport.writeInt(dataOutputStream, this.cropHeight);
        dataOutputStream.writeBoolean(this.depthClearEnabled);
        dataOutputStream.writeBoolean(this.colorClearEnabled);
    }

    public int getObjectType()
    {
        return ObjectTypes.BACKGROUND;
    }

    public ColorRGBA getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public Image2D getBackgroundImage()
    {
        return this.backgroundImage;
    }

    public int getBackgroundImageModeX()
    {
        return this.backgroundImageModeX;
    }

    public int getBackgroundImageModeY()
    {
        return this.backgroundImageModeY;
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

    public boolean isDepthClearEnabled()
    {
        return this.depthClearEnabled;
    }

    public boolean isColorClearEnabled()
    {
        return this.colorClearEnabled;
    }
}
