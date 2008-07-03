package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.primitives.Vector3D;

/**
  Boolean       hasComponentTransform;
  IF hasComponentTransform==TRUE, THEN
  Vector3D      translation;
  Vector3D      scale;
  Float32       orientationAngle;
  Vector3D      orientationAxis;
  END
  Boolean       hasGeneralTransform;
  IF hasGeneralTransform==TRUE, THEN
    Matrix        transform;
  END

 * @author jsaarinen
 */
public abstract class Transformable extends Object3D implements M3GSerializable
{
  private boolean hasComponentTransform;
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
    this.hasComponentTransform = true;
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
    this.hasComponentTransform = false;
    this.translation = null;
    this.scale = null;
    this.orientationAngle = 0.0f;
    this.orientationAxis = null;
    this.hasGeneralTransform = true;
    this.transform = transform;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.hasComponentTransform = dataInputStream.readBoolean();
    if (this.hasComponentTransform)
    {
      this.translation = new Vector3D();
      this.translation.deserialize(dataInputStream, m3gVersion);
      this.scale = new Vector3D();
      this.scale.deserialize(dataInputStream, m3gVersion);
      this.orientationAngle = M3GSupport.readFloat(dataInputStream);
      this.orientationAxis = new Vector3D();
      this.orientationAxis.deserialize(dataInputStream, m3gVersion);
      this.hasGeneralTransform = false;
    } 
    else 
    if (this.hasComponentTransform)
    {
      this.hasGeneralTransform = false;
      this.transform = new Matrix();
      this.transform.deserialize(dataInputStream, m3gVersion);
    }
    else
    {
      throw new FileFormatException("Neither component transform or general transform present!");
    }
  }
  
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    if (this.hasComponentTransform)
    {
      dataOutputStream.writeBoolean(true);
      this.translation.serialize(dataOutputStream, m3gVersion);
      this.scale.serialize(dataOutputStream, m3gVersion);
      M3GSupport.writeFloat(dataOutputStream, this.orientationAngle);
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
