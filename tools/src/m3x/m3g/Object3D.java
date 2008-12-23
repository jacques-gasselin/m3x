package m3x.m3g;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    /**
     * Does a null safe deep equality comparison of 2 objects.
     * @param value1
     * @param value2
     * @return
     */
    private static boolean nullSafeEquals(Object value1, Object value2)
    {
        //Check for quick null safe equality
        if (value1 == null)
        {
            if (value2 == null)
            {
                return true;
            }
            return false;
        }

        //Compare the classes
        if (!(value1.getClass().equals(value2.getClass())))
        {
            return false;
        }

        //do full compare
        if (value1.getClass().isArray())
        {
            //array compare

            //length
            final int length = Array.getLength(value1);
            if (length != Array.getLength(value2))
            {
                return false;
            }

            //contents
            for (int i = 0; i < length; ++i)
            {
                Object a = Array.get(value1, i);
                Object b = Array.get(value2, i);
                if (!nullSafeEquals(a, b))
                {
                    return false;
                }
            }
        }
        else if (!value1.equals(value2))
        {
            return false;
        }

        return true;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }

        //Compare the classes
        if (!(getClass().equals(obj.getClass())))
        {
            return false;
        }

        //Get the bean for the class
        BeanInfo bean = null;
        try
        {
            bean = Introspector.getBeanInfo(getClass());
        }
        catch (IntrospectionException e)
        {
            return false;
        }

        //Compare properties
        for (PropertyDescriptor prop : bean.getPropertyDescriptors())
        {
            Method getter = prop.getReadMethod();
            if (getter != null)
            {
                try
                {
                    Object value1 = getter.invoke(this, (Object[])null);
                    Object value2 = getter.invoke(obj, (Object[])null);

                    if (!nullSafeEquals(value1, value2))
                    {
                        return false;
                    }
                }
                catch (InvocationTargetException e)
                {
                    throw new IllegalStateException("unable to invoke " + getter, e);
                }
                catch (IllegalAccessException e)
                {
                    throw new IllegalStateException("unable to access " + getter, e);
                }
            }
        }


        //Compare references
        Object3D obj3D = (Object3D)obj;
        final int referenceCount = getReferences(null);
        if (referenceCount != obj3D.getReferences(null))
        {
            return false;
        }

        Object3D[] references1 = new Object3D[referenceCount];
        getReferences(references1);
        Object3D[] references2 = new Object3D[referenceCount];
        obj3D.getReferences(references2);

        for (int i = 0; i < referenceCount; ++i)
        {
            Object3D value1 = references1[i];
            Object3D value2 = references2[i];

            if (!nullSafeEquals(value1, value2))
            {
                return false;
            }
        }

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
