package m3x.m3g.primitives;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GObject;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
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
    M3GSerializable[] objects = new M3GSerializable[3];
    objects[0] = this.getAnimationTracks()[0];
    objects[1] = this.getMatrix(); 
    objects[2] = this.getMatrix();                                                                    
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

  public void testCompression()
  {
    M3GSerializable[] objects = new M3GSerializable[3];
    objects[0] = this.getAnimationTracks()[0];
    objects[1] = this.getMatrix(); 
    objects[2] = this.getMatrix(); 
    try
    {   
      Section section = new Section(Section.COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, objects, null);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      System.out.println(section.getObjects().length);
      System.out.println(deserialized.getObjects().length);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
