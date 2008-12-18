package m3x.m3g;

import m3x.m3g.util.LittleEndianDataInputStream;
import junit.framework.TestCase;
import java.io.*;

public class ExternalReferenceTest extends TestCase
{
  public void testSerializationAndDeserialization()
  {
    /*ExternalReference serialiazed = new ExternalReference("http://bla.com/bla");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try
    {
      serialiazed.serialize(dos, "1.0");
      dos.close();
      byte[] serialized = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
      LittleEndianDataInputStream dis = new LittleEndianDataInputStream(bais);
      ExternalReference deserialized = new ExternalReference();
      deserialized.deserialize(dis, "1.0");
      dis.close();
      assertTrue(serialiazed.getUri().equals(deserialized.getUri()));
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }*/
  }
}
