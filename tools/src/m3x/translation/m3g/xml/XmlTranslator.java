package m3x.translation.m3g.xml;

import java.util.Hashtable;
import m3x.translation.m3g.Translator;

/**
 *
 * @author jgasseli
 */
public class XmlTranslator extends Translator
{
    Hashtable<Class, Class> converterMap;

    public XmlTranslator()
    {
        converterMap = new Hashtable<Class, Class>();

        //add supported class converters
        converterMap.put(m3x.xml.AnimationController.class,
                AnimationControllerConverter.class);
        converterMap.put(m3x.xml.AnimationTrack.class,
                AnimationTrackConverter.class);
    }


    public m3x.m3g.Object3D getObject(m3x.xml.Object3D key)
    {
        return super.getObject((java.lang.Object)key);
    }

    protected void setObject(m3x.xml.Object3D key, m3x.m3g.Object3D value)
    {
        super.setObject(key, value);
    }

    @Override
    protected Class getConverterClass(Class objectClass)
    {
        return converterMap.get(objectClass);
    }

}
