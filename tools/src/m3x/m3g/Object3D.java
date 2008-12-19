package m3x.m3g;

import m3x.m3g.primitives.Serializable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import m3x.m3g.util.Object3DReferences;

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
public abstract class Object3D implements Serializable
{
    private int userID;
    private Vector<AnimationTrack> animationTracks;
    private int userParameterCount;
    private UserParameter[] userParameters;

    public static class UserParameter implements Serializable
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

        public void deserialize(Deserialiser deserialiser)
            throws IOException
        {
            this.parameterID = deserialiser.readInt();
            int parameterValueLength = deserialiser.readInt();
            this.parameterValue = new byte[parameterValueLength];
            deserialiser.readFully(this.parameterValue);
        }

        public void serialize(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeInt(this.parameterID);
            serialiser.writeInt(this.parameterValue.length);
            serialiser.write(this.parameterValue);
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
    public Object3D(AnimationTrack[] animationTracks,
        UserParameter[] userParameters)
    {
        assert (animationTracks != null);
        this.animationTracks = new Vector<AnimationTrack>();
        for (AnimationTrack at : animationTracks)
        {
            addAnimationTrack(at);
        }
        this.userParameterCount = userParameters != null ? userParameters.length : 0;
        this.userParameters = userParameters;
    }

    public Object3D()
    {
        super();
        this.animationTracks = new Vector<AnimationTrack>();
    }

    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        this.userID = deserialiser.readInt();
        final int animationTracksLength = deserialiser.readInt();
        for (int i = 0; i < animationTracksLength; ++i)
        {
            addAnimationTrack((AnimationTrack)deserialiser.readReference());
        }

        this.userParameterCount = deserialiser.readInt();
        this.userParameters = new UserParameter[this.userParameterCount];
        for (int i = 0; i < this.userParameters.length; ++i)
        {
            this.userParameters[i] = new UserParameter();
            this.userParameters[i].deserialize(deserialiser);
        }
    }

    public void serialize(Serialiser serialiser)
        throws IOException
    {
        serialiser.writeInt(this.userID);
        serialiser.writeInt(getAnimationTrackCount());
        for (int i = 0; i < getAnimationTrackCount(); ++i)
        {
            serialiser.writeReference(getAnimationTrack(i));
        }
        serialiser.writeInt(this.userParameterCount);
        for (int i = 0; i < this.userParameterCount; i++)
        {
            UserParameter userParameter = this.userParameters[i];
            userParameter.serialize(serialiser);
        }
    }

    public int getUserID()
    {
        return this.userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public int getReferences(Object3D[] references)
    {
        Object3DReferences queue = new Object3DReferences(0, references);
        for (int i = 0; i < getAnimationTrackCount(); ++i)
        {
            queue.add(getAnimationTrack(i));
        }
        return queue.size();
    }

    public void addAnimationTrack(AnimationTrack animationTrack)
    {
        if (animationTrack == null)
        {
            throw new NullPointerException("animationTrack is null");
        }
        animationTracks.add(animationTrack);
    }

    public AnimationTrack getAnimationTrack(int trackIndex)
    {
        return animationTracks.get(trackIndex);
    }

    public int getAnimationTrackCount()
    {
        return animationTracks.size();
    }
}
