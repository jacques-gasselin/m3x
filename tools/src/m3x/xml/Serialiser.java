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

package m3x.xml;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

/**The Serialiser class hides the complexities of binding a JAXB context
 * and marshalling the classes. 
 * This is a reversed version of the Deserialiser class that was written earlier.
 *
 * @author imsteve
 * @author jgasseli
 */
public class Serialiser
{
    /**The JAXB mashaller that is responsible for converting
     * an XML document into m3x.xml classes.
     */
    private Marshaller xmlMarshaller = null;

    /**Creates a new Serialiser that is bound to the m3x.xml JAXB context.
     * @throws NoClassDefFoundError - if unable to bind the xml schema
     * @throws IllegalArgumentException - if unable to create a marshaller
     */
    public Serialiser()
    {
        //create the unmarshaller
        JAXBContext context = null;
        try
        {
            context = JAXBContext.newInstance("m3x.xml");
        }
        catch (JAXBException e)
        {
            throw new NoClassDefFoundError("unable to bind schema: " + e.getMessage());
        }
        
        try
        {
            xmlMarshaller = context.createMarshaller();
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to create marshaller: " + e.getMessage());
        }
    }
    
    /**Serialise an M3G object and write it to the output stream given.
     * 
     * @param object - the object to write.
     * @param stream - the output stream to write to.
     * @param format - whether the output should be formatted.
     * @throws NullPointerException - if object is null
     * @throws NullPointerException - if stream is null
     * @throws IllegalArgumentException - if there is an error in serialising
     * the object to the stream
     */
    public void serialise(m3x.xml.M3G object, OutputStream stream, boolean formatted)
    {
        if (object == null)
        {
            throw new NullPointerException("m3g object is null");
        }
        if (stream == null)
        {
            throw new NullPointerException("stream is null");
        }
        try
        {
            xmlMarshaller.setProperty("jaxb.formatted.output", new Boolean(formatted));
            xmlMarshaller.marshal(object, stream);
        }
        catch (JAXBException e)
        {
            throw new IllegalArgumentException("unable to write outfile: " + e.toString());
        }       
    }
}
