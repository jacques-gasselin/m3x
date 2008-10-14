package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import m3x.m3g.primitives.ObjectChunk;


/**
 * This class includes support methods for swapping the byte
 * order. M3G uses little-endian byte order, so this class
 * is needed.
 * 
 * @author jsaarinen
 */
public final class M3GSupport
{
    private M3GSupport()
    {
    }

    /**
     * Converts a float from big-endian to little-endian or vice versa.
     *
     * @param x
     * @return
     *  The float as bytes swapped.
     */
    private static int swapBytes(float x)
    {
        int asInt = Float.floatToIntBits(x);
        return swapBytes(asInt);
    }

    /**
     * Converts an 32-bit integer from big-endian to little-endian or vice versa.
     *
     * @param x
     * @return
     *  The integer bytes swapped.
     */
    private static int swapBytes(int x)
    {
        int b1 = (x >> 0) & 0x000000FF;
        int b2 = (x >> 8) & 0x000000FF;
        int b3 = (x >> 16) & 0x000000FF;
        int b4 = (x >> 24) & 0x000000FF;
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | (b4 << 0);
    }

    /**
     * Converts a 16-bit integer from big-endian to little-endian or vice versa.
     *
     * @param x
     * @return
     *  The short bytes swapped.
     */
    private static short swapBytes(short x)
    {
        int b1 = (x >> 0) & 0x000000FF;
        int b2 = (x >> 8) & 0x000000FF;
        return (short) ((b1 << 8) | (b2 << 0));
    }

    /**
     * Returns an integer in little-endian bytes.
     *
     * @param x
     *  The integer.
     *
     * @return
     *  The integer bytes, LSB(yte) first.
     *
     * @throws IOException
     */
    public static byte[] intToBytes(int x) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(swapBytes(x));
        dos.close();
        return baos.toByteArray();
    }

    /**
     * Converts the M3G object to a byte array.
     *
     * @param object
     * @return
     *  The deserialized object as byte array.
     * @throws IOException
     */
    public static byte[] objectToBytes(M3GSerializable object) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        object.serialize(dos, null);
        dos.close();
        return baos.toByteArray();
    }

    /**
     * A helper method for read an integer from the stream saved as little-endian byte order.
     *
     * @param dataInputStream
     * @return
     *  The integer in big-endian.
     * @throws IOException
     */
    public static int readInt(DataInputStream dataInputStream) throws IOException
    {
        return swapBytes(dataInputStream.readInt());
    }

    /**
     * A helper method to write an integer as little-endian to the stream.
     *
     * @param dataOutputStream
     * @param x
     * @throws IOException
     */
    public static void writeInt(DataOutputStream dataOutputStream, int x) throws IOException
    {
        dataOutputStream.writeInt(swapBytes(x));
    }

    /**
     * A helper method for read a float from the stream saved as little-endian byte order.
     *
     * @param dataInputStream
     * @return
     *  The float in big-endian.
     * @throws IOException
     */
    public static float readFloat(DataInputStream dataInputStream) throws IOException
    {
        return Float.intBitsToFloat(swapBytes(dataInputStream.readInt()));
    }

    /**
     * A helper method to write a float as little-endian to the stream.
     *
     * @param dataOutputStream
     * @param x
     *  The float in big-endian.
     * @throws IOException
     */
    public static void writeFloat(DataOutputStream dataOutputStream, float x) throws IOException
    {
        dataOutputStream.writeInt(swapBytes(x));
    }

    /**
     * A helper method for writing a 16-bit integer as little-endian to the stream.
     *
     * @param dataOutputStream
     * @param x
     * @throws IOException
     */
    public static void writeShort(DataOutputStream dataOutputStream, short x) throws IOException
    {
        dataOutputStream.writeShort(swapBytes(x));
    }

    /**
     * A helper method for reading a 16-bit integer as little-endian from the stream.
     *
     * @param dataInputStream
     * @return
     *  The short in big-endian.
     */
    public static short readShort(DataInputStream dataInputStream) throws IOException
    {
        return swapBytes(dataInputStream.readShort());
    }

    /**
     * Reads an UTF-8 string terminated with \0 byte.
     *
     * @param dataInputStream
     *
     * @return
     *  The String object from stream.
     *
     * @throws IOException
     */
    public static String readUTF8(DataInputStream dataInputStream) throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        byte b;
        while ((b = dataInputStream.readByte()) != '\0') {
            buffer.append((char) b);
        }
        return buffer.toString();
    }

    /**
     * Constructs a M3G object from a byte array.
     *
     * @param serialized
     *  The bytes.
     *
     * @param clazz
     *  Class of the object in the bytes given.
     *
     * @return
     *  The object constructed from the byte array.
     * @throws Exception
     */
    public static M3GSerializable bytesToObject(byte[] serialized, Class<? extends M3GSerializable> clazz) throws Exception
    {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(serialized));
        M3GSerializable serializable = clazz.newInstance();
        serializable.deserialize(dataInputStream, null);
        dataInputStream.close();
        return serializable;
    }

    /**
     * Wraps a M3G object to a ObjectChunk.
     *
     * @param serializable
     * @return
     * @throws IOException
     */
    public static ObjectChunk wrapSerializableToObjectChunk(M3GTypedObject serializable) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        serializable.serialize(dos, null);
        dos.close();
        byte[] objectBytes = baos.toByteArray();
        ObjectChunk objectChunk = new ObjectChunk(serializable.getObjectType(), objectBytes);
        return objectChunk;
    }
    
    /*
    public static ObjectIndex[] getDepthFirstOrdering(Object3D root)
    {
    List<ObjectIndex> referenceArray = new ArrayList<ObjectIndex>();
    recurse(root, 1, referenceArray);
    return referenceArray.toArray(new ObjectIndex[referenceArray.size()]);
    }

    private static void recurse(Object3D node, int index, List<ObjectIndex> referenceArray)
    {
    Object3D[] children = node.getChildren();
    if (children != null)
    {
    for (Object3D child : children)
    {
    recurse(child, ++index, referenceArray);
    }
    }
    referenceArray.add(new ObjectIndex(index));
    }*/
}
