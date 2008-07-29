package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#Object3D<br>
  UInt32           userID;<br>
  ObjectIndex[]    animationTracks;<br>
  UInt32           userParameterCount;<br>
  FOR each user parameter...<br>
    UInt32        parameterID;<br>
    Byte[]        parameterValue;<br>
  END<br>
  <br>
 * @author jsaarinen
 */
public abstract class Object3D implements M3GSerializable
{
  public static class UserParameter implements M3GSerializable
  {
    public int parameterID;
    public byte[] parameterValue;
    
    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
      this.parameterID = M3GSupport.readInt(dataInputStream);
      int parameterValueLength = M3GSupport.readInt(dataInputStream);
      this.parameterValue = new byte[parameterValueLength];
      dataInputStream.read(this.parameterValue);
    }
    
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
      M3GSupport.writeInt(dataOutputStream, this.parameterID);
      M3GSupport.writeInt(dataOutputStream, this.parameterValue.length);
      dataOutputStream.write(this.parameterValue);
    }    
  }

  private ObjectIndex[] animationTracks;
  private int userParameterCount;
  private UserParameter[] userParameters;

  /**
   * Constructor for the class. 
   * 
   * @param animationTracks
   * @param userParameters
   *  Not mandatory, can be null which means that there's no
   *  user parameters.
   */
  public Object3D(ObjectIndex[] animationTracks,
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

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    int animationTracksLength = M3GSupport.readInt(dataInputStream);
    this.animationTracks = new ObjectIndex[animationTracksLength];
    for (int i = 0; i < this.animationTracks.length; i++)
    {
      this.animationTracks[i] = new ObjectIndex();
      this.animationTracks[i].deserialize(dataInputStream, m3gVersion);
    }
    
    this.userParameterCount = M3GSupport.readInt(dataInputStream);
    this.userParameters = new UserParameter[this.userParameterCount];
    for (int i = 0; i < this.userParameters.length; i++)
    {
      this.userParameters[i] = new UserParameter();
      this.userParameters[i].deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    M3GSupport.writeInt(dataOutputStream, this.animationTracks.length);
    for (int i = 0; i < this.animationTracks.length; i++)
    {
      this.animationTracks[i].serialize(dataOutputStream, null);
    }
    M3GSupport.writeInt(dataOutputStream, this.userParameterCount);
    for (int i = 0; i < this.userParameterCount; i++)
    {
      UserParameter userParameter = this.userParameters[i];
      userParameter.serialize(dataOutputStream, m3gVersion);
    }
  }
}
