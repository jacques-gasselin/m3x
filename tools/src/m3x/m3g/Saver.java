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
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static void save(OutputStream outputStream, Object3D[] roots,
            String version, String author)
        throws IOException
    {
        Serialiser serialiser = new Serialiser(version, author);
        serialiser.writeFileIdentifier(outputStream);
        serialiser.serialise(outputStream, roots);
    }
}
