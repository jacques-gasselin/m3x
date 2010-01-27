/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import m3x.m3g.primitives.SectionSerializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import java.util.ArrayList;
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#SkinnedMesh<br>
  ObjectIndex   skeleton;<br>
  UInt32        transformReferenceCount;<br>
  FOR each bone reference...<br>
    ObjectIndex   transformNode;<br>
    UInt32        firstVertex;<br>
    UInt32        vertexCount;<br>
    Int32         weight;<br>
  END<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class SkinnedMesh extends Mesh implements SectionSerializable
{
    private static final class Bone
    {
        private Node transformNode;
        private int firstVertex;
        private int vertexCount;
        private int weight;

        public Bone(Node transformNode, int firstVertex,
            int vertexCount, int weight)
        {
            this.transformNode = transformNode;
            this.firstVertex = firstVertex;
            this.vertexCount = vertexCount;
            this.weight = weight;
        }

        public Node getTransformNode()
        {
            return this.transformNode;
        }

        public int getFirstVertex()
        {
            return this.firstVertex;
        }

        public int getVertexCount()
        {
            return this.vertexCount;
        }

        public int getWeight()
        {
            return this.weight;
        }
    }
    
    private Group skeleton;
    private final ArrayList<Bone> boneReferences = new ArrayList<Bone>();

    public SkinnedMesh()
    {
        super();
    }

    public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes,
            Appearance[] appearances, Group skeleton)
    {
        super(vertices, submeshes, appearances);
        setSkeleton(skeleton);
    }

    public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh,
            Appearance appearance, Group skeleton)
    {
        super(vertices, submesh, appearance);
        setSkeleton(skeleton);
    }

    private final void resetBones(int capacity)
    {
        boneReferences.clear();
        boneReferences.ensureCapacity(capacity);
    }

    public void addTransform(Node bone, int weight, int firstVertex, int vertexCount)
    {
        boneReferences.add(new Bone(bone, firstVertex, vertexCount, weight));
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getSkeleton());
    }

    @Override
    public int getSectionObjectType()
    {
        return ObjectTypes.SKINNED_MESH;
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setSkeleton((Group)deserialiser.readReference());
        final int transformReferenceCount = deserialiser.readInt();
        resetBones(transformReferenceCount);
        for (int i = 0; i < transformReferenceCount; ++i)
        {
            final Node bone = (Node) deserialiser.readReference();
            final int firstVertex = deserialiser.readInt();
            final int vertexCount = deserialiser.readInt();
            final int weight = deserialiser.readInt();
            addTransform(bone, weight, firstVertex, vertexCount);
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getSkeleton());
        final int transformReferenceCount = getTransformReferenceCount();
        serialiser.writeInt(transformReferenceCount);
        for (int i = 0; i < transformReferenceCount; ++i)
        {
            Bone ref = this.boneReferences.get(i);
            serialiser.writeReference(ref.getTransformNode());
            serialiser.writeInt(ref.getFirstVertex());
            serialiser.writeInt(ref.getVertexCount());
            serialiser.writeInt(ref.getWeight());
        }
    }

    public Group getSkeleton()
    {
        return this.skeleton;
    }

    public int getTransformReferenceCount()
    {
        return this.boneReferences.size();
    }

    public void setSkeleton(Group skeleton)
    {
        this.skeleton = skeleton;
        this.skeleton.setParent(this);
    }
}
