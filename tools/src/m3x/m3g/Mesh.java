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
            
            SubMesh that = (SubMesh) obj;
            //null guard the checks
            if (this.indexBuffer == null)
            {
                if (that.indexBuffer != null)
                {
                    return false;
                }
            }
            else
            {
                if (this.indexBuffer.equals(that.indexBuffer))
                {
                    return false;
                }
            }

            if (this.appearance == null)
            {
                if (that.appearance != null)
                {
                    return false;
                }
            }
            else
            {
                if (!this.appearance.equals(that.appearance))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private VertexBuffer vertexBuffer;
    private SubMesh[] subMeshes;

    public Mesh()
    {
        super();
    }

    public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances)
    {
        super();
        
        if (submeshes == null)
        {
            throw new NullPointerException("submeshes is null");
        }

        setVertexBuffer(vertices);

        final int submeshCount = submeshes.length;
        setSubmeshCount(submeshCount);
        for (int i = 0; i < submeshCount; ++i)
        {
            setIndexBuffer(i, submeshes[i]);
        }

        if (appearances != null)
        {
            for (int i = 0; i < submeshCount; ++i)
            {
                setAppearance(i, appearances[i]);
            }
        }
    }

    public Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance)
    {
        super();
        setVertexBuffer(vertices);
        setSubmeshCount(1);
        setIndexBuffer(0, submesh);
        setAppearance(0, appearance);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
        {
            return false;
        }

        final Mesh that = (Mesh) obj;
        final int submeshCount = getSubmeshCount();
        if (submeshCount != that.getSubmeshCount())
        {
            return false;
        }

        for (int i = 0; i < submeshCount; ++i)
        {
            SubMesh submeshA = getSubMesh(i);
            SubMesh submeshB = that.getSubMesh(i);
            if (submeshA.equals(submeshB))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setVertexBuffer((VertexBuffer)deserialiser.readReference());
        setSubmeshCount(deserialiser.readInt());
        for (SubMesh subMesh : this.subMeshes)
        {
            subMesh.deserialise(deserialiser);
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
        final int subMeshCount = getSubmeshCount();
        for (int i = 0; i < subMeshCount; ++i)
        {
            queue.add(getIndexBuffer(i));
            queue.add(getAppearance(i));
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
            throw new IndexOutOfBoundsException("index < 0");
        }
        if (index >= getSubmeshCount())
        {
            throw new IndexOutOfBoundsException("index >= getSubmeshCount()");
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
        if (indexBuffer == null)
        {
            throw new NullPointerException("indexBuffer is null");
        }
        //check the indices for validity
        VertexBuffer vb = getVertexBuffer();
        if (vb != null)
        {
            final int vertexCount = vb.getVertexCount();
            if (indexBuffer.isExplicit())
            {
                final int[] indices = indexBuffer.getIndices();
                final int indexCount = indices.length;
                for (int i = 0; i < indexCount; ++i)
                {
                    if (indices[i] >= vertexCount)
                    {
                        throw new IndexOutOfBoundsException("indexBuffer.getIndices()["
                                + i + "] >= vertexCount");
                    }
                }
            }
            else
            {
                final int firstIndex = indexBuffer.getFirstIndex();
                if (firstIndex >= vertexCount)
                {
                    throw new IndexOutOfBoundsException("indexBuffer.getFirstIndex()"
                            + " >= vertexCount");
                }
                if (indexBuffer instanceof TriangleStripArray)
                {
                    TriangleStripArray tsa = (TriangleStripArray) indexBuffer;
                    //check to see if the implicit indices go over the vertex count
                    int sum = 0;
                    for (int l : tsa.getStripLengths())
                    {
                        sum += l;
                    }
                    if ((firstIndex + sum) > vertexCount)
                    {
                        throw new IndexOutOfBoundsException("implicit indices will"
                                + " exceed vertexCount");
                    }
                }
            }
        }
        getSubMesh(index).setIndexBuffer(indexBuffer);
    }

    public void setSubmeshCount(int submeshCount)
    {
        if (submeshCount < 1)
        {
            throw new IllegalArgumentException("submeshCount < 1");
        }

        this.subMeshes = new SubMesh[submeshCount];
        for (int i = 0; i < submeshCount; ++i)
        {
            this.subMeshes[i] = new SubMesh();
        }
    }

    public void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        if (vertexBuffer == null)
        {
            throw new NullPointerException("vertexBuffer is null");
        }
        this.vertexBuffer = vertexBuffer;
    }
}
