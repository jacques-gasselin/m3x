package m3x.m3g;

import m3x.m3g.primitives.Serialisable;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

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

    public Mesh(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (subMeshes != null);
        assert (subMeshes.length > 0);
        setVertexBuffer(vertexBuffer);
        this.subMeshes = subMeshes;
    }

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
        for (int i = 0; i < this.subMeshes.length; i++)
        {
            this.subMeshes[i] = new SubMesh();
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
    }

    public void setVertexBuffer(VertexBuffer vertexBuffer)
    {
        this.vertexBuffer = vertexBuffer;
    }
}
