package m3x.m3g;

import m3x.m3g.primitives.Serialisable;
import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class MorphingMesh extends Mesh
{
    public static class TargetBuffer implements Serialisable
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

        public void deserialise(Deserialiser deserialiser)
            throws IOException
        {
            this.morphTarget = (VertexBuffer)deserialiser.readReference();
            this.initialWeight = deserialiser.readFloat();
        }

        public void serialise(Serialiser serialiser)
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
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        int morphTargetCount = deserialiser.readInt();
        this.morphTargets = new TargetBuffer[morphTargetCount];
        for (int i = 0; i < this.morphTargets.length; i++)
        {
            this.morphTargets[i] = new TargetBuffer();
            this.morphTargets[i].deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeInt(this.morphTargets.length);
        for (TargetBuffer targetBuffer : this.morphTargets)
        {
            targetBuffer.serialise(serialiser);
        }
    }

    @Override
    public int getSectionObjectType()
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
