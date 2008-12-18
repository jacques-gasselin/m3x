package m3x.translation;

/**
 *
 * @author jgasseli
 */
public interface XmlToBinaryConverter
{
    /**
     * Convert to an XML object.
     *
     * @return an XML representation
     */
    public void toBinary(Translator translator, m3x.xml.Object3D from);
}
