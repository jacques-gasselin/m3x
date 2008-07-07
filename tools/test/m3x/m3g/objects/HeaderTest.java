package m3x.m3g.objects;

import junit.framework.TestCase;
import java.io.*;

public class HeaderTest extends TestCase
{
  public void testHeader()
  {
    Header serializaedHeader = new Header(false, 666, 666, "JUnit test case");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try
    {
      serializaedHeader.serialize(dos, "1.0");
      dos.close();
      byte[] serialized = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
      DataInputStream dis = new DataInputStream(bais);
      Header deserializedHeader = new Header();
      deserializedHeader.deserialize(dis, "1.0");
      dis.close();
      assertTrue(serializaedHeader.getAuthoringInformation().equals(deserializedHeader.getAuthoringInformation()));
      assertTrue(serializaedHeader.getApproximateContentSize() == deserializedHeader.getApproximateContentSize());
      assertTrue(serializaedHeader.getTotalFileSize() == deserializedHeader.getTotalFileSize());
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
}
