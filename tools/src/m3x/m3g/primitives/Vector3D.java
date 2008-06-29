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
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    this.x = M3GSupport.readFloat(dataInputStream);
    this.y = M3GSupport.readFloat(dataInputStream);
    this.z = M3GSupport.readFloat(dataInputStream);
  }


  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.x));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.y));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.z));
  }
}
