package m3x;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**Console test reader for the M3X tool-suite.
 * 
 * @author Jacques Gasselin de Richebourg
 */
public final class ConsoleTest extends Object
{
    private m3x.xml.M3G xmlRoot = null;
    
    /**Creates a ConsoleTest instance using the XML bindings.
     * 
     * @param root - the root of the XML document
     * @param deserialiser - the object that resolves the references of the XML.
     */
    private ConsoleTest(m3x.xml.M3G root)
    {
        super();
        
        xmlRoot = root;
    }
    
    /**Convenience function for printing to the screen.
     * 
     * @param s - the message to print.
     */
    private static final void print(final String s)
    {
        System.out.println(s);
    }
    
    /**Prints a brief usage message for the ConsoleTest.
     * 
     */
    private static final void printUsage()
    {
        print("M3X<->M3G format conversion tool");
        print("usage : M3X [-f m3g|m3g] [-o outfile] [infile]");
        print("- if outfile is omitted the application will write to stdout.");
        print("- if infile is omitted the application read from stdin.");
    }

    /**Parses command line options and arguments.
     * 
     * @param args - the command line arguments
     * @throws java.lang.IllegalArgumentException - if any option is unparsable
     * @return an initialised ConsoleTest instance
     */
    private static final ConsoleTest parseArgs(final String[] args)
            throws IllegalArgumentException
    {
        String infile = null;
        String outfile = null;
        String format = null;
        
        //parse the commandline arguments
        for (int i = 0; i < args.length; ++i)
        {
            final String option = args[i];
            if (option.equals("-f"))
            {
                //the next option is the format type
                try
                {
                    format = args[++i];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    //end of arg list
                    throw new IllegalArgumentException("-f requires an argument");
                }
                if (format.startsWith("-"))
                {
                    //probably another option flag
                    throw new IllegalArgumentException("-f requires an argument");
                }
                if (!(format.equals("m3x") || format.equals("m3g")))
                {
                    //unknown format
                    throw new IllegalArgumentException("unknown format; only m3x, m3g supported");
                }
            }
            else if (option.equals("-o"))
            {
                //the next option is the output file
                try
                { 
                    outfile = args[++i];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    //end of arg list
                    throw new IllegalArgumentException("-o requires an argument");
                }
                if (outfile.startsWith("-"))
                {
                    //probably another option flag
                    throw new IllegalArgumentException("-o requires an argument");
                }
            }
            else
            {
                if (!option.startsWith("-"))
                {
                    infile = option;
                }
                else
                {
                    //probably another option flag
                    throw new IllegalArgumentException("unknown option; " + option);
                }
            }
        }
        
        //process the file input option
        InputStream inStream = null;
        if (infile != null)
        {
            try
            {
                inStream = new FileInputStream(infile);
            }
            catch (FileNotFoundException e)
            {
                print("infile is not a valid file: " + e.getMessage());
                return null;
            }
        }
        else
        {
            //no input file given, use system in.
            inStream = System.in;
        }

        final m3x.xml.M3G root = m3x.xml.Deserialiser.deserialise(inStream);
        
        return new ConsoleTest(root);
    }
    
    /**Entry function for M3X
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            printUsage();
            return;
        }
        
        ConsoleTest testClass = null;
        try
        {
            testClass = parseArgs(args);
        }
        catch (IllegalArgumentException e)
        {
            print("parse error: " + e.getMessage());
        }
    }

}

