package m3x.m3g.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Adler32;

/**
 *
 * @author jgasseli
 */
public class Adler32FilterOutputStream extends FilterOutputStream
{
    private final Adler32 adler;
    
    public Adler32FilterOutputStream(Adler32 adler, OutputStream out)
    {
        super(out);
        this.adler = adler;
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        super.write(b);
        adler.update(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        super.write(b, off, len);
        adler.update(b, off, len);
    }

    @Override
    public void write(int b) throws IOException
    {
        super.write(b);
        adler.update(b);
    }
    
}
