package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;

/**
  ObjectIndex   image;
  ColorRGB      blendColor;
  Byte          blending;
  Byte          wrappingS;
  Byte          wrappingT;
  Byte          levelFilter;
  Byte          imageFilter;

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

  private ObjectIndex texture;
  private ColorRGB blendColor;
  private int blending;
  private int wrappingS;
  private int wrappingT;
  private int levelFilter;
  private int imageFilter;

  public Texture2D(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, Vector3D translation, Vector3D scale,
      float orientationAngle, Vector3D orientationAxis, ObjectIndex texture,
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

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.texture = new ObjectIndex();
    this.texture.deserialize(dataInputStream, m3gVersion);
    this.blendColor = new ColorRGB();
    this.blendColor.deserialize(dataInputStream, m3gVersion);
    this.blending = dataInputStream.readByte() & 0xFF;
    this.wrappingS = dataInputStream.readByte() & 0xFF;
    this.wrappingT = dataInputStream.readByte() & 0xFF;
    this.levelFilter = dataInputStream.readByte() & 0xFF;
    this.imageFilter = dataInputStream.readByte() & 0xFF;
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.texture.serialize(dataOutputStream, m3gVersion);
    this.blendColor.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.blending);
    dataOutputStream.write(this.wrappingS);
    dataOutputStream.write(this.wrappingT);
    dataOutputStream.write(this.levelFilter);
    dataOutputStream.write(this.imageFilter);
  }
 
  public byte getObjectType()
  {
    return ObjectTypes.TEXTURE_2D;
  }

  public ObjectIndex getTexture()
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
