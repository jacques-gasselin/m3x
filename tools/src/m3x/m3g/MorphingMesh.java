package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

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
public class MorphingMesh extends Mesh
{
    public static class TargetBuffer implements M3GSerializable
    {

        private VertexBuffer morphTarget;
        private float initialWeight;

        public TargetBuffer()
        {
        }

        public TargetBuffer(VertexBuffer morphTarget, float initialWeight)
        {
            this.morphTarget = morphTarget;
            this.initialWeight = initialWeight;
        }

        public VertexBuffer getMorphTarget()
        {
            return this.morphTarget;
        }

        public float getInitialWeight()
        {
            return this.initialWeight;
        }

        @Override
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
            return this.morphTarget.equals(another.morphTarget) &&
                   this.initialWeight == another.initialWeight;
        }

        public void deserialize(M3GDeserialiser deserialiser)
            throws IOException, FileFormatException
        {
            this.morphTarget = (VertexBuffer)deserialiser.readObjectReference();
            this.initialWeight = deserialiser.readFloat();
        }

        public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
            throws IOException
        {
            this.morphTarget.serialize(dataOutputStream, m3gVersion);
            M3GSupport.writeFloat(dataOutputStream, this.initialWeight);
        }
    }
    
    private TargetBuffer[] morphTargets;

    public MorphingMesh(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Matrix transform,
        boolean enableRendering, boolean enablePicking, byte alphaFactor,
        int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes,
        TargetBuffer[] morphTargets) throws FileFormatException
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope, vertexBuffer, subMeshes);
        assert (morphTargets != null);
        assert (morphTargets.length > 0);
        this.morphTargets = morphTargets;
    }

    public MorphingMesh()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        int morphTargetCount = deserialiser.readInt();
        this.morphTargets = new TargetBuffer[morphTargetCount];
        for (int i = 0; i < this.morphTargets.length; i++)
        {
            this.morphTargets[i] = new TargetBuffer();
            this.morphTargets[i].deserialize(deserialiser);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeInt(dataOutputStream, this.morphTargets.length);
        for (TargetBuffer targetBuffer : this.morphTargets)
        {
            targetBuffer.serialize(dataOutputStream, m3gVersion);
        }
    }

    @Override
    public int getObjectType()
    {
        return ObjectTypes.MORPHING_MESH;
    }

    public int getMorphTargetCount()
    {
        return this.morphTargets.length;
    }

    public TargetBuffer[] getMorphTargets()
    {
        return this.morphTargets;
    }
}
