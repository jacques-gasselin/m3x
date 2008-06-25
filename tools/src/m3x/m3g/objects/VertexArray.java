package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ObjectIndex;

public class VertexArray extends Object3D implements M3GTypedObject
{
  private final int componentSize;
  private final int componentCount;
  private final int encoding;
  private final byte[] byteComponentsOrDeltas;
  private final short[] shortComponentsOrDeltas;
   
  public VertexArray(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, byte[] byteComponentsOrDeltas, boolean isDeltas)
  {
    super(animationTracks, userParameters);
    assert(byteComponentsOrDeltas != null);
    this.componentSize = 1;
    this.componentCount = byteComponentsOrDeltas.length;
    this.encoding = isDeltas ? 1 : 0;
    this.byteComponentsOrDeltas = byteComponentsOrDeltas;
    this.shortComponentsOrDeltas = null;
  }

  public VertexArray(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, short[] shortComponentsOrDeltas, boolean isDeltas)
  {
    super(animationTracks, userParameters);
    assert(shortComponentsOrDeltas != null);
    this.componentSize = 2;
    this.componentCount = shortComponentsOrDeltas.length;
    this.encoding = isDeltas ? 1 : 0;
    this.byteComponentsOrDeltas = null;
    this.shortComponentsOrDeltas = shortComponentsOrDeltas;
  }
  
  @Override
  public void serialize(DataOutputStream dataOutputStream, String version)
      throws IOException
  {
    super.serialize(dataOutputStream, version);
    dataOutputStream.write(this.componentSize);
    dataOutputStream.write(this.componentCount);
    dataOutputStream.write(this.encoding);
    switch (this.encoding)
    {
      case 1:
        for (byte b : this.byteComponentsOrDeltas)
        {
          dataOutputStream.write(b);
        }
        break;

      case 2:
        for (short s : this.shortComponentsOrDeltas)
        {
          dataOutputStream.writeShort(M3GSupport.swapBytes(s));
        }
        break;
     
      default:
        throw new IOException("Invalid component size: " + this.componentSize);
    }
  }

  @Override
  public byte getObjectType()
  {
    return ObjectTypes.VERTEX_ARRAY;
  }
}
