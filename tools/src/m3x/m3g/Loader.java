package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper class for loading/saving a M3GObject.
 * 
 * @author jsaarinen
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
    public static Object3D[] load(byte[] bytes) throws IOException, FileFormatException
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
    public static Object3D[] load(InputStream inputStream) throws IOException, FileFormatException
    {
        M3GDeserialiser deserialiser = new M3GDeserialiser();
        if (!deserialiser.verifyFileIdentifier(inputStream))
        {
            throw new FileFormatException();
        }

        deserialiser.pushInputStream(inputStream);
        deserialiser.deserialise();
        deserialiser.popInputStream();

        return deserialiser.getRootObjects();
    }

    /**
     * Saves a M3GObject to a given stream.
     *
     * @param outputStream
     * @param object
     * @throws IOException
     */
    /*public static void save(OutputStream outputStream, M3GObject object) throws IOException
    {
        DataOutputStream dos = null;
        try
        {
            dos = new DataOutputStream(outputStream);
            object.serialize(dos, M3GObject.M3G_VERSION);
        }
        finally
        {
            if (dos != null)
            {
                dos.close();
            }
        }
    }*/
}
