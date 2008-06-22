package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;

public class Header implements M3GSerializable
{
  private final static byte[] version = {1, 0};
  private final boolean hasExternalReferences;
  private final int totalFileSize;
  private final int approximateContentSize;
  private final String authoringInformation;
  
  public Header(boolean hasExternalReferences, int totalFileSize,
      int approximateContentSize, String authoringInformation)
  {
    this.hasExternalReferences = hasExternalReferences;
    this.totalFileSize = totalFileSize;
    this.approximateContentSize = approximateContentSize;
    this.authoringInformation = authoringInformation;
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(version);
    dataOutputStream.writeBoolean(this.hasExternalReferences);
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.totalFileSize));
    dataOutputStream.writeInt(M3GSupport.swapBytes(this.approximateContentSize));
    dataOutputStream.write(this.authoringInformation.getBytes("UTF-8"));
    dataOutputStream.write('\0');
  }
}
