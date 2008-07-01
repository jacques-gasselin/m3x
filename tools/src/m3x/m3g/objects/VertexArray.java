package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

public class VertexArray extends Object3D implements M3GTypedObject
{
  private final int componentSize;
  private final int componentCount;
  private final int encoding;
  private final byte[] byteComponentsOrDeltas;
  private final short[] shortComponentsOrDeltas;

  /**
   * 
   * @param animationTracks
   * @param userParameters
   * @param byteComponentsOrDeltas
   * @param isDeltas
   *          If true, this constructor is given deltas in the previous
   *          parameter, i.e. the user of this class must calculate them.
   */
  public VertexArray(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, byte[] byteComponentsOrDeltas,
      boolean isDeltas)
  {
    super(animationTracks, userParameters);
    assert (byteComponentsOrDeltas != null);
    this.componentSize = 1;
    this.componentCount = byteComponentsOrDeltas.length;
    this.encoding = isDeltas ? 1 : 0;
    this.byteComponentsOrDeltas = byteComponentsOrDeltas;
    this.shortComponentsOrDeltas = null;
  }

  /**
   * 
   * @param animationTracks
   * @param userParameters
   * @param shortComponentsOrDeltas
   * @param isDeltas
   *          If true, this constructor is given deltas in the previous
   *          parameter, i.e. the user of this class must calculate them.
   */
  public VertexArray(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, short[] shortComponentsOrDeltas,
      boolean isDeltas)
  {
    super(animationTracks, userParameters);
    assert (shortComponentsOrDeltas != null);
    this.componentSize = 2;
    this.componentCount = shortComponentsOrDeltas.length;
    this.encoding = isDeltas ? 1 : 0;
    this.byteComponentsOrDeltas = null;
    this.shortComponentsOrDeltas = shortComponentsOrDeltas;
  }
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.componentSize);
    dataOutputStream.write(this.componentCount);
    dataOutputStream.write(this.encoding);
    switch (this.componentSize)
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
          M3GSupport.writeShort(dataOutputStream, s);
        }
        break;

      default:
        throw new IOException("Invalid component size: " + this.componentSize);
    }
  }

  public byte getObjectType()
  {
    return ObjectTypes.VERTEX_ARRAY;
  }

  public int getComponentSize()
  {
    return this.componentSize;
  }

  public int getComponentCount()
  {
    return this.componentCount;
  }

  public int getEncoding()
  {
    return this.encoding;
  }

  public byte[] getByteComponentsOrDeltas()
  {
    return this.byteComponentsOrDeltas;
  }

  public short[] getShortComponentsOrDeltas()
  {
    return this.shortComponentsOrDeltas;
  }
}
