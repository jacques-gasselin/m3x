package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.util.LittleEndianDataInputStream;

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
    private int userID;
    private ObjectIndex[] animationTracks;
    private int userParameterCount;
    private UserParameter[] userParameters;

    public static class UserParameter implements M3GSerializable
    {

        private int parameterID;
        private byte[] parameterValue;

        public UserParameter(int parameterID, byte[] parameterValue)
        {
            this.parameterID = parameterID;
            this.parameterValue = parameterValue;
        }

        public UserParameter()
        {
        }

        public int getParameterID()
        {
            return this.parameterID;
        }

        public byte[] getParameterValue()
        {
            return this.parameterValue;
        }

        public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
            throws IOException, FileFormatException
        {
            this.parameterID = dataInputStream.readInt();
            int parameterValueLength = dataInputStream.readInt();
            this.parameterValue = new byte[parameterValueLength];
            dataInputStream.readFully(this.parameterValue);
        }

        public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException
        {
            M3GSupport.writeInt(dataOutputStream, this.parameterID);
            M3GSupport.writeInt(dataOutputStream, this.parameterValue.length);
            dataOutputStream.write(this.parameterValue);
        }
    }

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

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        this.userID = dataInputStream.readInt();
        int animationTracksLength = dataInputStream.readInt();
        this.animationTracks = new ObjectIndex[animationTracksLength];
        for (int i = 0; i < this.animationTracks.length; i++)
        {
            this.animationTracks[i] = new ObjectIndex();
            this.animationTracks[i].deserialize(dataInputStream, m3gVersion);
        }

        this.userParameterCount = dataInputStream.readInt();
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
        M3GSupport.writeInt(dataOutputStream, this.userID);
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
