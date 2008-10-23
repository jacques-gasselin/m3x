package m3x.m3g;

import m3x.m3g.FileIdentifier;
import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSupport;

public class FileIdentifierTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization()
  {
    try
    {
      FileIdentifier identifier = new FileIdentifier();
      byte[] serialized = M3GSupport.objectToBytes(identifier);
      FileIdentifier deserializedHeader = (FileIdentifier)M3GSupport.bytesToObject(serialized, FileIdentifier.class);
      this.doTestAccessors(identifier, deserializedHeader);
    }
    catch (Exception e)
    {
      fail(e.getMessage());
    }
  }
}
