package m3x.m3g;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;
import m3x.m3g.util.LittleEndianDataInputStream;

/**
 *
 * @author jgasseli
 */
public class M3GDeserialiser
{
    private Vector<Object3D> rootObjects;
    private Vector<Object3D> objects;
    
    
    public M3GDeserialiser()
    {
    }

    public boolean verifyFileIdentifier(DataInputStream dataInput)
        throws IOException
    {
        //compare the bytes at the current position to the
        //FileIdentifier constant.
        final byte[] BYTES = FileIdentifier.BYTES;
        byte[] fileIdentifier = new byte[BYTES.length];
        dataInput.readFully(fileIdentifier);
        return Arrays.equals(fileIdentifier, BYTES);
    }

}
