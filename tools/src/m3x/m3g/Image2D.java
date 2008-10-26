package m3x.m3g;

import m3x.m3g.Object3D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.util.LittleEndianDataInputStream;

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

    public Image2D(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        int format, int width, int height, byte[] palette, byte[] pixels) throws FileFormatException
    {
        super(animationTracks, userParameters);
        validateFormat(format);
        this.isMutable = false;
        validateWidthAndHeight(width, height);
        this.palette = palette;
        this.pixels = pixels;
    }

    public Image2D(ObjectIndex[] animationTracks, UserParameter[] userParameters,
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

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.format = dataInputStream.readByte() & 0xFF;
        if (this.format != FORMAT_ALPHA &&
            this.format != FORMAT_LUMINANCE &&
            this.format != FORMAT_LUMINANCE_ALPHA &&
            this.format != FORMAT_RGB &&
            this.format != FORMAT_RGBA)
        {
            throw new FileFormatException("Invalid Image2D format: " + this.format);
        }
        this.isMutable = dataInputStream.readBoolean();
        this.width = dataInputStream.readInt();
        if (this.width <= 0)
        {
            throw new FileFormatException("Invalid Image2D width: " + this.width);
        }
        this.height = dataInputStream.readInt();
        if (this.height <= 0)
        {
            throw new FileFormatException("Invalid Image2D height: " + this.height);
        }
        if (this.isMutable == false)
        {
            int paletteLength = dataInputStream.readInt();
            this.palette = new byte[paletteLength];
            dataInputStream.readFully(this.palette);
            int pixelsLength = dataInputStream.readInt();
            this.pixels = new byte[pixelsLength];
            dataInputStream.readFully(this.pixels);
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
