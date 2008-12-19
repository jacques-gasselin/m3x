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
            throws IOException
        {
            this.morphTarget = (VertexBuffer)deserialiser.readReference();
            this.initialWeight = deserialiser.readFloat();
        }

        public void serialize(M3GSerialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getMorphTarget());
            serialiser.writeFloat(initialWeight);
        }
    }
    
    private TargetBuffer[] morphTargets;

    public MorphingMesh(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Matrix transform,
        boolean enableRendering, boolean enablePicking, byte alphaFactor,
        int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes,
        TargetBuffer[] morphTargets)
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
        throws IOException
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

    @Override
    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeInt(this.morphTargets.length);
        for (TargetBuffer targetBuffer : this.morphTargets)
        {
            targetBuffer.serialize(serialiser);
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
