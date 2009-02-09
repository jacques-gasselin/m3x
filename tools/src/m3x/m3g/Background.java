package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class Background extends Object3D
{
    public final static int BORDER = 32;
    public final static int REPEAT = 33;
    
    private ColorRGBA color;
    private Image2D image;
    private int imageModeX;
    private int imageModeY;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;
    private boolean colorClearEnabled;
    private boolean depthClearEnabled;

    public Background()
    {
        super();
        color = new ColorRGBA();
        setColor(0x0);
        setColorClearEnable(true);
        setDepthClearEnable(true);
        setImage(null);
        setImageMode(BORDER, BORDER);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.color.deserialise(deserialiser);
        setImage((Image2D)deserialiser.readReference());
        final int modeX = deserialiser.readUnsignedByte();
        final int modeY = deserialiser.readUnsignedByte();
        setImageMode(modeX, modeY);
        final int x = deserialiser.readInt();
        final int y = deserialiser.readInt();
        final int width = deserialiser.readInt();
        final int height = deserialiser.readInt();
        setCrop(x, y, width, height);
        setDepthClearEnable(deserialiser.readBoolean());
        setColorClearEnable(deserialiser.readBoolean());
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.color.serialise(serialiser);
        serialiser.writeReference(getImage());
        serialiser.write(getImageModeX());
        serialiser.write(getImageModeY());
        serialiser.writeInt(getCropX());
        serialiser.writeInt(getCropY());
        serialiser.writeInt(getCropWidth());
        serialiser.writeInt(getCropHeight());
        serialiser.writeBoolean(isDepthClearEnabled());
        serialiser.writeBoolean(isColorClearEnabled());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.BACKGROUND;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public int getImageModeX()
    {
        return this.imageModeX;
    }

    public int getImageModeY()
    {
        return this.imageModeY;
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

    public void setColor(int argb)
    {
        this.color.set(argb);
    }

    public void setColorClearEnable(boolean enable)
    {
        this.colorClearEnabled = enable;
    }

    public void setDepthClearEnable(boolean enable)
    {
        this.depthClearEnabled = enable;
    }

    public void setImage(Image2D image)
    {
        this.image = image;
    }

    public void setImageMode(int modeX, int modeY)
    {
        this.imageModeX = modeX;
        this.imageModeY = modeY;
    }

    public void setCrop(int x, int y, int width, int height)
    {
        this.cropX = x;
        this.cropY = y;
        this.cropWidth = width;
        this.cropHeight = height;
    }
}
