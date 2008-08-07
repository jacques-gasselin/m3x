package m3x.m3g.primitives;

import java.io.*;

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
    final int N = 1024;
    byte[] object = new byte[N];
    for (int i = 0; i < N; i++)
    {
      object[i] = (byte)i;
    }
    /*Section section = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, object);
                                                                    
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
    }*/
  }  

  public void testSerializationAndDeserialization2()
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
      /*
      Section section = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, objectChunks, null);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      this.doTestAccessors(section, deserialized);
    */
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
      /*
      Section section = new Section(Section.COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, objectChunks, null);
      byte[] serialized = M3GSupport.objectToBytes(section);
      Section deserialized = (Section)M3GSupport.bytesToObject(serialized, Section.class);
      System.out.println(section.getObjects().length);
      System.out.println(deserialized.getObjects().length);
      */
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }  
}
