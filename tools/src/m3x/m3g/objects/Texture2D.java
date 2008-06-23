package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ObjectIndex;

public class Texture2D extends Object3D implements M3GSerializable
{
  public static final byte FILTER_BASE_LEVEL = (byte)208;
  public static final byte FILTER_LINEAR = (byte)209;
  public static final byte FILTER_NEAREST = (byte)210;
  public static final byte FUNC_ADD = (byte)224;
  public static final byte FUNC_BLEND = (byte)225;
  public static final byte FUNC_DECAL = (byte)226;
  public static final byte FUNC_MODULATE = (byte)227;
  public static final byte FUNC_REPLACE = (byte)228;
  public static final byte WRAP_CLAMP = (byte)240;
  public static final byte WRAP_REPEAT = (byte)241;
  
  private final ObjectIndex texture;
  private final ColorRGB blendColor;
  private final byte blending;
  private final byte wrappingS;
  private final byte wrappingT;
  private final byte levelFilter;
  private final byte imageFilter;
  
  public Texture2D(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, ObjectIndex texture, ColorRGB blendColor,
      byte blending, byte wrappingS, byte wrappingT, byte levelFilter,
      byte imageFilter)
  {
    super(animationTracks, userParameters);
    this.texture = texture;
    this.blendColor = blendColor;
    this.blending = blending;
    this.wrappingS = wrappingS;
    this.wrappingT = wrappingT;
    this.levelFilter = levelFilter;
    this.imageFilter = imageFilter;
  }

  @Override
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
}
