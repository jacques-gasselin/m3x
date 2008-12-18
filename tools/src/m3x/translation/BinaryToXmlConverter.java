package m3x.translation;

/**
 *
 * @author jgasseli
 */
public interface BinaryToXmlConverter
{
    /**
     * Convert to an XML object.
     *
     * @return an XML representation
     */
    public void toXml(Translator translator, m3x.m3g.Object3D from);
}
