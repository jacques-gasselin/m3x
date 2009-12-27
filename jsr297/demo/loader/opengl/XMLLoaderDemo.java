/*
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

package loader.opengl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import m3x.m3g.Object3D;
import m3x.translation.m3g.XmlToBinaryTranslator;
import util.DemoFrame;

/**
 * @author jgasseli
 */
public class XMLLoaderDemo extends AbstractLoaderDemo
{
    private static final class Saver extends Thread
    {
        private final OutputStream stream;
        private final Object3D[] binRoots;

        Saver(OutputStream stream, m3x.m3g.Object3D[] binRoots)
        {
            this.stream = stream;
            this.binRoots = binRoots;
        }

        @Override
        public void run()
        {
            try
            {
                m3x.m3g.Saver.save(stream, binRoots, "1.0", null);
                stream.flush();
                stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void loadXML(Reader reader)
    {
        try
        {
            //deserialise XML stream
            final m3x.xml.Deserializer xmlDeserializer = new m3x.xml.Deserializer();
            final m3x.xml.M3G xmlRoot = xmlDeserializer.deserialize(reader);
            //convert to binary
            final m3x.m3g.Object3D[] binRoots = XmlToBinaryTranslator.convertRoot(
                    xmlRoot);
            final PipedOutputStream outstream = new PipedOutputStream();
            final PipedInputStream instream = new PipedInputStream(outstream);
            //save to pipe
            Saver saver = new Saver(outstream, binRoots);
            saver.start();
            //allow the conversion to get a head start
            try
            {
                Thread.sleep(5);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //load from the pipe
            canvas.load(instream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public XMLLoaderDemo(String resourceName)
    {
        super();
        loadXML(new InputStreamReader(
                getClass().getResourceAsStream(resourceName)));
    }

    public static void main(String[] args)
    {
        DemoFrame frame = new XMLLoaderDemo("model1.m3x");
        frame.present(false);
    }
}
