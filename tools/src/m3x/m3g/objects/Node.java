package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
  Boolean       enableRendering;
  Boolean       enablePicking;
  Byte          alphaFactor;
  UInt32        scope;
  Boolean       hasAlignment;
  IF hasAlignment==TRUE, THEN
    Byte          zTarget;
    Byte          yTarget;

    ObjectIndex   zReference;
    ObjectIndex   yReference;
  END

 * @author jsaarinen
 */
public abstract class Node extends Transformable implements M3GSerializable
{
  public static final int NONE = 144;
  public static final int ORIGIN = 145;
  public static final int X_AXIS = 146;
  public static final int Y_AXIS = 147;
  public static final int Z_AXIS = 148;

  private boolean enableRendering;
  private boolean enablePicking;
  private byte alphaFactor;
  private int scope;
  private boolean hasAlignment;
  private byte zTarget;
  private byte yTarget;
  private ObjectIndex zReference;
  private ObjectIndex yReference;

  public Node(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope)
  {
    super(animationTracks, userParameters, transform);
    this.enableRendering = enableRendering;
    this.enablePicking = enablePicking;
    this.alphaFactor = alphaFactor;
    this.scope = scope;
    this.hasAlignment = false;
    this.zTarget = 0;
    this.yTarget = 0;
    this.zReference = null;
    this.yReference = null;
  }

  public Node(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, byte zTarget, byte yTarget,
      ObjectIndex zReference, ObjectIndex yReference)
  {
    super(animationTracks, userParameters, transform);
    this.enableRendering = enableRendering;
    this.enablePicking = enablePicking;
    this.alphaFactor = alphaFactor;
    this.scope = scope;
    this.hasAlignment = true;
    this.zTarget = zTarget;
    this.yTarget = yTarget;
    this.zReference = zReference;
    this.yReference = yReference;
  }

  public Node()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.enableRendering = dataInputStream.readBoolean();
    this.enablePicking = dataInputStream.readBoolean();
    this.alphaFactor = dataInputStream.readByte();
    this.scope = M3GSupport.readInt(dataInputStream);
    this.hasAlignment = dataInputStream.readBoolean();
    if (this.hasAlignment)
    {
      this.zTarget = dataInputStream.readByte();
      this.yTarget = dataInputStream.readByte();
      this.zReference.deserialize(dataInputStream, m3gVersion);
      this.yReference.deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.writeBoolean(this.enableRendering);
    dataOutputStream.writeBoolean(this.enablePicking);
    dataOutputStream.write(this.alphaFactor);
    M3GSupport.writeInt(dataOutputStream, this.scope);
    if (this.hasAlignment)
    {
      dataOutputStream.write(this.zTarget);
      dataOutputStream.write(this.yTarget);
      this.zReference.serialize(dataOutputStream, m3gVersion);
      this.yReference.serialize(dataOutputStream, m3gVersion);
    }
  }
}
