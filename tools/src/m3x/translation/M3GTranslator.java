package m3x.translation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m3x.m3g.M3GObject;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectChunk;
import m3x.m3g.primitives.Section;
import m3x.xml.Deserialiser;
import m3x.xml.M3G;
import m3x.xml.Object3DType;
import m3x.xml.SectionType;
import m3x.xml.VertexArray;

public class M3GTranslator
{
  private Map<Class, Translator> translators = new HashMap<Class, Translator>();

  public M3GTranslator()
  {
    translators.put(VertexArray.class, new VertexArrayTranslator());
  }

  public M3G toXML(M3GObject m3g)
  {
    return null;
  }

  public M3GObject toM3G(M3G xml, Deserialiser deserializer) throws IOException
  {
    List<SectionType> sections = xml.getSection();
    Section[] m3gSections = new Section[sections.size()];
    for (SectionType sectionType : sections)
    {
      List<Object3DType> m3xObjects = sectionType.getObjects();
      ObjectChunk[] m3gObjects = new ObjectChunk[m3xObjects.size()];
      int i = 0;
      for (Object3DType object : m3xObjects)
      {
        Translator translator = translators.get(object.getClass());
        translator.set(object, null, deserializer);
        Object3D object3d = translator.toM3G();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        object3d.serialize(new DataOutputStream(bout), "1.0");
        m3gObjects[i] = new ObjectChunk(((M3GTypedObject) object3d)
            .getObjectType(), bout.toByteArray());
      }
      Section section = new Section(
          Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32, m3gObjects);
      m3gSections[i] = section;
      i++;
    }
    M3GObject m3g = new M3GObject(m3gSections);
    return m3g;
  }
}
