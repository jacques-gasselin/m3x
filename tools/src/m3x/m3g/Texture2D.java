package m3x.m3g;

import java.util.List;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;

/**
 * See http://java2me.org/m3g/file-format.html#Texture2D<br>
  ObjectIndex   image;
  ColorRGB      blendColor;
  Byte          blending;
  Byte          wrappingS;
  Byte          wrappingT;
  Byte          levelFilter;
  Byte          imageFilter;
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Texture2D extends Transformable implements SectionSerialisable
{
    public static final int FILTER_BASE_LEVEL = 208;
    public static final int FILTER_LINEAR = 209;
    public static final int FILTER_NEAREST = 210;
    public static final int FUNC_ADD = 224;
    public static final int FUNC_BLEND = 225;
    public static final int FUNC_DECAL = 226;
    public static final int FUNC_MODULATE = 227;
    public static final int FUNC_REPLACE = 228;
    public static final int WRAP_CLAMP = 240;
    public static final int WRAP_REPEAT = 241;
    private Image2D image;
    private ColorRGB blendColor;
    private int blending;
    private int wrappingS;
    private int wrappingT;
    private int levelFilter;
    private int imageFilter;

    public Texture2D()
    {
        super();
        this.blendColor = new ColorRGB();
        setBlendColor(0xffffff);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.image = (Image2D)deserialiser.readReference();
        this.blendColor = new ColorRGB();
        this.blendColor.deserialise(deserialiser);
        this.blending = deserialiser.readUnsignedByte();
        this.wrappingS = deserialiser.readUnsignedByte();
        this.wrappingT = deserialiser.readUnsignedByte();
        this.levelFilter = deserialiser.readUnsignedByte();
        this.imageFilter = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(this.image);
        this.blendColor.serialise(serialiser);
        serialiser.write(this.blending);
        serialiser.write(this.wrappingS);
        serialiser.write(this.wrappingT);
        serialiser.write(this.levelFilter);
        serialiser.write(this.imageFilter);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.TEXTURE_2D;
    }

    public Image2D getImage()
    {
        return this.image;
    }

    public ColorRGB getBlendColor()
    {
        return this.blendColor;
    }

    public int getBlending()
    {
        return this.blending;
    }

    public int getWrappingS()
    {
        return this.wrappingS;
    }

    public int getWrappingT()
    {
        return this.wrappingT;
    }

    public int getLevelFilter()
    {
        return this.levelFilter;
    }

    public int getImageFilter()
    {
        return this.imageFilter;
    }

    public void setBlendColor(int ARGB)
    {
        this.blendColor.set(ARGB);
    }

    public void setBlendColor(List<Short> blendColor)
    {
        this.blendColor.set(blendColor);
    }

    public void setBlending(int func)
    {
        this.blending = func;
    }

    public void setBlending(String func)
    {
        setBlending(getFieldValue(func, "func"));
    }

    public void setFiltering(int levelFilter, int imageFilter)
    {
        this.levelFilter = levelFilter;
        this.imageFilter = imageFilter;
    }

    public void setFiltering(String levelFilter, String imageFilter)
    {
        setFiltering(getFieldValue(levelFilter, "level filter"),
            getFieldValue(imageFilter, "image filter"));
    }

    public void setImage(Image2D image)
    {
        this.image = image;
    }

    public void setWrapping(int wrapS, int wrapT)
    {
        this.wrappingS = wrapS;
        this.wrappingT = wrapT;
    }

    public void setWrapping(String wrapS, String wrapT)
    {
        setWrapping(getFieldValue(wrapS, "wrapS"), getFieldValue(wrapT, "wrapT"));
    }

}
