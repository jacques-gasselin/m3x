package m3x.m3g.primitives;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;

public class ColorRGB implements M3GSerializable
{
  private byte r, g, b;

  public ColorRGB(byte r, byte g, byte b)
  {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public ColorRGB(float r, float g, float b)
  {
    this.r = (byte)(r * 255.0f + 0.5f);
    this.g = (byte)(g * 255.0f + 0.5f);
    this.b = (byte)(b * 255.0f + 0.5f);
  }
  
  @Override
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(this.r);
    dataOutputStream.write(this.g);
    dataOutputStream.write(this.b);
  }
}