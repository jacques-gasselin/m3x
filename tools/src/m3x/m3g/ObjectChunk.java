package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


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

  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException
  {
    this.objectType = dataInputStream.readByte();
    this.length = M3GSupport.swapBytes(dataInputStream.readInt());
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
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.length));
    dataOutputStream.write(this.data);
  }
}
