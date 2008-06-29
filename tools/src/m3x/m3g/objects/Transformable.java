package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;

public abstract class Transformable extends Object3D implements M3GSerializable
{
  private boolean hasComponentTransforn;
  private Vector3D translation;
  private Vector3D scale;
  private float orientationAngle;
  private Vector3D orientationAxis;

  private boolean hasGeneralTransform;
  private Matrix transform;

  public Transformable(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, Vector3D translation, Vector3D scale,
      float orientationAngle, Vector3D orientationAxis)
  {
    super(animationTracks, userParameters);
    this.hasComponentTransforn = true;
    this.translation = translation;
    this.scale = scale;
    this.orientationAngle = orientationAngle;
    this.orientationAxis = orientationAxis;
    this.hasGeneralTransform = false;
    this.transform = null;
  }

  public Transformable()
  {
    super();
  }

  public Transformable(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, Matrix transform)
  {
    super(animationTracks, userParameters);
    this.hasComponentTransforn = false;
    this.translation = null;
    this.scale = null;
    this.orientationAngle = 0.0f;
    this.orientationAxis = null;
    this.hasGeneralTransform = true;
    this.transform = transform;
  }

  
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    if (this.hasComponentTransforn)
    {
      dataOutputStream.writeBoolean(true);
      this.translation.serialize(dataOutputStream, m3gVersion);
      this.scale.serialize(dataOutputStream, m3gVersion);
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.orientationAngle));
      this.orientationAxis.serialize(dataOutputStream, m3gVersion);
      dataOutputStream.writeBoolean(false);
    }
    else if (this.hasGeneralTransform)
    {
      dataOutputStream.writeBoolean(false);
      dataOutputStream.writeBoolean(true);
      this.transform.serialize(dataOutputStream, m3gVersion);
    }
  }
}
