package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;


/**
 * Models a Object3D (see http://www.java2me.org/m3g/file-format.html#Objects)
 * structure inside Section.objects byte array. There can be several objects
 * inside one section.
 * 
 * @author jsaarinen
 */
public class ObjectChunk implements M3GSerializable
{
  /**
   * Object type, enumerated in ObjectTypes.
   */
  private byte objectType;
  
  /**
   * Length of the this.data;
   */
  private int length;
  
  /**
   * The actual object data.
   */
  private byte[] data;

  public ObjectChunk()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * 
   * @param objectType
   * @param data
   */
  public ObjectChunk(byte objectType, byte[] data)
  {
    assert(data != null);
    this.objectType = objectType;
    this.length = data.length;
    this.data = data;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException
  {
    this.objectType = dataInputStream.readByte();
    this.length = M3GSupport.readInt(dataInputStream);
    this.data = new byte[this.length];
    dataInputStream.read(this.data);
  }

  /**
   * Serialization happens according to
   * http://www.java2me.org/m3g/file-format.html#ObjectStructure
   */
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(this.objectType);
    M3GSupport.writeInt(dataOutputStream, this.length);
    dataOutputStream.write(this.data);
  }

  public byte getObjectType()
  {
    return this.objectType;
  }

  public int getLength()
  {
    return this.length;
  }

  public byte[] getData()
  {
    return this.data;
  }
}
