package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class Image2D extends Object3D implements M3GTypedObject
{
    public static final int FORMAT_ALPHA = 96;
    public static final int FORMAT_LUMINANCE = 97;
    public static final int FORMAT_LUMINANCE_ALPHA = 98;
    public static final int FORMAT_RGB = 99;
    public static final int FORMAT_RGBA = 100;

    private final int format;
    private final boolean isMutable;
    private final int width;
    private final int height;
    private final byte[] palette;
    private final byte[] pixels;

    public Image2D(ObjectIndex[] animationTracks,
            UserParameter[] userParameters, int format, int width, int height,
            byte[] palette, byte[] pixels) 
    {
        super(animationTracks, userParameters);
        this.format = format;
        this.isMutable = false;
        this.width = width;
        this.height = height;
        this.palette = palette;
        this.pixels = pixels;
    }

    public Image2D(ObjectIndex[] animationTracks,
            UserParameter[] userParameters, byte format, int width, int height) 
    {
        super(animationTracks, userParameters);
        this.format = format;
        this.isMutable = true;
        this.width = width;
        this.height = height;
        this.palette = null;
        this.pixels = null;
    }

    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException 
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.format);
        dataOutputStream.writeBoolean(this.isMutable);
        dataOutputStream.writeInt(M3GSupport.swapBytes(this.width));
        dataOutputStream.writeInt(M3GSupport.swapBytes(this.height));
        if (this.isMutable == false)
        {
            dataOutputStream.write(this.palette);
            dataOutputStream.write(this.pixels);
        }
    }

    @Override
    public byte getObjectType()
    {
      return ObjectTypes.IMAGE_2D;
    }
}
