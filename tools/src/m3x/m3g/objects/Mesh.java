package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class Mesh extends Node implements M3GTypedObject
{
  public class SubMesh implements M3GSerializable
  {
    public ObjectIndex indexBuffer;
    public ObjectIndex appearance;

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
      this.indexBuffer.deserialize(dataInputStream, m3gVersion);
      this.appearance.deserialize(dataInputStream, m3gVersion);
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
      this.indexBuffer.serialize(dataOutputStream, m3gVersion);
      this.appearance.serialize(dataOutputStream, m3gVersion);
    }
  }

  private final ObjectIndex vertexBuffer;
  private final int submeshCount;
  private final SubMesh[] subMeshes;

  public Mesh(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex vertexBuffer, SubMesh[] subMeshes)
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
    assert (subMeshes != null);
    assert (subMeshes.length > 0);
    this.vertexBuffer = vertexBuffer;
    this.subMeshes = subMeshes;
    this.submeshCount = subMeshes.length;
  }
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.vertexBuffer.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeInt(dataOutputStream, this.submeshCount);
    for (SubMesh subMesh : this.subMeshes)
    {
      subMesh.serialize(dataOutputStream, m3gVersion);
    }
  }

  public byte getObjectType()
  {
    return ObjectTypes.MESH;
  }
}
