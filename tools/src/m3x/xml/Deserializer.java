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

import java.io.InputStreamReader;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**The Deserializer class hides the complexities of binding a JAXB context
 * and unmarshalling the classes. The xml classes use refid references that
 * need to be resolved in order to make a properly referenced translation.
 *
 * @author jgasseli
 */
public final class Deserializer
{
    private static Unmarshaller unmarshaller;

    /**
     * Tries to initialize the xml unmashaller if it is not already initialized.
     * @throws NoClassDefFoundError if unable to receive a context to the m3x.xml
     * databinding schema.
     * @throws IllegalStateException if the context can not create the unmarshaller.
     */
    private static final void initializeOnce()
    {
        //skip if already initialized
        if (unmarshaller != null)
        {
            return;
        }
        
        //ensure that classpaths used to load this class are used to
        //load the clases needed by the context.
        final ClassLoader clsLoader = Deserializer.class.getClassLoader();

        JAXBContext context = null;
        try
        {
            context = JAXBContext.newInstance("m3x.xml", clsLoader);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            throw new NoClassDefFoundError("Unable to parse the m3x schema. " +
                    "The m3x.xml classes may be missing from your distribution.");
        }

        try
        {
            unmarshaller = context.createUnmarshaller();
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            throw new IllegalStateException("Unable to create unmarshaller. " +
                    "The ObjectFactory class may be missing in the m3x.xml package");
        }

        //set the validation handler
        try
        {
            unmarshaller.setEventHandler(new ValidationHandler());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            unmarshaller = null;
            throw new IllegalStateException(
                "can't set validation handler");
        }

        //set a validating schema
        if (true)
        {
            Schema validatingSchema = unmarshaller.getSchema();
            if (validatingSchema == null)
            {
                //load it from the packaged schema
                SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Source schemaSource = new StreamSource(
                    m3x.xml.M3G.class.getResourceAsStream("m3x.xsd"));
                try
                {
                    validatingSchema = schemaFactory.newSchema(schemaSource);
                    unmarshaller.setSchema(validatingSchema);
                }
                catch (SAXException e)
                {
                    e.printStackTrace();
                    throw new IllegalStateException(
                        "unable to load the m3x schema");
                }
            }
        }
    }

    /**The JAXB unsmahaller that is responsible for converting
     * an XML document into m3x.xml classes.
     */
    public Deserializer()
    {
        initializeOnce();
    }

    public static final class ValidationException extends RuntimeException
    {
        private final int line;
        private final int column;
        
        private ValidationException(String message)
        {
            super(message);
            this.line = 0;
            this.column = 0;
        }

        private ValidationException(int line, int column, String message)
        {
            super("Line " + line + ", Col " + column + " : " + message);
            this.line = line;
            this.column = column;
        }
        
        public int getColumn()
        {
            return column;
        }

        public int getLine()
        {
            return line;
        }
    }

    private static class ValidationHandler implements ValidationEventHandler
    {
        public boolean handleEvent(ValidationEvent event)
        {
            ValidationEventLocator locator = event.getLocator();
            final int offset = locator.getOffset();
            if (offset != -1)
            {
                throw new ValidationException("Byte offset " + offset 
                    + ": " + event.getMessage());

            }
            final int line = locator.getLineNumber();
            final int column = locator.getColumnNumber();
            if (line != -1)
            {
                throw new ValidationException(line, column, event.getMessage());
            }
            throw new ValidationException(event.getMessage());
        }
    }

    /**Deserialise an input stream that contains an XML document.
     * 
     * @param stream - the input stream to read from.
     * @return - the root M3G object
     * @throws IllegalArgumentException - if the stream is unable to be read
     * or the sections are empty
     * @throws ValidationException if the stream is invalid
     * @throws NullPointerException if stream is null
     */
    public m3x.xml.M3G deserialize(java.io.InputStream stream)
    {
        if (stream == null)
        {
            throw new NullPointerException("stream is null");
        }

        return deserialize(new InputStreamReader(stream));
    }

    /**Deserialise an input stream that contains an XML document.
     *
     * @param reader the reader to read from.
     * @return the root M3G object
     * @throws IllegalArgumentException if the reader is unable to be read
     * or the sections are empty
     * @throws ValidationException if the reader is invalid
     * @throws NullPointerException if reader is null
     */
    public m3x.xml.M3G deserialize(java.io.Reader reader)
    {
        if (reader == null)
        {
            throw new NullPointerException("reader is null");
        }
        
        try
        {
            return (m3x.xml.M3G) unmarshaller.unmarshal(reader);
        }
        catch (UnmarshalException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof SAXParseException)
            {
                SAXParseException saxe = (SAXParseException) cause;
                throw new ValidationException(
                    saxe.getLineNumber(), saxe.getColumnNumber(),
                    saxe.getMessage());
            }
            throw new IllegalArgumentException("stream is not valid", cause);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            throw new AssertionError(
                "unmarshaller should only throw UnmarshalException");
        }
    }
}
