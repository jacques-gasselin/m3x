package m3x.translation.m3g;

/**
 *
 * @author jgasseli
 */
public interface XmlToBinaryConverter extends BinaryConverter
{
    /**
     * Convert to an M3G binary object.
     *
     * @return an M3G object representation
     */
    public void toBinary(XmlTranslator translator, m3x.xml.Object3D from);

}
