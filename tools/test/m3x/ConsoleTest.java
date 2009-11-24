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

package m3x;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**Console test reader for the M3X tool-suite.
 * 
 * @author jgasseli
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
        Reader inputReader = null;
        if (infile != null)
        {
            try
            {
                inputReader = new FileReader(infile);
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
            inputReader = new InputStreamReader(System.in);
        }

        final m3x.xml.Deserializer deserializer = new m3x.xml.Deserializer();
        final m3x.xml.M3G root = deserializer.deserialize(inputReader);
        
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

