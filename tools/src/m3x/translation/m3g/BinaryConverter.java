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
    public void toBinary(Translator translator, Object from);
}
