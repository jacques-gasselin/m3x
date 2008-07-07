package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;

public class ExternalReference implements M3GTypedObject
{
  private String uri;

  public ExternalReference(String uri)
  {
    this.uri = uri;
  }

  public ExternalReference()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    this.uri = M3GSupport.readUTF8(dataInputStream);
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    dataOutputStream.write(this.uri.getBytes("UTF-8"));
    dataOutputStream.write('\0');
  }

  public byte getObjectType()
  {
    return ObjectTypes.EXTERNAL_REFERENCE;
  }

  public String getUri()
  {
    return this.uri;
  }
}
