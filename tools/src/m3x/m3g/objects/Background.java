package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

public class Background extends Object3D implements M3GSerializable
{
  private final ColorRGBA backgroundColor;
  private final ObjectIndex backgroundImage;
  private final byte backgroundImageModeX;
  private final byte backgroundImageModeY;
  private final int cropX;
  private final int cropY;
  private final int cropWidth;
  private final int cropHeight;
  private final boolean depthClearEnabled;
  private final boolean colorClearEnabled;
  
  public Background(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, ColorRGBA backgroundColor, ObjectIndex backgroundImage,
      byte backgroundImageModeX, byte backgroundImageModeY, int cropX,
      int cropY, int cropWidth, int cropHeight, boolean depthClearEnabled,
      boolean colorClearEnabled)
  {
    super(animationTracks, userParameters);
    this.backgroundColor = backgroundColor;
    this.backgroundImage = backgroundImage;
    this.backgroundImageModeX = backgroundImageModeX;
    this.backgroundImageModeY = backgroundImageModeY;
    this.cropX = cropX;
    this.cropY = cropY;
    this.cropWidth = cropWidth;
    this.cropHeight = cropHeight;
    this.depthClearEnabled = depthClearEnabled;
    this.colorClearEnabled = colorClearEnabled;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.backgroundColor.serialize(dataOutputStream, null);
    this.backgroundImage.serialize(dataOutputStream, null);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.backgroundImageModeX));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.backgroundImageModeY));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropX));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropY));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropWidth));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.cropHeight));
    dataOutputStream.writeBoolean(this.depthClearEnabled);
    dataOutputStream.writeBoolean(this.colorClearEnabled);
  }
}
