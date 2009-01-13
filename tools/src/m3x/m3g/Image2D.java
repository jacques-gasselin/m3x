package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;
import java.util.List;


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
    private boolean mutable;
    private int width;
    private int height;
    private byte[] palette;
    private byte[][] pixels;

    public Image2D(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height, byte[] palette, byte[] pixels)
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.mutable = false;
        validateWidthAndHeight(width, height);
        setPalette(palette);
        setPixels(pixels);
    }

    public Image2D(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height)
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.mutable = true;
        validateWidthAndHeight(width, height);
        setPalette(null);
        setPixels((byte[])null);
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
        this.mutable = deserialiser.readBoolean();
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
        if (this.mutable == false)
        {
            int paletteLength = deserialiser.readInt();
            this.palette = new byte[paletteLength];
            deserialiser.readFully(this.palette);
            int pixelsLength = deserialiser.readInt();
            this.pixels = new byte[1][];
            this.pixels[0] = new byte[pixelsLength];
            deserialiser.readFully(this.pixels[0]);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.format);
        serialiser.writeBoolean(isMutable());
        serialiser.writeInt(this.width);
        serialiser.writeInt(this.height);
        if (!isMutable())
        {
            serialiser.writeInt(this.palette.length);
            serialiser.write(this.palette);
            serialiser.writeInt(this.pixels.length);
            serialiser.write(this.pixels[0]);
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

    public void setMutable(boolean enable)
    {
        this.mutable = enable;
    }

    public boolean isMutable()
    {
        return this.mutable;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setPalette(byte[] palette)
    {
        if (palette != null)
        {
            this.palette = new byte[palette.length];
            System.arraycopy(palette, 0, this.palette, 0, palette.length);
        }
        else
        {
            this.palette = null;
        }
    }

    public byte[] getPalette()
    {
        return this.palette;
    }

    public void setPixels(List<Short> pixels)
    {
        byte[] signedPixels = null;
        if ((pixels != null) && (pixels.size() > 0))
        {
            signedPixels = new byte[pixels.size()];
            for (int i = 0; i < pixels.size(); ++i)
            {
                short unsignedPixel = pixels.get(i);
                if (unsignedPixel > 127)
                {
                    unsignedPixel -= 256;
                }
                signedPixels[i] = (byte)unsignedPixel;
            }
        }
        setPixels(signedPixels);
    }

    public void setPixels(byte[] pixels)
    {
        if (pixels != null)
        {
            //single level only
            this.pixels = new byte[1][];
            this.pixels[0] = new byte[pixels.length];
            System.arraycopy(pixels, 0, this.pixels[0], 0, pixels.length);
        }
        else
        {
            this.pixels = null;
        }
    }

    public byte[] getPixels()
    {
        if (this.pixels == null)
        {
            return null;
        }
        return this.pixels[0];
    }

    public byte[][] getMipmapPixels()
    {
        return this.pixels;
    }
}
