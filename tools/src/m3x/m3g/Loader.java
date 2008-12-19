package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper class for loading/saving a M3GObject.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class Loader
{
    /**
     * Hides the constructor for a Utility class.
     */
    private Loader()
    {

    }

    /**
     * Instantiates a M3GObject from a given byte array.
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static Object3D[] load(byte[] bytes) throws IOException
    {
        return load(new ByteArrayInputStream(bytes));
    }

    /**
     * Instantiates a M3GObject from a given stream.
     *
     * @param inputStream
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static Object3D[] load(InputStream inputStream) throws IOException
    {
        Deserialiser deserialiser = new Deserialiser();
        if (!deserialiser.verifyFileIdentifier(inputStream))
        {
            throw new IllegalStateException("wrong file identifier");
        }

        deserialiser.deserialise(inputStream);

        return deserialiser.getRootObjects();
    }

}
