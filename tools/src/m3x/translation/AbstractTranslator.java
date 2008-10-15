package m3x.translation;

import java.util.ArrayList;
import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.AnimationTrack;

public abstract class AbstractTranslator implements Translator
{
    private m3x.xml.Object3D xmlObject;
    private Object3D binaryObject;
    private m3x.xml.M3G xmlRootObject;
    private m3x.xml.Deserialiser xmlDeserializer;

    /**
     * Sets the values from an XML object May throw java.lang.ClassCastException -
     * if object is not of a valid type.
     *
     * @param object
     *          - the object to set from
     * @param deserialiser
     *          - the deserialiser used to resolve references.
     */
    public void set(m3x.xml.Object3D object,
            m3x.xml.M3G root,
            m3x.xml.Deserialiser deserialiser)
    {
        this.setXmlObject(object);
        this.setXmlRootObject(root);
        this.setXmlDeserializer(deserialiser);
    }

    /**
     * Sets the values from an M3G object May throw java.lang.ClassCastException -
     * if object is not of a valid type.
     *
     * @param object
     *          - the object to set from
     */
    public void set(Object3D object)
    {
        this.setBinaryObject(object);

    }

    protected ObjectIndex[] getM3GAnimationTracks()
    {
        List<AnimationTrack> list = this.getXmlObject().getAnimationTrack();
        List<Integer> animationTracksList = new ArrayList<Integer>(list.size());
        for (AnimationTrack at : list)
        {
            int animationTrackObjectIndex = Integer.parseInt(at.getId());
            animationTracksList.add(animationTrackObjectIndex);
        }
        ObjectIndex[] animationTracks = new ObjectIndex[animationTracksList.size()];
        for (int i = 0; i < animationTracks.length; i++)
        {
            animationTracks[i] = new ObjectIndex(animationTracksList.get(i));
        }
        return animationTracks;
    }

    protected static Matrix getM3GTransformMatrix(m3x.xml.TransformableType transformable)
    {
        List<Float> list = transformable.getTransform();
        float[] matrixData = new float[16];
        for (int i = 0; i < matrixData.length; i++)
        {
            // .. hopefully list is an array..
            matrixData[i] = list.get(i);
        }
        Matrix transform = new Matrix(matrixData);
        return transform;
    }

    protected static ColorRGB translateColorRGB(List<Short> components)
    {
        byte r = components.get(0).byteValue();
        byte g = components.get(1).byteValue();
        byte b = components.get(2).byteValue();
        return new ColorRGB(r, g, b);
    }

    protected static ColorRGBA translateColorRGBA(List<Short> components)
    {
        byte r = components.get(0).byteValue();
        byte g = components.get(1).byteValue();
        byte b = components.get(2).byteValue();
        byte a = components.get(3).byteValue();
        return new ColorRGBA(r, g, b, a);
    }

    /**
     * Searches an XML element (Object3D subclass instance) with given ID.
     *
     * @param root
     *  Root of the M3X document.
     *
     * @param searchKey
     *  Element to be found
     *
     * @return
     *  Index of the found Object3D inside Sections list or -1 if not found.
     */
    protected static int searchObjectIndex(m3x.xml.M3G root, Object searchKey)
    {
        int index = 1;
        for (m3x.xml.Section section : root.getSection())
        {
            for (m3x.xml.Object3D object : section.getObjects())
            {
                if (object.equals(searchKey))
                {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    public m3x.xml.Object3D getXmlObject()
    {
        return xmlObject;
    }

    public void setXmlObject(m3x.xml.Object3D object)
    {
        this.xmlObject = object;
    }

    public Object3D getBinaryObject()
    {
        return binaryObject;
    }

    public void setBinaryObject(Object3D binaryObject)
    {
        this.binaryObject = binaryObject;
    }

    public m3x.xml.M3G getXmlRootObject()
    {
        return xmlRootObject;
    }

    public void setXmlRootObject(m3x.xml.M3G xmlRootObject)
    {
        this.xmlRootObject = xmlRootObject;
    }

    public m3x.xml.Deserialiser getXmlDeserializer()
    {
        return xmlDeserializer;
    }

    public void setXmlDeserializer(m3x.xml.Deserialiser xmlDeserializer)
    {
        this.xmlDeserializer = xmlDeserializer;
    }
}
