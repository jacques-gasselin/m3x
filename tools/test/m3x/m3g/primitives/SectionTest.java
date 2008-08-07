package m3x.m3g.primitives;

import java.io.*;
import java.math.BigInteger;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.M3GObject;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.objects.Fog;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.Section;

public class SectionTest extends AbstractTestCase
{ 
  public void testSerializationAndDeserialization1()
  {
    try
    {   
      M3GTypedObject[] objects = new M3GTypedObject[1];
      ObjectIndex[] animationTracks = getAnimationTracks();
      UserParameter[] userParameters = getUserParameters();
      ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
      Fog fog = new Fog(animationTracks,
                        userParameters,
                        color,
                        0.1f);
      objects[0] = fog;    
      ObjectChunk[] objectChunks = new ObjectChunk[objects.length];
      for (int i = 0; i < objectChunks.length; i++)
      {
        objectChunks[i] = M3GSupport.wrapSerializableToObjectChunk(objects[i]);
      }
      
      Section section = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, objectChunks);
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
    try
    {   
      M3GTypedObject[] objects = new M3GTypedObject[1];
      ObjectIndex[] animationTracks = getAnimationTracks();
      UserParameter[] userParameters = getUserParameters();
      ColorRGB color = new ColorRGB(0.1f, 0.2f, 0.3f);
      Fog fog = new Fog(animationTracks,
                        userParameters,
                        color,
                        0.1f);
      objects[0] = fog;    
      ObjectChunk[] objectChunks = new ObjectChunk[objects.length];
      for (int i = 0; i < objectChunks.length; i++)
      {
        objectChunks[i] = M3GSupport.wrapSerializableToObjectChunk(objects[i]);
      }
      
      Section section = new Section(Section.COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, objectChunks);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      System.out.println(new BigInteger(1, section.getObjects()[0].getData()).toString(16));
      System.out.println(new BigInteger(1, deserialized.getObjects()[0].getData()).toString(16));
      assertTrue(section.equals(deserialized));
      this.doTestAccessors(section, deserialized);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
