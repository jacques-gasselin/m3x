package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * See See http://java2me.org/m3g/file-format.html#Image2D<br>
  Byte          format;<br>
  Boolean       isMutable;<br>
  UInt32        width;<br>
  UInt32        height;<br>
  IF isMutable==false, THEN<br>
      Byte[]        palette;<br>
      Byte[]        pixels;<br>
  END<br>
      
 * @author jsaarinen
 */
public class Image2D extends Object3D implements M3GTypedObject
{
    public static final int FORMAT_ALPHA = 96;
    public static final int FORMAT_LUMINANCE = 97;
    public static final int FORMAT_LUMINANCE_ALPHA = 98;
    public static final int FORMAT_RGB = 99;
    public static final int FORMAT_RGBA = 100;
    private int format;
    private boolean isMutable;
    private int width;
    private int height;
    private byte[] palette;
    private byte[] pixels;

    public Image2D(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height, byte[] palette, byte[] pixels)
        throws FileFormatException
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.isMutable = false;
        validateWidthAndHeight(width, height);
        this.palette = palette;
        this.pixels = pixels;
    }

    public Image2D(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height) throws FileFormatException
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.isMutable = true;
        validateWidthAndHeight(width, height);
        this.palette = null;
        this.pixels = null;
    }

    private void validateFormat(int format) throws FileFormatException
    {
        if (format < FORMAT_ALPHA || format > FORMAT_RGBA)
        {
            throw new FileFormatException("Invalid image format: " + format);
        }
        this.format = format;
    }

    private void validateWidthAndHeight(int width, int height)
        throws FileFormatException
    {
        if (width <= 0)
        {
            throw new FileFormatException("Invalid width: " + width);
        }
        this.width = width;
        if (height <= 0)
        {
            throw new FileFormatException("Invalid height: " + height);
        }
        this.height = height;
    }

    public Image2D()
    {
        super();
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.format = deserialiser.readUnsignedByte();
        if (this.format != FORMAT_ALPHA &&
            this.format != FORMAT_LUMINANCE &&
            this.format != FORMAT_LUMINANCE_ALPHA &&
            this.format != FORMAT_RGB &&
            this.format != FORMAT_RGBA)
        {
            throw new FileFormatException("Invalid Image2D format: " + this.format);
        }
        this.isMutable = deserialiser.readBoolean();
        this.width = deserialiser.readInt();
        if (this.width <= 0)
        {
            throw new FileFormatException("Invalid Image2D width: " + this.width);
        }
        this.height = deserialiser.readInt();
        if (this.height <= 0)
        {
            throw new FileFormatException("Invalid Image2D height: " + this.height);
        }
        if (this.isMutable == false)
        {
            int paletteLength = deserialiser.readInt();
            this.palette = new byte[paletteLength];
            deserialiser.readFully(this.palette);
            int pixelsLength = deserialiser.readInt();
            this.pixels = new byte[pixelsLength];
            deserialiser.readFully(this.pixels);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.format);
        dataOutputStream.writeBoolean(this.isMutable);
        M3GSupport.writeInt(dataOutputStream, this.width);
        M3GSupport.writeInt(dataOutputStream, this.height);
        if (this.isMutable == false)
        {
            M3GSupport.writeInt(dataOutputStream, this.palette.length);
            dataOutputStream.write(this.palette);
            M3GSupport.writeInt(dataOutputStream, this.pixels.length);
            dataOutputStream.write(this.pixels);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.IMAGE_2D;
    }

    public int getFormat()
    {
        return this.format;
    }

    public boolean isMutable()
    {
        return this.isMutable;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public byte[] getPalette()
    {
        return this.palette;
    }

    public byte[] getPixels()
    {
        return this.pixels;
    }
}
