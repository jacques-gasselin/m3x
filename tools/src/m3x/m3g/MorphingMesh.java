/**
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

import m3x.m3g.primitives.Serializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

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
    private static final class MorphTarget implements Serializable
    {
        private VertexBuffer target;
        private float weight;

        public MorphTarget()
        {
        }

        public VertexBuffer getTarget()
        {
            return this.target;
        }

        public void setTarget(VertexBuffer target)
        {
            this.target = target;
        }

        public float getWeight()
        {
            return this.weight;
        }

        public void setWeight(float weight)
        {
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof MorphTarget))
            {
                return false;
            }
            MorphTarget another = (MorphTarget) obj;
            return this.target.equals(another.target) &&
                   this.weight == another.weight;
        }

        public void deserialise(Deserializer deserialiser)
            throws IOException
        {
            setTarget((VertexBuffer) deserialiser.readReference());
            setWeight(deserialiser.readFloat());
        }

        public void serialise(Serializer serialiser)
            throws IOException
        {
            serialiser.writeReference(getTarget());
            serialiser.writeFloat(getWeight());
        }
    }
    
    private MorphTarget[] morphTargets;

    public MorphingMesh()
    {
        super();
    }

    private static final void requireTargetsNotNull(VertexBuffer[] targets)
    {
        if (targets == null)
        {
            throw new NullPointerException("targets is null");
        }
    }
    
    public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] submeshes, Appearance[] appearances)
    {
        super(base, submeshes, appearances);

        requireTargetsNotNull(targets);
        setMorphTargetCount(targets.length);
    }

    public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer submesh, Appearance appearance)
    {
        super(base, submesh, appearance);

        requireTargetsNotNull(targets);
        setMorphTargetCount(targets.length);
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        final int morphTargetCount = deserialiser.readInt();
        setMorphTargetCount(morphTargetCount);
        for (int i = 0; i < morphTargetCount; ++i)
        {
            morphTargets[i].deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        final int morphTargetCount = getMorphTargetCount();
        serialiser.writeInt(morphTargetCount);
        for (int i = 0; i < morphTargetCount; ++i)
        {
            MorphTarget target = morphTargets[i];
            target.serialise(serialiser);
        }
    }

    @Override
    public int getSectionObjectType()
    {
        return ObjectTypes.MORPHING_MESH;
    }

    public VertexBuffer getMorphTarget(int index)
    {
        return morphTargets[index].getTarget();
    }

    public int getMorphTargetCount()
    {
        if (morphTargets == null)
        {
            return 0;
        }
        return morphTargets.length;
    }

    private void setMorphTargetCount(int morphTargetCount)
    {
        morphTargets = new MorphTarget[morphTargetCount];
        for (int i = 0; i < morphTargetCount; ++i)
        {
            morphTargets[i] = new MorphTarget();
        }
    }

    public void getWeights(float[] weights)
    {
        if (weights == null)
        {
            throw new NullPointerException("weights is null");
        }
        final int morphTargetCount = getMorphTargetCount();
        if (weights.length < morphTargetCount)
        {
            throw new IllegalArgumentException("weights.length < getMorphTargetCount()");
        }

        for (int i = 0; i < morphTargetCount; ++i)
        {
            weights[i] = morphTargets[i].getWeight();
        }
    }
}
