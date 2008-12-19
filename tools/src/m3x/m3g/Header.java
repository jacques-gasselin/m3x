package m3x.m3g;

import java.io.IOException;
import java.util.Arrays;

/**
 * See http://java2me.org/m3g/file-format.html#Header<br>
 * 
 * @author jsaarinen
 */
public class Header implements M3GTypedObject
{  
    private final static byte[] VERSION =
    {
        1, 0
    };
    private boolean hasExternalReferences;
    private int totalFileSize;
    private int approximateContentSize;
    private String authoringInformation;

    public Header(boolean hasExternalReferences, int totalFileSize,
        int approximateContentSize, String authoringInformation)
    {
        this.hasExternalReferences = hasExternalReferences;
        this.totalFileSize = totalFileSize;
        this.approximateContentSize = approximateContentSize;
        this.authoringInformation = authoringInformation;
    }

    public Header()
    {
        super();
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        byte[] version = new byte[2];
        deserialiser.readFully(version);
        if (!Arrays.equals(version, VERSION))
        {
            throw new IllegalArgumentException("Invalid M3G version!");
        }
        this.hasExternalReferences = deserialiser.readBoolean();
        this.totalFileSize = deserialiser.readInt();
        this.approximateContentSize = deserialiser.readInt();
        this.authoringInformation = deserialiser.readUTF8();
    }

    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        serialiser.write(VERSION);
        serialiser.writeBoolean(this.hasExternalReferences);
        serialiser.writeInt(this.totalFileSize);
        serialiser.writeInt(this.approximateContentSize);
        serialiser.writeUTF8(this.authoringInformation);
    }

    public boolean isHasExternalReferences()
    {
        return this.hasExternalReferences;
    }

    public int getTotalFileSize()
    {
        return this.totalFileSize;
    }

    public int getApproximateContentSize()
    {
        return this.approximateContentSize;
    }

    public String getAuthoringInformation()
    {
        return this.authoringInformation;
    }

    public int getObjectType()
    {
        return ObjectTypes.OBJECT_HEADER;
    }
}
