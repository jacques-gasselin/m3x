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
    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;
    private int format;
    private boolean mutable;
    private int width;
    private int height;
    private byte[] paletteData;
    private byte[][] mipmapData;

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

    private static void validateFormat(int format)
    {
        if (format < ALPHA || format > RGBA)
        {
            throw new IllegalArgumentException("Invalid image format: " + format);
        }
    }

    private static void validateWidthAndHeight(int width, int height)
    {
        if (width <= 0)
        {
            throw new IllegalArgumentException("Invalid width: " + width);
        }
        if (height <= 0)
        {
            throw new IllegalArgumentException("Invalid height: " + height);
        }
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
        setFormat(deserialiser.readUnsignedByte());
        setMutable(deserialiser.readBoolean());
        setWidth(deserialiser.readInt());
        setHeight(deserialiser.readInt());
        validateWidthAndHeight(this.width, this.height);
        if (!isMutable())
        {
            byte[] palette = deserialiser.readByteArray();
            byte[] pixels = deserialiser.readByteArray();
            setPalette(palette);
            setPixels(pixels);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(getFormat());
        serialiser.writeBoolean(isMutable());
        serialiser.writeInt(getWidth());
        serialiser.writeInt(getHeight());
        if (!isMutable())
        {
            byte[] palette = getPalette();
            byte[] pixels = getPixels();
            serialiser.writeByteArray(palette);
            serialiser.writeByteArray(pixels);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.IMAGE_2D;
    }

    public void setFormat(String format)
    {
        if (format.equals("ALPHA"))
        {
            setFormat(ALPHA);
        }
        else if (format.equals("LUMINANCE"))
        {
            setFormat(LUMINANCE);
        }
        else if (format.equals("LUMINANCE_ALPHA"))
        {
            setFormat(LUMINANCE_ALPHA);
        }
        else if (format.equals("RGB"))
        {
            setFormat(RGB);
        }
        else if (format.equals("RGBA"))
        {
            setFormat(RGBA);
        }
        else
        {
            throw new IllegalArgumentException("unknown format " + format);
        }
    }

    public void setFormat(int format)
    {
        validateFormat(format);
        this.format = format;
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

    public void setSize(int width, int height)
    {
        validateWidthAndHeight(width, height);
        setWidth(width);
        setHeight(height);
    }

    private void setWidth(int width)
    {
        this.width = width;
    }

    private void setHeight(int height)
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
            this.paletteData = new byte[palette.length];
            System.arraycopy(palette, 0, this.paletteData, 0, palette.length);
        }
        else
        {
            this.paletteData = null;
        }
    }

    public byte[] getPalette()
    {
        return this.paletteData;
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
            this.mipmapData = new byte[1][];
            this.mipmapData[0] = new byte[pixels.length];
            System.arraycopy(pixels, 0, this.mipmapData[0], 0, pixels.length);
        }
        else
        {
            this.mipmapData = null;
        }
    }

    public byte[] getPixels()
    {
        if (this.mipmapData == null)
        {
            return null;
        }
        return this.mipmapData[0];
    }

    public byte[][] getMipmapPixels()
    {
        return this.mipmapData;
    }
}
