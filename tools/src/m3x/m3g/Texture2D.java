package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Vector3D;

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
 */
public class Texture2D extends Transformable implements M3GTypedObject
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
    private Image2D texture;
    private ColorRGB blendColor;
    private int blending;
    private int wrappingS;
    private int wrappingT;
    private int levelFilter;
    private int imageFilter;

    public Texture2D(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Vector3D translation, Vector3D scale,
        float orientationAngle, Vector3D orientationAxis, Image2D texture,
        ColorRGB blendColor, int blending, int wrappingS, int wrappingT,
        int levelFilter, int imageFilter)
    {
        super(animationTracks, userParameters, translation, scale,
            orientationAngle, orientationAxis);
        this.texture = texture;
        this.blendColor = blendColor;
        this.blending = blending;
        this.wrappingS = wrappingS;
        this.wrappingT = wrappingT;
        this.levelFilter = levelFilter;
        this.imageFilter = imageFilter;
    }

    public Texture2D()
    {
        super();
    }

    @Override
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.texture = (Image2D)deserialiser.readReference();
        this.blendColor = new ColorRGB();
        this.blendColor.deserialize(deserialiser);
        this.blending = deserialiser.readUnsignedByte();
        this.wrappingS = deserialiser.readUnsignedByte();
        this.wrappingT = deserialiser.readUnsignedByte();
        this.levelFilter = deserialiser.readUnsignedByte();
        this.imageFilter = deserialiser.readUnsignedByte();
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeReference(this.texture);
        this.blendColor.serialize(serialiser);
        serialiser.write(this.blending);
        serialiser.write(this.wrappingS);
        serialiser.write(this.wrappingT);
        serialiser.write(this.levelFilter);
        serialiser.write(this.imageFilter);
    }

    public int getObjectType()
    {
        return ObjectTypes.TEXTURE_2D;
    }

    public Image2D getTexture()
    {
        return this.texture;
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
}
