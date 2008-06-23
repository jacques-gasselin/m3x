package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ObjectIndex;

public class AnimationTrack extends Object3D implements M3GSerializable
{
  public static final int ALPHA = 256;
  public static final int AMBIENT_COLOR = 257;
  public static final int COLOR = 258;
  public static final int CROP = 259;
  public static final int DENSITY = 260;
  public static final int DIFFUSE_COLOR = 261;
  public static final int EMISSIVE_COLOR = 262;
  public static final int FAR_DISTANCE = 263;
  public static final int FIELD_OF_VIEW = 264;
  public static final int INTENSITY = 265;
  public static final int MORPH_WEIGHTS = 266;
  public static final int NEAR_DISTANCE = 267;
  public static final int ORIENTATION = 268;
  public static final int PICKABILITY = 269;
  public static final int SCALE = 270;
  public static final int SHININESS = 271;
  public static final int SPECULAR_COLOR = 272;
  public static final int SPOT_ANGLE = 273;
  public static final int SPOT_EXPONENT = 274;
  public static final int TRANSLATION = 275;
  public static final int VISIBILITY = 276;
  
  private final ObjectIndex keyframeSequence;
  private final ObjectIndex animationController;
  private final int propertyID;
  
  public AnimationTrack(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, ObjectIndex keyframeSequence,
      ObjectIndex animationController, int propertyID)
  {
    super(animationTracks, userParameters);
    this.keyframeSequence = keyframeSequence;
    this.animationController = animationController;
    this.propertyID = propertyID;
  }

  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.keyframeSequence.serialize(dataOutputStream, null);
    this.animationController.serialize(dataOutputStream, null);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.propertyID));
  }
}
