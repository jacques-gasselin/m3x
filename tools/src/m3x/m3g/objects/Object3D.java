package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.primitives.ObjectIndex;

public abstract class Object3D implements M3GSerializable
{
    public static class UserParameter
    {
        public int parameterID;
        public byte[] parameterValue;
    }
  
    private final ObjectIndex[] animationTracks;
    private final int userParameterCount;
    private final UserParameter[] userParameters;

    protected Object3D(ObjectIndex[] animationTracks, UserParameter[] userParameters)
    {
        assert(animationTracks != null);
        this.animationTracks = animationTracks;
        this.userParameterCount = userParameters != null ? userParameters.length : 0;
        this.userParameters = userParameters;
    }
  

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        for (int i = 0; i < this.animationTracks.length; i++)
        {
            this.animationTracks[i].serialize(dataOutputStream, null);
        }
        dataOutputStream.writeInt(M3GSupport.swapBytes(this.userParameterCount));
        for (int i = 0; i < this.userParameterCount; i++)
        {
            UserParameter userParameter = this.userParameters[i];
            dataOutputStream.writeInt(M3GSupport.swapBytes(userParameter.parameterID));
            dataOutputStream.write(userParameter.parameterValue);
        }
    }
}
