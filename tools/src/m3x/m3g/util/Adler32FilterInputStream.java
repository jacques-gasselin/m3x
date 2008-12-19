package m3x.m3g.util;

import java.io.FilterInputStream;
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

}
