package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

/**
 * See See http://java2me.org/m3g/file-format.html#ExternalReference 
 * for more information.<br>
 * @author jsaarinen
 * @author jgasseli
 */
public class ExternalReference implements SectionSerialisable
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

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        this.uri = deserialiser.readUTF8();
    }

    public void serialise(Serialiser serialiser)
        throws IOException
    {
        // .. write the string data in raw UTF-8..
        // .. and terminate it with a null byte
        serialiser.writeUTF8(this.uri);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.EXTERNAL_REFERENCE;
    }

    public String getUri()
    {
        return this.uri;
    }
}
