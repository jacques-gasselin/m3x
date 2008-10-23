package m3x.m3g;

import m3x.m3g.Node;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#MorphingMesh<br>
  UInt32        morphTargetCount;<br>
  FOR each target buffer...<br>
    ObjectIndex   morphTarget;<br>
    Float32       initialWeight;<br>
  END<br>
  <br>
 * @author jsaarinen
 */
public class MorphingMesh extends Node implements M3GTypedObject
{
    public static class TargetBuffer implements M3GSerializable
    {

        private ObjectIndex morphTarget;
        private float initialWeight;

        public TargetBuffer()
        {
        }

        public TargetBuffer(ObjectIndex morphTarget, float initialWeight)
        {
            this.morphTarget = morphTarget;
            this.initialWeight = initialWeight;
        }

        public ObjectIndex getMorphTarget()
        {
            return this.morphTarget;
        }

        public float getInitialWeight()
        {
            return this.initialWeight;
        }

        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof TargetBuffer))
            {
                return false;
            }
            TargetBuffer another = (TargetBuffer) obj;
            return this.morphTarget.equals(another.morphTarget) && this.initialWeight == another.initialWeight;
        }

        public void deserialize(DataInputStream dataInputStream, String m3gVersion)
            throws IOException, FileFormatException
        {
            this.morphTarget = new ObjectIndex();
            this.morphTarget.deserialize(dataInputStream, m3gVersion);
            this.initialWeight = M3GSupport.readFloat(dataInputStream);
        }

        public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException
        {
            this.morphTarget.serialize(dataOutputStream, m3gVersion);
            M3GSupport.writeFloat(dataOutputStream, this.initialWeight);
        }
    }
    private int morphTargetCount;
    private TargetBuffer[] morphTargets;

    public MorphingMesh(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, Matrix transform,
        boolean enableRendering, boolean enablePicking, byte alphaFactor,
        int scope, TargetBuffer[] morphTargets) throws FileFormatException
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (morphTargets != null);
        assert (morphTargets.length > 0);
        this.morphTargetCount = morphTargets.length;
        this.morphTargets = morphTargets;
    }

    public MorphingMesh()
    {
        super();
    }

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.morphTargetCount = M3GSupport.readInt(dataInputStream);
        this.morphTargets = new TargetBuffer[this.morphTargetCount];
        for (int i = 0; i < this.morphTargets.length; i++)
        {
            this.morphTargets[i] = new TargetBuffer();
            this.morphTargets[i].deserialize(dataInputStream, m3gVersion);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.morphTargetCount);
        for (TargetBuffer targetBuffer : this.morphTargets)
        {
            targetBuffer.serialize(dataOutputStream, m3gVersion);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.MORPHING_MESH;
    }

    public int getMorphTargetCount()
    {
        return this.morphTargetCount;
    }

    public TargetBuffer[] getMorphTargets()
    {
        return this.morphTargets;
    }
}
