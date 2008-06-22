package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.ObjectTypes;

public class ExternalReference implements M3GSerializable
{
  private final String uri;

  public ExternalReference(String uri)
  {
    this.uri = uri;
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
  {
    dataOutputStream.write(ObjectTypes.EXTERNAL_REFERENCE);
    dataOutputStream.write(this.uri.getBytes("UTF.8"));
    dataOutputStream.write('\0');
  }
}
