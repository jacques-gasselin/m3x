package m3x.m3g;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;
import m3x.m3g.primitives.Section;
import m3x.m3g.util.LittleEndianDataOutputStream;

/**
 *
 * @author jgasseli
 */
public class M3GSerialiser
{
    private String version;
    private String author;
    private Stack<LittleEndianDataOutputStream> outputStreams;
    
    public M3GSerialiser(String version, String author)
    {
        this.version = version;
        this.author = author;

        outputStreams = new Stack<LittleEndianDataOutputStream>();
        
    }

    public void writeFileIdentifier(OutputStream out) throws IOException
    {
        out.write(FileIdentifier.BYTES);
    }

    public void serialise(OutputStream out, Object3D[] roots)
    {
        pushOutputStream(out);

        Section headerSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32);
        Section contentSection = new Section(Section.COMPRESSION_SCHEME_UNCOMPRESSED_ADLER32);

        popOutputStream();
    }

    private void popOutputStream()
    {
        outputStreams.pop();
    }

    private void pushOutputStream(OutputStream out)
    {
        outputStreams.push(new LittleEndianDataOutputStream(out));
    }

    public LittleEndianDataOutputStream getOutputStream()
    {
        return outputStreams.peek();
    }
}
