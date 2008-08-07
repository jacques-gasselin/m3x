package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;


/**
 * Models a Object3D subclass (see http://www.java2me.org/m3g/file-format.html#Objects)
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
   * The actual object data.
   */
  private byte[] data;

  public ObjectChunk()
  {
    super();
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
    this.data = data;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException
  {
    this.objectType = dataInputStream.readByte();
    int length = M3GSupport.readInt(dataInputStream);
    this.data = new byte[length];
    dataInputStream.read(this.data);
  }

  /**
   * Serialization happens according to
   * http://www.java2me.org/m3g/file-format.html#ObjectStructure
   */
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(this.objectType);
    M3GSupport.writeInt(dataOutputStream, this.data.length);
    dataOutputStream.write(this.data);
  }

  public byte getObjectType()
  {
    return this.objectType;
  }

  /**
   * Returns the length of the whole object when serialized.
   * 
   * @return
   *  The length in bytes.
   */
  public int getLength()
  {
    // object length serialized is object type + object data length integer + objects
    return 1 + 4 + this.data.length;
  }

  public byte[] getData()
  {
    return this.data;
  }

  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (!(obj instanceof ObjectChunk))
    {
      return false;
    }
    ObjectChunk another = (ObjectChunk)obj;
    return this.objectType == another.objectType && Arrays.equals(this.data, another.data);
  }
}
