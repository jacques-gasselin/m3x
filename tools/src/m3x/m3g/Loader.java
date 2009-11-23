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

package m3x.m3g;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper class for loading/saving a M3GObject.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class Loader
{
    /**
     * Hides the constructor for a Utility class.
     */
    private Loader()
    {

    }

    /**
     * Loads the root objects from an m3g byte array.
     *
     * @param bytes an m3g byte array to deserialise from
     * @return an array containing the root objects
     * @throws IOException
     * @throws IllegalStateException if the array does not have
     * a valid file identifier.
     */
    public static Object3D[] load(byte[] bytes) throws IOException
    {
        return load(new ByteArrayInputStream(bytes));
    }

    /**
     * Loads the root objects from an m3g data stream.
     *
     * @param inputStream an m3g data stream to deserialise from
     * @return an array containing the root objects
     * @throws IOException if there is an error reading the stream
     * @throws IllegalStateException if the data stream does not have
     * a valid file identifier.
     */
    public static Object3D[] load(InputStream inputStream) throws IOException
    {
        Deserializer deserialiser = new Deserializer();
        if (!deserialiser.verifyFileIdentifier(inputStream))
        {
            throw new IllegalStateException("wrong file identifier");
        }

        deserialiser.deserialize(inputStream);

        return deserialiser.getRootObjects();
    }

}
