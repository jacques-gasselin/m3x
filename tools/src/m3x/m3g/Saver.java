package m3x.m3g;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author jgasseli
 */
public abstract class Saver
{
    private Saver()
    {
        
    }

    /**
     * Instantiates a M3GObject from a given stream.
     *
     * @param inputStream
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static void save(OutputStream outputStream, Object3D[] roots,
            String version, String author)
            throws IOException
    {
        M3GSerialiser serialiser = new M3GSerialiser(version, author);
        serialiser.writeFileIdentifier(outputStream);
        serialiser.serialise(outputStream, roots);
    }
}
