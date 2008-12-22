package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class Image2D extends Object3D
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
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.isMutable = false;
        validateWidthAndHeight(width, height);
        this.palette = palette;
        this.pixels = pixels;
    }

    public Image2D(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height)
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.isMutable = true;
        validateWidthAndHeight(width, height);
        this.palette = null;
        this.pixels = null;
    }

    private void validateFormat(int format)
    {
        if (format < FORMAT_ALPHA || format > FORMAT_RGBA)
        {
            throw new IllegalArgumentException("Invalid image format: " + format);
        }
        this.format = format;
    }

    private void validateWidthAndHeight(int width, int height)
    {
        if (width <= 0)
        {
            throw new IllegalArgumentException("Invalid width: " + width);
        }
        this.width = width;
        if (height <= 0)
        {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
        this.height = height;
    }

    public Image2D()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.format = deserialiser.readUnsignedByte();
        if (this.format != FORMAT_ALPHA &&
            this.format != FORMAT_LUMINANCE &&
            this.format != FORMAT_LUMINANCE_ALPHA &&
            this.format != FORMAT_RGB &&
            this.format != FORMAT_RGBA)
        {
            throw new IllegalArgumentException("Invalid Image2D format: " + this.format);
        }
        this.isMutable = deserialiser.readBoolean();
        this.width = deserialiser.readInt();
        if (this.width <= 0)
        {
            throw new IllegalArgumentException("Invalid Image2D width: " + this.width);
        }
        this.height = deserialiser.readInt();
        if (this.height <= 0)
        {
            throw new IllegalArgumentException("Invalid Image2D height: " + this.height);
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

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.format);
        serialiser.writeBoolean(this.isMutable);
        serialiser.writeInt(this.width);
        serialiser.writeInt(this.height);
        if (this.isMutable == false)
        {
            serialiser.writeInt(this.palette.length);
            serialiser.write(this.palette);
            serialiser.writeInt(this.pixels.length);
            serialiser.write(this.pixels);
        }
    }

    public int getSectionObjectType()
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
