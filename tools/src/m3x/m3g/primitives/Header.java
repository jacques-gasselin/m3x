package m3x.m3g.primitives;

import java.io.IOException;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

/**
 * See http://java2me.org/m3g/file-format.html#Header<br>
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public class Header implements SectionSerialisable
{  
    private boolean hasExternalReferences;
    private int totalFileSize;
    private int approximateContentSize;
    private byte[] version = new byte[2];
    private String authoringInformation;

    public Header()
    {
        super();
        setVersion(1, 0);
    }

    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        deserialiser.readFully(this.version);
        this.hasExternalReferences = deserialiser.readBoolean();
        this.totalFileSize = deserialiser.readInt();
        this.approximateContentSize = deserialiser.readInt();
        this.authoringInformation = deserialiser.readUTF8();
    }

    public void serialise(Serialiser serialiser)
        throws IOException
    {
        serialiser.write(this.version);
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

    public void setTotalFileSize(int totalFileSize)
    {
        this.totalFileSize = totalFileSize;
    }

    public int getApproximateContentSize()
    {
        return this.approximateContentSize;
    }

    public void setApproximateContentSize(int contentSize)
    {
        this.approximateContentSize = contentSize;
    }

    public String getVersion()
    {
        return this.version[0] + "." + this.version[1];
    }

    public void setVersion(String version)
    {
        if (version.length() != 3)
        {
            throw new IllegalArgumentException("String of format B.B expected");
        }
        if (!Character.isDigit(version.charAt(0)))
        {
            throw new IllegalArgumentException("Major revision is not a digit");
        }
        if (version.charAt(1) != '.')
        {
            throw new IllegalArgumentException("String of format B.B expected");
        }
        if (!Character.isDigit(version.charAt(2)))
        {
            throw new IllegalArgumentException("Minor revision is not a digit");
        }
        //Assume format is B.B
        this.version[0] = (byte) Character.digit(version.charAt(0), 10);
        this.version[1] = (byte) Character.digit(version.charAt(2), 10);
    }

    public void setVersion(int major, int minor)
    {
        if (major < 1)
        {
            throw new IllegalArgumentException("major revision must be >= 1");
        }
        if (minor < 0)
        {
            throw new IllegalArgumentException("minor revision must be >= 0");
        }
        this.version[0] = (byte) major;
        this.version[1] = (byte) minor;
    }

    public String getAuthoringInformation()
    {
        return this.authoringInformation;
    }

    public void setAuthoringInformation(String author)
    {
        this.authoringInformation = author;
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.HEADER;
    }
}
