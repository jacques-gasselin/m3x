package m3x.m3g;

import java.io.IOException;

/**
 * See See http://java2me.org/m3g/file-format.html#ExternalReference 
 * for more information.<br>
 * @author jsaarinen
 */
public class ExternalReference implements M3GTypedObject
{
    private String uri;

    public ExternalReference(String uri)
    {
        this.uri = uri;
    }

    public ExternalReference()
    {
        super();
    }

    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        this.uri = deserialiser.readUTF8();
    }

    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        // .. write the string data in raw UTF-8..
        // .. and terminate it with a null byte
        serialiser.writeUTF8(this.uri);
    }

    public int getObjectType()
    {
        return ObjectTypes.EXTERNAL_REFERENCE;
    }

    public String getUri()
    {
        return this.uri;
    }
}
