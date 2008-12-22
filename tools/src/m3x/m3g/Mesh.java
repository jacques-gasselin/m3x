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
    public static class SubMesh implements Serialisable
    {

        private IndexBuffer indexBuffer;
        private Appearance appearance;

        public SubMesh()
        {
        }

        public SubMesh(IndexBuffer indexBuffer, Appearance appearance)
        {
            this.indexBuffer = indexBuffer;
            this.appearance = appearance;
        }

        public IndexBuffer getIndexBuffer()
        {
            return this.indexBuffer;
        }

        public Appearance getAppearance()
        {
            return this.appearance;
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
    private int subMeshCount;
    private SubMesh[] subMeshes;

    public Mesh(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, VertexBuffer vertexBuffer, SubMesh[] subMeshes)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        assert (subMeshes != null);
        assert (subMeshes.length > 0);
        this.vertexBuffer = vertexBuffer;
        this.subMeshes = subMeshes;
        this.subMeshCount = subMeshes.length;
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
        this.vertexBuffer = (VertexBuffer)deserialiser.readReference();
        this.subMeshCount = deserialiser.readInt();
        this.subMeshes = new SubMesh[subMeshCount];
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

    public VertexBuffer getVertexBuffer()
    {
        return this.vertexBuffer;
    }

    public int getSubmeshCount()
    {
        return this.subMeshCount;
    }

    public SubMesh[] getSubMeshes()
    {
        return this.subMeshes;
    }
}
