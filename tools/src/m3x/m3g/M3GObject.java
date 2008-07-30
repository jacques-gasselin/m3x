package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
public class M3GObject
{
  private final static String M3G_VERSION = "1.0";
  
  private final FileIdentifier fileIdentifier;
  private final Header header;
  private M3GTypedObject[] m3gObjects;
  
  public M3GObject(InputStream inputStream) throws IOException, M3GException
  {
    DataInputStream dataInputStream = new DataInputStream(inputStream);
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
      throw new M3GException("First section is not object header!");
    }
    this.header = (Header)M3GObjectFactory.getInstance(objectType);
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
        Section section = new Section();
        section.deserialize(dataInputStream, M3G_VERSION);
        objectInputStream = new DataInputStream(new ByteArrayInputStream(section.getObjects()));

        while (true)
        {
          // try to read next object chunk from the Section
          try
          {
            objectChunk = new ObjectChunk();
            objectChunk.deserialize(objectInputStream, M3G_VERSION);
            objectType = objectChunk.getObjectType();
            M3GTypedObject object = M3GObjectFactory.getInstance(objectType);
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
    M3GObject object = new M3GObject(new FileInputStream("teapot.m3g"));
    System.out.println(object.getFileIdentifier());
    System.out.println(object.getHeader());
    for (M3GTypedObject typedObject : object.getObjects())
    {
      System.out.println(typedObject);
    }
  }
}
