package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;

public class HeaderTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    try
    {
      Header serializedHeader = new Header(false, 666, 666);
      byte[] serialized = M3GSupport.objectToBytes(serializedHeader);
      Header deserializedHeader = (Header)M3GSupport.bytesToObject(serialized, Header.class);
      this.doTestAccessors(serializedHeader, deserializedHeader);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
}
