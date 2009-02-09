/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Vector;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.Serialisable;
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
 * @author jgasseli
 */
public abstract class Object3D implements SectionSerialisable
{
    private int userID;
    private Vector<AnimationTrack> animationTracks;
    private int userParameterCount;
    private UserParameter[] userParameters;

    public static class UserParameter implements Serialisable
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

        public void deserialise(Deserialiser deserialiser)
            throws IOException
        {
            this.parameterID = deserialiser.readInt();
            int parameterValueLength = deserialiser.readInt();
            this.parameterValue = new byte[parameterValueLength];
            deserialiser.readFully(this.parameterValue);
        }

        public void serialise(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeInt(this.parameterID);
            serialiser.writeInt(this.parameterValue.length);
            serialiser.write(this.parameterValue);
        }
    }

    protected Object3D()
    {
        super();
        this.animationTracks = new Vector<AnimationTrack>();
        setUserID(0);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Object3D other = (Object3D) obj;
        if (getUserID() != other.getUserID())
        {
            return false;
        }

        //does the equal comparison for all referenced objects.
        final int referenceCountA = getReferences(null);
        final int referenceCountB = other.getReferences(null);
        if (referenceCountA != referenceCountB)
        {
            return false;
        }

        final Object3D[] referencesA = new Object3D[referenceCountA];
        getReferences(referencesA);

        final Object3D[] referencesB = new Object3D[referenceCountB];
        other.getReferences(referencesB);

        for (int i = 0; i < referenceCountA; ++i)
        {
            Object3D a = referencesA[i];
            Object3D b = referencesB[i];
            if (!a.equals(b))
            {
                return false;
            }
        }

        //FIXME compare user object
        
        return true;
    }

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        setUserID(deserialiser.readInt());
        final int numTracks = deserialiser.readInt();
        for (int i = 0; i < numTracks; ++i)
        {
            addAnimationTrack((AnimationTrack)deserialiser.readReference());
        }

        this.userParameterCount = deserialiser.readInt();
        this.userParameters = new UserParameter[this.userParameterCount];
        for (int i = 0; i < this.userParameters.length; ++i)
        {
            this.userParameters[i] = new UserParameter();
            this.userParameters[i].deserialise(deserialiser);
        }
    }

    public void serialise(Serialiser serialiser)
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
            userParameter.serialise(serialiser);
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

    protected void setReferenceQueue(Object3DReferences queue)
    {
        for (int i = 0; i < getAnimationTrackCount(); ++i)
        {
            queue.add(getAnimationTrack(i));
        }
    }

    public final int getReferences(Object3D[] references)
    {
        Object3DReferences queue = new Object3DReferences(0, references);
        setReferenceQueue(queue);
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

    public final int getFieldValue(String fieldName, String context)
    {
        if (fieldName == null)
        {
            throw new NullPointerException("fieldName is null");
        }
        try
        {
            Field field = getClass().getField(fieldName);
            return field.getInt(this);
        }
        catch (NoSuchFieldException e)
        {
            throw new IllegalArgumentException("unknown " + context + " " + fieldName);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("no access to " + context + " " + fieldName);
        }
    }

}
