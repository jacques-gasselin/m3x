package m3x.m3g.primitives;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;

public class ColorRGBA extends ColorRGB implements M3GSerializable
{
  private byte a;
  
  public ColorRGBA(byte r, byte g, byte b, byte a)
  {
    super(r, g, b);
    this.a = a;
  }

  public ColorRGBA(float r, float g, float b, float a)
  {
    super(r, g, b);
    this.a = (byte)(a * 255.0f + 0.5f);
  }
  
  public ColorRGBA()
  {
    super();
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (!(obj instanceof ColorRGBA))
    {
      return false;
    }
    return super.equals(obj) && this.a == ((ColorRGBA)obj).a;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.a = dataInputStream.readByte();
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.a);
  }

  public byte getA()
  {
    return this.a;
  }
}
