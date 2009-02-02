package m3x.translation.m3g;

/**
 *
 * @author jgasseli
 */
public abstract class XmlToBinaryConverter implements BinaryConverter
{
    @SuppressWarnings("unchecked")
    public static final <T extends m3x.xml.Object3D, I extends m3x.xml.InstanceType>
    T getObjectOrInstance(T object, I instance)
    {
        if (object != null)
        {
            return object;
        }
        if (instance == null)
        {
            return null;
        }
        return (T) instance.getRef();
    }


    public final m3x.m3g.Object3D toBinary(BinaryTranslator translator, Object from)
    {
        return toBinary((XmlToBinaryTranslator)translator, (m3x.xml.Object3D)from);
    }

    /**
     * Convert to an M3G binary object.
     *
     * @return an M3G object representation
     */
    public abstract m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from);

}
