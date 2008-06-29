package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class Header implements M3GSerializable
{
  private final static byte[] VERSION = {1, 0};
  private boolean hasExternalReferences;
  private int totalFileSize;
  private int approximateContentSize;
  private String authoringInformation;

  public Header(boolean hasExternalReferences, int totalFileSize,
      int approximateContentSize, String authoringInformation)
  {
    this.hasExternalReferences = hasExternalReferences;
    this.totalFileSize = totalFileSize;
    this.approximateContentSize = approximateContentSize;
    this.authoringInformation = authoringInformation;
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    byte[] version = new byte[2];
    dataInputStream.read(version);
    this.hasExternalReferences = dataInputStream.readBoolean();
    this.totalFileSize = M3GSupport.readInt(dataInputStream);
    this.approximateContentSize = M3GSupport.readInt(dataInputStream);
    this.authoringInformation = dataInputStream.readUTF();
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    dataOutputStream.write(VERSION);
    dataOutputStream.writeBoolean(this.hasExternalReferences);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.totalFileSize));
    dataOutputStream
        .writeInt(M3GSupport.swapBytes(this.approximateContentSize));
    dataOutputStream.write(this.authoringInformation.getBytes("UTF-8"));
    dataOutputStream.write('\0');
  }
}
