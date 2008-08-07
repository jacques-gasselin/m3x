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
  public final static String M3G_VERSION = "1.0";
  
  private Section[] sections;
  
  public M3GObject(Section[] objects)
  {
    this.sections = objects;
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
    for (Section section : this.sections)
    {
      section.serialize(sectionDataOutputStream, M3G_VERSION);
    }
    sectionDataOutputStream.close();
    int sectionsLength = sectionDataOutputStream.size();
    int totalFileLength = FileIdentifier.LENGTH + Header.LENGTH + sectionsLength;
    
    // create header
    Header header = new Header(false, totalFileLength, totalFileLength);
    byte[] headerObjectBytes = M3GSupport.objectToBytes(header);
    ObjectChunk headerObjectChunk = new ObjectChunk(ObjectTypes.OBJECT_HEADER, headerObjectBytes);    
    Section headerSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, new ObjectChunk[] { headerObjectChunk });
    
    // serialize header
    headerSection.serialize(dataOutputStream, M3G_VERSION);
    
    // serialize Sections
    for (Section section : this.sections)
    {
      section.serialize(dataOutputStream, M3G_VERSION);
    }
  }
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion) throws IOException, FileFormatException
  {
    // read file identifier
    FileIdentifier fileIdentifier = new FileIdentifier();
    fileIdentifier.deserialize(dataInputStream, M3G_VERSION);

    // read header
    Section headerSection = new Section();
    headerSection.deserialize(dataInputStream, M3G_VERSION);
  
    ObjectChunk objectChunk = headerSection.getObjects()[0];
    byte objectType = objectChunk.getObjectType();
    if (objectType != ObjectTypes.OBJECT_HEADER)
    {
      throw new FileFormatException("First section is not object header!");
    }
    Header header;
    try
    {
      header = (Header)M3GObjectFactory.getInstance(objectType);
    }
    catch (M3GException e)
    {
      throw new FileFormatException(e);
    }
    byte[] objectData = objectChunk.getData();
    DataInputStream chunkInputStream = new DataInputStream(new ByteArrayInputStream(objectData));
    header.deserialize(chunkInputStream, M3G_VERSION);
    chunkInputStream.close();
    
    // deserialize other sections
    List<Section> sections = new ArrayList<Section>();
    
    while (true)
    {
      try
      {
        // there can be N sections, each containing 1..M M3G objects
        Section section = new Section();
        section.deserialize(dataInputStream, M3G_VERSION);
        sections.add(section);
      }
      catch (EOFException e)
      {
        // end of stream, quit parsing
        dataInputStream.close();
        break;
      }
    }
    this.sections = sections.toArray(new Section[sections.size()]);
  }

  public Section[] getObjects()
  {
    return this.sections;
  }
  
  public static void main(String[] args) throws Exception
  {
    M3GObject object = new M3GObject();
    DataInputStream dataInputStream = new DataInputStream(new FileInputStream("tools/test/data/teapot.m3g"));
    object.deserialize(dataInputStream, M3G_VERSION);
    for (Section section : object.getObjects())
    {
      System.out.println(section);
    }
    DataOutputStream dataOutputStream = new DataOutputStream(new ByteArrayOutputStream());
    object.serialize(dataOutputStream, M3G_VERSION);
  }
}
