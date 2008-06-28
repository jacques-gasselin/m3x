package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.primitives.ObjectIndex;

public abstract class Object3D implements M3GSerializable
{
  public static class UserParameter implements M3GSerializable
  {
    public int parameterID;
    public byte[] parameterValue;
    
    public void deserialize(DataInputStream dataInputStream, String version)
        throws IOException, FileFormatException
    {
      this.parameterID = M3GSupport.swapBytes(dataInputStream.readInt());
      int parameterValueLength = M3GSupport.swapBytes(dataInputStream.readInt());
      this.parameterValue = new byte[parameterValueLength];
      dataInputStream.read(this.parameterValue);
    }
    
    public void serialize(DataOutputStream dataOutputStream, String version)
        throws IOException
    {
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.parameterID));
      dataOutputStream.writeInt(M3GSupport.swapBytes(this.parameterValue.length));
      dataOutputStream.write(this.parameterValue);
    }    
  }

  private ObjectIndex[] animationTracks;
  private int userParameterCount;
  private UserParameter[] userParameters;

  protected Object3D(ObjectIndex[] animationTracks,
      UserParameter[] userParameters)
  {
    assert (animationTracks != null);
    this.animationTracks = animationTracks;
    this.userParameterCount = userParameters != null ? userParameters.length : 0;
    this.userParameters = userParameters;
  }
 
  public Object3D()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException, FileFormatException
  {
    int animationTracksLength = M3GSupport.swapBytes(dataInputStream.readInt());
    this.animationTracks = new ObjectIndex[animationTracksLength];
    for (int i = 0; i < this.animationTracks.length; i++)
    {
      this.animationTracks[i] = new ObjectIndex();
      this.animationTracks[i].deserialize(dataInputStream, version);
    }
    
    this.userParameterCount = M3GSupport.swapBytes(dataInputStream.readInt());
    this.userParameters = new UserParameter[this.userParameterCount];
    for (int i = 0; i < this.userParameters.length; i++)
    {
      this.userParameters[i] = new UserParameter();
      this.userParameters[i].deserialize(dataInputStream, version);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.animationTracks.length));
    for (int i = 0; i < this.animationTracks.length; i++)
    {
      this.animationTracks[i].serialize(dataOutputStream, null);
    }
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.userParameterCount));
    for (int i = 0; i < this.userParameterCount; i++)
    {
      UserParameter userParameter = this.userParameters[i];
      userParameter.serialize(dataOutputStream, m3gVersion);
    }
  }
}
