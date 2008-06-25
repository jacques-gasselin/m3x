package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class Sprite extends Object3D implements M3GTypedObject
{
  private final ObjectIndex image;
  private final ObjectIndex appearance;
  private final boolean isScaled;
  private final int cropX;
  private final int cropY;
  private final int cropWidth;
  private final int cropHeight;
  
  public Sprite(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      ObjectIndex image, ObjectIndex appearance, boolean isScaled, int cropX,
      int cropY, int cropWidth, int cropHeight)
  {
    super(animationTracks, userParameters);
    this.image = image;
    this.appearance = appearance;
    this.isScaled = isScaled;
    this.cropX = cropX;
    this.cropY = cropY;
    this.cropWidth = cropWidth;
    this.cropHeight = cropHeight;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.image.serialize(dataOutputStream, m3gVersion);
    this.appearance.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeBoolean(this.isScaled);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropX));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropY));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropWidth));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropHeight));
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.SPRITE;
  }
}
