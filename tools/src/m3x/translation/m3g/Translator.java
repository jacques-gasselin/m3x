package m3x.translation.m3g;

import java.util.Hashtable;

/**
 * 
 * @author jgasseli
 */
public abstract class Translator
{
    private Hashtable<Object, m3x.m3g.Object3D> objectToBinaryMap;
    private Hashtable<Class, BinaryConverter> objectClassToBinaryConverterMap;

    public Translator()
    {
        objectToBinaryMap = new Hashtable<Object, m3x.m3g.Object3D>();
        objectClassToBinaryConverterMap = new Hashtable<Class, BinaryConverter>();
    }

    protected m3x.m3g.Object3D getObject(Object key)
    {
        m3x.m3g.Object3D value = objectToBinaryMap.get(key);
        if (value == null)
        {
            //the object does not exist yet as a mapping.
            //do a conversion to get it.
            convertObject(key);
            //The conversion is responsible for registering the object.
            value = objectToBinaryMap.get(key);
            assert(value != null);
        }
        return value;
    }

    protected void setObject(Object key, m3x.m3g.Object3D value)
    {
        objectToBinaryMap.put(key, value);
    }

    protected abstract Class getConverterClass(Class objectClass);

    private void convertObject(Object key)
    {
        Class keyClass = key.getClass();
        BinaryConverter converter = objectClassToBinaryConverterMap.get(keyClass);
        if (converter == null)
        {
            try
            {
                Class converterClass = getConverterClass(keyClass);
                converter = (BinaryConverter)converterClass.newInstance();
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
}
