package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import m3x.m3g.objects.*;
import m3x.m3g.primitives.Section;

/**
 * Model a deserialized M3G file.
 * 
 * @author jsaarinen
 */
public class M3GObject implements M3GSerializable
{
  private final static String M3G_VERSION = "1.0";
  
  private FileIdentifier fileIdentifier;
  private Header header;
  private M3GTypedObject[] m3gObjects;
  
  public M3GObject(Header header,
      M3GTypedObject[] objects)
  {
    this.fileIdentifier = new FileIdentifier();
    this.header = header;
    this.m3gObjects = objects;
  }
  
  public M3GObject()
  {
    super();
  }

  public void serialize(DataOutputStream dataOutputStream, String version)
      throws IOException
  {    
    this.fileIdentifier.serialize(dataOutputStream, M3G_VERSION);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream sectionDataOutputStream = new DataOutputStream(baos);
    byte[] headerByteArray = baos.toByteArray();
    Section headerSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, headerByteArray);
    headerSection.serialize(dataOutputStream, M3G_VERSION);
    sectionDataOutputStream.close();
    
    // serialize M3G objects into the same Section
    baos = new ByteArrayOutputStream();
    sectionDataOutputStream = new DataOutputStream(baos);
    for (M3GTypedObject object : this.m3gObjects)
    {
      object.serialize(sectionDataOutputStream, M3G_VERSION);
    }
    sectionDataOutputStream.close();
    byte[] objectsByteArray = baos.toByteArray();
    Section m3gObjectsSection = new Section(Section.COMPRESSION_SCHEME_ZLIB_32K_COMPRESSED_ADLER32, objectsByteArray);
    m3gObjectsSection.serialize(dataOutputStream, M3G_VERSION);
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion) throws IOException, FileFormatException
  {
    this.fileIdentifier = new FileIdentifier();
    this.fileIdentifier.deserialize(dataInputStream, M3G_VERSION);

    Section headerSection = new Section();
    headerSection.deserialize(dataInputStream, M3G_VERSION);
    byte[] headerObject = headerSection.getObjects();
    ObjectChunk objectChunk = new ObjectChunk();
    DataInputStream objectInputStream = new DataInputStream(new ByteArrayInputStream(headerObject));
    objectChunk.deserialize(objectInputStream, M3G_VERSION);
    byte objectType = objectChunk.getObjectType();
    if (objectType != ObjectTypes.OBJECT_HEADER)
    {
      throw new FileFormatException("First section is not object header!");
    }
    try
    {
      this.header = (Header)M3GObjectFactory.getInstance(objectType);
    }
    catch (M3GException e)
    {
      throw new FileFormatException(e);
    }
    byte[] objectData = objectChunk.getData();
    DataInputStream chunkInputStream = new DataInputStream(new ByteArrayInputStream(objectData));
    this.header.deserialize(chunkInputStream, M3G_VERSION);
    chunkInputStream.close();
      
    List<M3GTypedObject> objects = new ArrayList<M3GTypedObject>();
    while (true)
    {
      try
      {
        // there can be N sections, each containing 1..M M3G objects
        objectInputStream = loadSection(dataInputStream);

        while (true)
        {
          // try to read next object chunk from the Section
          try
          {
            objectChunk = new ObjectChunk();
            objectChunk.deserialize(objectInputStream, M3G_VERSION);
            objectType = objectChunk.getObjectType();
            M3GTypedObject object;
            try
            {
              object = M3GObjectFactory.getInstance(objectType);
            }
            catch (M3GException e)
            {
              throw new FileFormatException(e);
            }
            objectData = objectChunk.getData();
            chunkInputStream = new DataInputStream(new ByteArrayInputStream(objectData));
            object.deserialize(chunkInputStream, M3G_VERSION);
            chunkInputStream.close();
            objects.add(object);
          }
          catch (EOFException e)
          {
            // the Section ended, read next section
            objectInputStream.close();
            break;
          }
        }
      }
      catch (EOFException e)
      {
        // end of stream, quit parsing
        dataInputStream.close();
        break;
      }
    }
    this.m3gObjects = objects.toArray(new M3GTypedObject[objects.size()]);
  }

  private DataInputStream loadSection(DataInputStream dataInputStream)
      throws IOException, FileFormatException
  {
    DataInputStream objectInputStream;
    Section section = new Section();
    section.deserialize(dataInputStream, M3G_VERSION);
    objectInputStream = new DataInputStream(new ByteArrayInputStream(section.getObjects()));
    return objectInputStream;
  }
  
  public FileIdentifier getFileIdentifier()
  {
    return this.fileIdentifier;
  }

  public Header getHeader()
  {
    return this.header;
  }

  public M3GTypedObject[] getObjects()
  {
    return this.m3gObjects;
  }
  
  public static void main(String[] args) throws Exception
  {
    M3GObject object = new M3GObject();
    DataInputStream dataInputStream = new DataInputStream(new FileInputStream("tools/test/data/teapot.m3g"));
    object.deserialize(dataInputStream, M3G_VERSION);
    System.out.println(object.getFileIdentifier());
    System.out.println(object.getHeader());
    for (M3GTypedObject typedObject : object.getObjects())
    {
      System.out.println(typedObject);
    }
  }
}
