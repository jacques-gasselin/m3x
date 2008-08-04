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
import m3x.m3g.primitives.ObjectChunk;
import m3x.m3g.primitives.Section;

/**
 * Models a M3G file.
 * 
 * @author jsaarinen
 */
public class M3GObject implements M3GSerializable
{
  private final static String M3G_VERSION = "1.0";
  
  private M3GTypedObject[] m3gObjects;
  private Header header;
  
  public M3GObject(M3GTypedObject[] objects)
  {
    this.m3gObjects = objects;
  }
  
  public M3GObject()
  {
    super();
  }

  public void serialize(DataOutputStream dataOutputStream, String version)
      throws IOException
  {    
    // serialize file id.
    FileIdentifier fileIdentifier = new FileIdentifier();
    fileIdentifier.serialize(dataOutputStream, M3G_VERSION);
    
    // serialize M3G objects into a temp buffer so that the file
    // total length can be calculated
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream sectionDataOutputStream = new DataOutputStream(baos);
    for (M3GTypedObject typedObject : this.m3gObjects)
    {
      ObjectChunk objectChunk = M3GSupport.wrapSerializableToObjectChunk(typedObject);
      objectChunk.serialize(sectionDataOutputStream, M3G_VERSION);
    }
    sectionDataOutputStream.close();
    byte[] sectionByteArray = baos.toByteArray();
    int totalFileLength = sectionByteArray.length + FileIdentifier.LENGTH + Header.LENGTH;
    
    // create header
    this.header = new Header(false, totalFileLength, totalFileLength);
    byte[] headerBytes = M3GSupport.objectToBytes(this.header);
    Section headerSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, headerBytes);
    
    // serialize header
    headerSection.serialize(dataOutputStream, M3G_VERSION);
    
    // serialize all the rest objects into the same section, compressed
    Section objectsSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, sectionByteArray);
    objectsSection.serialize(dataOutputStream, M3G_VERSION);
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion) throws IOException, FileFormatException
  {
    FileIdentifier fileIdentifier = new FileIdentifier();
    fileIdentifier.deserialize(dataInputStream, M3G_VERSION);

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
    System.out.println(object.getHeader());
    for (M3GTypedObject typedObject : object.getObjects())
    {
      System.out.println(typedObject);
    }
  }
}