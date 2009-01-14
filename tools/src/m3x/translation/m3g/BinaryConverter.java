package m3x.translation.m3g;

/**
 *
 * @author jgasseli
 */
public interface BinaryConverter
{
    /**
     * Convert to an M3G binary object.
     *
     * @return an M3G object representation
     */
    public m3x.m3g.Object3D toBinary(BinaryTranslator translator, Object from);
}
