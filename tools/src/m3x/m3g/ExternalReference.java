package m3x.m3g;

import m3x.m3g.primitives.TypedObject;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

/**
 * See See http://java2me.org/m3g/file-format.html#ExternalReference 
 * for more information.<br>
 * @author jsaarinen
 */
public class ExternalReference implements TypedObject
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

    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        this.uri = deserialiser.readUTF8();
    }

    public void serialize(Serialiser serialiser)
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
