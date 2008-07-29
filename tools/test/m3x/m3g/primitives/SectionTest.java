package m3x.m3g.primitives;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.primitives.Section;

public class SectionTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization1()
  {
    final int N = 1024;
    byte[] object = new byte[N];
    for (int i = 0; i < N; i++)
    {
      object[i] = (byte)i;
    }
    Section section = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, object);
                                                                    
    try
    {   
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      this.doTestAccessors(section, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  

  public void testSerializationAndDeserialization2()
  {
    M3GSerializable[] objects = {this.getAnimationTracks()[0], this.getMatrix()};                                                                    
    try
    {   
      Section section = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, objects, null);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      this.doTestAccessors(section, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  

  public void testSerializationAndDeserialization3()
  {
    M3GSerializable[] objects = this.getAnimationTracks();                                                                    
    try
    {   
      Section section = new Section(Section.COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, objects, null);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
