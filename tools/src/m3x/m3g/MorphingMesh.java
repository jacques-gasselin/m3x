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

import m3x.m3g.primitives.Serialisable;
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
