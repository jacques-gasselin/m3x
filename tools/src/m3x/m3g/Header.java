package m3x.m3g;

import java.io.DataOutputStream;
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
        throws IOException, FileFormatException
    {
        byte[] version = new byte[2];
        deserialiser.readFully(version);
        if (!Arrays.equals(version, VERSION))
        {
            throw new FileFormatException("Invalid M3G version!");
        }
        this.hasExternalReferences = deserialiser.readBoolean();
        this.totalFileSize = deserialiser.readInt();
        this.approximateContentSize = deserialiser.readInt();
        this.authoringInformation = deserialiser.readUTF8();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        dataOutputStream.write(VERSION);
        dataOutputStream.writeBoolean(this.hasExternalReferences);
        M3GSupport.writeInt(dataOutputStream, this.totalFileSize);
        M3GSupport.writeInt(dataOutputStream, this.approximateContentSize);
        dataOutputStream.write(this.authoringInformation.getBytes("UTF-8"));
        dataOutputStream.write('\0');
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
