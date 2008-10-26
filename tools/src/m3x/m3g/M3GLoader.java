package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import m3x.m3g.util.LittleEndianDataInputStream;

/**
 * A wrapper class for loading/saving a M3GObject.
 * 
 * @author jsaarinen
 */
public abstract class M3GLoader
{
    /**
     * Hides the constructor for a Utility class.
     */
    private M3GLoader()
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
    public static M3GObject load(byte[] bytes) throws IOException, FileFormatException
    {
        LittleEndianDataInputStream dis = null;
        try
        {
            dis = new LittleEndianDataInputStream(new ByteArrayInputStream(bytes));
            M3GObject object = new M3GObject();
            object.deserialize(dis, M3GObject.M3G_VERSION);
            return object;
        }
        finally
        {
            if (dis != null)
            {
                dis.close();
            }
        }
    }

    /**
     * Instantiates a M3GObject from a given stream.
     *
     * @param inputStream
     * @return
     * @throws IOException
     * @throws FileFormatException
     */
    public static M3GObject load(InputStream inputStream) throws IOException, FileFormatException
    {
        LittleEndianDataInputStream dis = null;
        try
        {
            dis = new LittleEndianDataInputStream(inputStream);
            M3GObject object = new M3GObject();
            object.deserialize(dis, M3GObject.M3G_VERSION);
            return object;
        }
        finally
        {
            if (dis != null)
            {
                dis.close();
            }
        }
    }

    /**
     * Saves a M3GObject to a given stream.
     *
     * @param outputStream
     * @param object
     * @throws IOException
     */
    public static void save(OutputStream outputStream, M3GObject object) throws IOException
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
    }
}
