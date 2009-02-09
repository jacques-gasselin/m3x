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
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#Mesh<br>
  ObjectIndex   vertexBuffer;<br>
  UInt32        submeshCount;<br>
  FOR each submesh...<br>
    ObjectIndex   indexBuffer;<br>
    ObjectIndex   appearance;<br>
  END<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Mesh extends Node implements SectionSerialisable
{
    private static class SubMesh implements Serialisable
    {

        private IndexBuffer indexBuffer;
        private Appearance appearance;

        public SubMesh()
        {
            setAppearance(null);
            setIndexBuffer(null);
        }

        public SubMesh(IndexBuffer indexBuffer, Appearance appearance)
        {
            setAppearance(appearance);
            setIndexBuffer(indexBuffer);
        }
        
        public Appearance getAppearance()
        {
            return this.appearance;
        }

        public IndexBuffer getIndexBuffer()
        {
            return this.indexBuffer;
        }

        public void setAppearance(Appearance appearance)
        {
            this.appearance = appearance;
        }

        public void setIndexBuffer(IndexBuffer indexBuffer)
        {
            this.indexBuffer = indexBuffer;
        }

        public void deserialise(Deserialiser deserialiser)
            throws IOException
        {
            this.indexBuffer = (IndexBuffer)deserialiser.readReference();
            this.appearance = (Appearance)deserialiser.readReference();
        }

        public void serialise(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getIndexBuffer());
            serialiser.writeReference(getAppearance());
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof SubMesh))
            {
                return false;
            }
            SubMesh another = (SubMesh) obj;
            return this.indexBuffer.equals(another.indexBuffer) &&
                this.appearance.equals(another.appearance);
        }
    }

    private VertexBuffer vertexBuffer;
    private SubMesh[] subMeshes;

    public Mesh()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setVertexBuffer((VertexBuffer)deserialiser.readReference());
        final int subMeshCount = deserialiser.readInt();
        setSubmeshCount(subMeshCount);
        for (int i = 0; i < subMeshCount; i++)
        {
            this.subMeshes[i].deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeReference(getVertexBuffer());
        serialiser.writeInt(getSubmeshCount());
        for (SubMesh subMesh : this.subMeshes)
        {
            subMesh.serialise(serialiser);
        }
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getVertexBuffer());
        for (int i = 0; i < getSubmeshCount(); ++i)
        {
            queue.add(getAppearance(i));
            queue.add(getIndexBuffer(i));
        }
    }


    public int getSectionObjectType()
    {
        return ObjectTypes.MESH;
    }

    private final void requireIndexInSubmeshRange(int index)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index < 0");
        }
        if (index >= getSubmeshCount())
        {
            throw new IllegalArgumentException("index >= getSubmeshCount()");
        }
    }

    private final SubMesh getSubMesh(int index)
    {
        requireIndexInSubmeshRange(index);

        return this.subMeshes[index];
    }

    public Appearance getAppearance(int index)
    {
        return getSubMesh(index).getAppearance();
    }

    public IndexBuffer getIndexBuffer(int index)
    {
        return getSubMesh(index).getIndexBuffer();
    }

    public int getSubmeshCount()
    {
        if (this.subMeshes == null)
        {
            return 0;
        }
        return this.subMeshes.length;
    }

    public VertexBuffer getVertexBuffer()
    {
        return this.vertexBuffer;
    }

    public void setAppearance(int index, Appearance appearance)
    {
        getSubMesh(index).setAppearance(appearance);
    }

    public void setIndexBuffer(int index, IndexBuffer indexBuffer)
    {
        getSubMesh(index).setIndexBuffer(indexBuffer);
    }

    public void setSubmeshCount(int submeshCount)
    {
        this.subMeshes = new SubMesh[submeshCount];
        for (int i = 0; i < submeshCount; ++i)
        {
            this.subMeshes[i] = new SubMesh();
        }
    }

    public void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        this.vertexBuffer = vertexBuffer;
    }
}
