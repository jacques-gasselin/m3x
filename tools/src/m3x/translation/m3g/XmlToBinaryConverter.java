package m3x.translation.m3g;

/**
 *
 * @author jgasseli
 */
public abstract class XmlToBinaryConverter implements BinaryConverter
{
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
