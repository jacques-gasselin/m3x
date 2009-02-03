package m3x.translation.m3g;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * @author jgasseli
 */
public abstract class BinaryTranslator
{
    private Hashtable<Object, m3x.m3g.Object3D> objectToBinaryMap;
    private Hashtable<Class, BinaryConverter> objectClassToBinaryConverterMap;
    private Vector<m3x.m3g.Object3D> objects;
    private Vector<m3x.m3g.Object3D> rootObjects;

    public BinaryTranslator()
    {
        objectToBinaryMap = new Hashtable<Object, m3x.m3g.Object3D>();
        objectClassToBinaryConverterMap = new Hashtable<Class, BinaryConverter>();
        objects = new Vector<m3x.m3g.Object3D>();
        rootObjects = new Vector<m3x.m3g.Object3D>();
    }

    public Vector<m3x.m3g.Object3D> getRootVector()
    {
        return rootObjects;
    }

    protected m3x.m3g.Object3D getObject(Object key)
    {
        if (key == null)
        {
            return null;
        }
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

    protected m3x.m3g.Object3D getReference(Object key)
    {
        m3x.m3g.Object3D value = getObject(key);
        if (rootObjects.contains(value))
        {
            rootObjects.remove(value);
        }
        return value;
    }

    protected void setObject(Object key, m3x.m3g.Object3D value)
    {
        if (value == null)
        {
            throw new NullPointerException("value is null");
        }
        objectToBinaryMap.put(key, value);
        objects.add(value);
        rootObjects.add(value);
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
                if (converterClass == null)
                {
                    throw new NullPointerException("no converter class for " + key);
                }
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
        m3x.m3g.Object3D binary = converter.toBinary(this, key);
        setObject(key, binary);
    }
}
