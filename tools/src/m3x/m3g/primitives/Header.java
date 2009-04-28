/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    /**Sets the version for the m3g binary file.
     * The format of the versionstring must be MAJOR.MINOR
     * With both MAJOR and MINOR being single digits.
     * For this version of the m3g binary format, MAJOR
     * must equal '1', MINOR must equal '0'.
     * @param version
     */
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
        final int major = Character.digit(version.charAt(0), 10);
        final int minor = Character.digit(version.charAt(2), 10);
        setVersion(major, minor);
    }

    public void setVersion(int major, int minor)
    {
        if (major != 1)
        {
            throw new IllegalArgumentException("major revision must be 1");
        }
        if (minor != 0)
        {
            throw new IllegalArgumentException("minor revision must be = 0");
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
