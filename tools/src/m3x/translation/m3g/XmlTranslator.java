package m3x.translation.m3g;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import m3x.translation.m3g.xml.AnimationControllerConverter;
import m3x.translation.m3g.xml.AnimationTrackConverter;

/**
 *
 * @author jgasseli
 */
public class XmlTranslator extends Translator
{
    private Map<Class, Class> converterMap;
    private String version;

    public XmlTranslator(String version)
    {
        converterMap = new Hashtable<Class, Class>();
        this.version = version;

        //add supported class converters
        converterMap.put(m3x.xml.AnimationController.class,
                AnimationControllerConverter.class);
        converterMap.put(m3x.xml.AnimationTrack.class,
                AnimationTrackConverter.class);
    }

    public static m3x.m3g.Object3D[] convertRoot(m3x.xml.M3G root)
    {
        XmlTranslator translator = new XmlTranslator(root.getVersion());
        for (m3x.xml.Section section : root.getSection())
        {
            for (m3x.xml.Object3D object : section.getObjects())
            {
                translator.getObject(object);
            }
        }
        Vector<m3x.m3g.Object3D> rootVector = translator.getRootVector();
        return rootVector.toArray(null);
    }

    public m3x.m3g.Object3D getObject(m3x.xml.Object3D key)
    {
        return super.getObject((java.lang.Object)key);
    }

    public m3x.m3g.Object3D getReference(m3x.xml.Object3D key)
    {
        return super.getReference((java.lang.Object)key);
    }

    public void setObject(m3x.xml.Object3D key, m3x.m3g.Object3D value)
    {
        super.setObject(key, value);
    }

    @Override
    protected Class getConverterClass(Class objectClass)
    {
        return converterMap.get(objectClass);
    }

}
