package m3x.translation;

import java.util.Hashtable;

/**
 * 
 * @author jgasseli
 */
public class Translator
{
    private Hashtable<m3x.m3g.Object3D, m3x.xml.Object3D> binaryToXmlMap;
    private Hashtable<m3x.xml.Object3D, m3x.m3g.Object3D> xmlToBinaryMap;

    private Hashtable<Class, XmlToBinaryConverter> objectClassToBinaryConverterMap;
    private Hashtable<Class, BinaryToXmlConverter> objectClassToXmlConverterMap;

    public Translator()
    {
        binaryToXmlMap = new Hashtable<m3x.m3g.Object3D, m3x.xml.Object3D>();
        xmlToBinaryMap = new Hashtable<m3x.xml.Object3D, m3x.m3g.Object3D>();
        objectClassToBinaryConverterMap = new Hashtable<Class, XmlToBinaryConverter>();
        objectClassToXmlConverterMap = new Hashtable<Class, BinaryToXmlConverter>();
    }

    public m3x.m3g.Object3D getObject(m3x.xml.Object3D key)
    {
        m3x.m3g.Object3D value = xmlToBinaryMap.get(key);
        if (value == null)
        {
            //the object does not exist yet as a mapping.
            //do a conversion to get it.
            convertObject(key);
            //The conversion is responsible for registering the object.
            value = xmlToBinaryMap.get(key);
            assert(value != null);
        }
        return value;
    }

    public m3x.xml.Object3D getObject(m3x.m3g.Object3D key)
    {
        m3x.xml.Object3D value = binaryToXmlMap.get(key);
        if (value == null)
        {
            //the object does not exist yet as a mapping.
            //do a conversion to get it.
            convertObject(key);
            //The conversion is responsible for registering the object.
            value = binaryToXmlMap.get(key);
            assert(value != null);
        }
        return value;
    }

    protected void setObject(m3x.xml.Object3D key, m3x.m3g.Object3D value)
    {
        xmlToBinaryMap.put(key, value);
    }

    protected void setObject(m3x.m3g.Object3D key, m3x.xml.Object3D value)
    {
        binaryToXmlMap.put(key, value);
    }

    private Class getConverterClass(Class objectClass)
    {
        //use reflection to get a converter
        //this depends on a 1:1 naming scheme overlap.
        //1:1 naming is a good thing to ensure in any case.
        //FIXME this will not work with obfuscation of classnames.
        String className = objectClass.getSimpleName();
        String translationPackageName = this.getClass().getPackage().getName();
        String converterClassName = translationPackageName + "." + className + "Converter";
        try
        {
            return Class.forName(converterClassName);
        }
        catch (ClassNotFoundException ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    private void convertObject(m3x.xml.Object3D key)
    {
        Class keyClass = key.getClass();
        XmlToBinaryConverter converter = objectClassToBinaryConverterMap.get(keyClass);
        if (converter == null)
        {
            try
            {
                Class converterClass = getConverterClass(keyClass);
                converter = (XmlToBinaryConverter)converterClass.newInstance();
                objectClassToBinaryConverterMap.put(keyClass, converter);
            }
            catch (InstantiationException ex)
            {
                throw new IllegalStateException(ex);
            }
            catch (IllegalAccessException ex)
            {
                throw new IllegalStateException(ex);
            }
        }
        converter.toBinary(this, key);
    }

    private void convertObject(m3x.m3g.Object3D key)
    {
        Class keyClass = key.getClass();
        BinaryToXmlConverter converter = objectClassToXmlConverterMap.get(keyClass);
        if (converter == null)
        {
            try
            {
                Class converterClass = getConverterClass(keyClass);
                converter = (BinaryToXmlConverter)converterClass.newInstance();
                objectClassToXmlConverterMap.put(keyClass, converter);
            }
            catch (InstantiationException ex)
            {
                throw new IllegalStateException(ex);
            }
            catch (IllegalAccessException ex)
            {
                throw new IllegalStateException(ex);
            }
        }
        converter.toXml(this, key);
    }
}
