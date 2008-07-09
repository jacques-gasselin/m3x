package m3x.m3g.objects;

import m3x.m3g.M3GSupport;

public class HeaderTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    try
    {
      Header serializaedHeader = new Header(false, 666, 666, "JUnit test case");
      byte[] serialized = M3GSupport.objectToBytes(serializaedHeader);
      Header deserializedHeader = (Header)M3GSupport.bytesToObject(serialized, Header.class);
      String authInfo1 = serializaedHeader.getAuthoringInformation();
      String authInfo2 = deserializedHeader.getAuthoringInformation();
      assertTrue(authInfo1.equals(authInfo2));
      assertTrue(serializaedHeader.getApproximateContentSize() == deserializedHeader.getApproximateContentSize());
      assertTrue(serializaedHeader.getTotalFileSize() == deserializedHeader.getTotalFileSize());
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
}
