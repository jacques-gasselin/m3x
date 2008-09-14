package m3x.translation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m3x.m3g.M3GObject;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.objects.AnimationController;
import m3x.m3g.objects.AnimationTrack;
import m3x.m3g.objects.Appearance;
import m3x.m3g.objects.Background;
import m3x.m3g.objects.Camera;
import m3x.m3g.objects.CompositingMode;
import m3x.m3g.objects.Fog;
import m3x.m3g.objects.Group;
import m3x.m3g.objects.Image2D;
import m3x.m3g.objects.KeyframeSequence;
import m3x.m3g.objects.Light;
import m3x.m3g.objects.Material;
import m3x.m3g.objects.Mesh;
import m3x.m3g.objects.MorphingMesh;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.PolygonMode;
import m3x.m3g.objects.SkinnedMesh;
import m3x.m3g.objects.Texture2D;
import m3x.m3g.objects.TriangleStripArray;
import m3x.m3g.objects.VertexBuffer;
import m3x.m3g.objects.World;
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
    translators.put(AnimationController.class, new AnimationControllerTranslator());
    translators.put(AnimationTrack.class, new AnimationTrackTranslator());
    translators.put(Appearance.class, new AppearanceTranslator());
    translators.put(Background.class, new BackgroundTranslator());
    translators.put(Camera.class, new CameraTranslator());
    translators.put(CompositingMode.class, new CompositingModeTranslator());
    translators.put(Fog.class, new FogTranslator());
    translators.put(Group.class, new GroupTranslator());
    translators.put(Image2D.class, new Image2DTranslator());
    translators.put(KeyframeSequence.class, new KeyframeSequenceTranslator());
    translators.put(Light.class, new LightTranslator());
    translators.put(Material.class, new MaterialTranslator());
    translators.put(Mesh.class, new MeshTranslator());
    translators.put(MorphingMesh.class, new MorphingMeshTranslator());
    translators.put(PolygonMode.class, new PolygonModeTranslator());
    translators.put(SkinnedMesh.class, new SkinnedMeshTranslator());
    translators.put(Texture2D.class, new Texture2DTranslator());
    translators.put(TriangleStripArray.class, new TriangleStripArrayTranslator());
    translators.put(VertexArray.class, new VertexArrayTranslator());
    translators.put(VertexBuffer.class, new VertexBufferTranslator());
    translators.put(World.class, new WorldTranslator());
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
