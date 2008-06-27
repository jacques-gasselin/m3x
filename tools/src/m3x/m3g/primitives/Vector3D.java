package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class Vector3D implements M3GSerializable
{
  private float x, y, z;

  public Vector3D(float x, float y, float z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException, FileFormatException
  {
    this.x = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
    this.y = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
    this.z = Float.intBitsToFloat(M3GSupport.swapBytes(dataInputStream.readInt()));
  }


  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.x));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.y));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.z));
  }
}
