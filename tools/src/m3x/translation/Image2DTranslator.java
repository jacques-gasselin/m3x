package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.Image2D;
import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.ImageBaseColorType;
;

public class Image2DTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.Image2D image = (m3x.xml.Image2D) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        try
        {
            this.setBinaryObject(
                new m3x.m3g.Image2D(
                    animationTracks,
                    userParameters,
                    toM3G(image.getFormat()),
                    image.getWidth(),
                    image.getHeight(),
                    toBytes(image.getPalette()),
                    toBytes(image.getPixels())));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private int toM3G(ImageBaseColorType format)
    {
        if (format.equals(ImageBaseColorType.ALPHA))
        {
            return Image2D.FORMAT_ALPHA;
        }
        if (format.equals(ImageBaseColorType.LUMINANCE))
        {
            return Image2D.FORMAT_LUMINANCE;
        }
        if (format.equals(ImageBaseColorType.LUMINANCE_ALPHA))
        {
            return Image2D.FORMAT_LUMINANCE_ALPHA;
        }
        if (format.equals(ImageBaseColorType.RGB))
        {
            return Image2D.FORMAT_RGB;
        }
        if (format.equals(ImageBaseColorType.RGBA))
        {
            return Image2D.FORMAT_RGBA;
        }
        if (format.equals(ImageBaseColorType.RGB_565))
        {
            // TODO: what to return here?
            throw new IllegalArgumentException(format.toString());
        }
        if (format.equals(ImageBaseColorType.RGB_ETC))
        {
            // TODO: what to return here?
            throw new IllegalArgumentException(format.toString());
        }
        if (format.equals(ImageBaseColorType.RGBA_4444))
        {
            // TODO: what to return here?
            throw new IllegalArgumentException(format.toString());
        }
        if (format.equals(ImageBaseColorType.RGBA_5551))
        {
            // TODO: what to return here?
            throw new IllegalArgumentException(format.toString());
        }
        throw new IllegalArgumentException(format.toString());
    }

    private byte[] toBytes(List<Short> list)
    {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = list.get(i).byteValue();
        }
        return bytes;
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
