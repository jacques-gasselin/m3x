package m3x.m3g.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;

/**
 *
 * @author jgasseli
 */
public class Adler32FilterInputStream extends FilterInputStream
{
    private final Adler32 adler;

    public Adler32FilterInputStream(Adler32 adler, InputStream in)
    {
        super(in);
        this.adler = adler;
    }

    @Override
    public int read() throws IOException
    {
        final int value = super.read();
        if (value != -1)
        {
            adler.update(value);
        }
        return value;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        final int nread = super.read(b);
        if (nread > 0)
        {
            adler.update(b, 0, nread);
        }
        return nread;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        final int nread = super.read(b, off, len);
        if (nread > 0)
        {
            adler.update(b, off, nread);
        }
        return nread;
    }
}
