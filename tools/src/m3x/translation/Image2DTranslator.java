package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.Image2D;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.ImageBaseColorType;
import m3x.xml.Object3DType;

public class Image2DTranslator extends AbstractTranslator
{

  public Object3D toM3G()
  {
    if (this.m3gObject != null)
    {
      return this.m3gObject;
    }

    // do translation
    m3x.xml.Image2D image = (m3x.xml.Image2D)this.m3xObject;
    ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
    Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
    
    try
    {
      this.m3gObject = new m3x.m3g.objects.Image2D(animationTracks, 
          userParameters, 
          toM3G(image.getFormat()),
          image.getWidth().intValue(),
          image.getHeight().intValue(),
          toBytes(image.getPalette()),
          toBytes(image.getPixels()));
    }
    catch (FileFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
    return this.m3gObject;
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

  public Object3DType toXML()
  {
    return null;
  }
}
