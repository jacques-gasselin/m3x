package m3x.m3g;

import m3x.m3g.Object3D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

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
    private ObjectIndex backgroundImage;
    private int backgroundImageModeX;
    private int backgroundImageModeY;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;
    private boolean depthClearEnabled;
    private boolean colorClearEnabled;

    public Background(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, ColorRGBA backgroundColor,
        ObjectIndex backgroundImage, int backgroundImageModeX,
        int backgroundImageModeY, int cropX, int cropY, int cropWidth,
        int cropHeight, boolean depthClearEnabled, boolean colorClearEnabled) throws FileFormatException
    {
        super(animationTracks, userParameters);
        if (!(backgroundImageModeX == MODE_BORDER || backgroundImageModeX == MODE_REPEAT)) {
            throw new FileFormatException("Invalid backgroudImageModeX: " + backgroundImageModeX);
        }
        if (!(backgroundImageModeY == MODE_BORDER || backgroundImageModeY == MODE_REPEAT)) {
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

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.backgroundColor = new ColorRGBA();
        this.backgroundColor.deserialize(dataInputStream, m3gVersion);
        this.backgroundImage = new ObjectIndex();
        this.backgroundImage.deserialize(dataInputStream, m3gVersion);
        this.backgroundImageModeX = dataInputStream.readByte() & 0xFF;
        this.backgroundImageModeY = dataInputStream.readByte() & 0xFF;
        this.cropX = M3GSupport.readInt(dataInputStream);
        this.cropY = M3GSupport.readInt(dataInputStream);
        this.cropWidth = M3GSupport.readInt(dataInputStream);
        this.cropHeight = M3GSupport.readInt(dataInputStream);
        this.depthClearEnabled = dataInputStream.readBoolean();
        this.colorClearEnabled = dataInputStream.readBoolean();
    }

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

    public ObjectIndex getBackgroundImage()
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
