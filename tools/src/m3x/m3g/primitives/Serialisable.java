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

package m3x.m3g.primitives;

import java.io.IOException;
import m3x.m3g.Deserialiser;
import m3x.m3g.Serialiser;

/**
 * All M3G objects that can be serialized will implement
 * this interface for object serialization.
 * 
 * @author jsaarinen
 */
public interface Serialisable
{  
    /**
     * Constructs a M3G object from a stream.
     * The user is responsible for handling the stream.
     *
     * @param dataInputStream
     *  The stream which from the M3G object is read.
     *
     * @param m3gVersion
     *
     * @throws IOException
     * @throws FileFormatException
     *  When the input data was somehow invalid from the M3G specification point of view.
     */
    void deserialise(Deserialiser deserialiser)
        throws IOException;
  
    /**
     * Implementations of this method should output the corresponding
     * M3G object data to the output stream.
     *
     * It is up to the caller of this method to set up and clean up
     * the output stream.
     *
     * @param dataOutputStream
     *  Where data will be written to.
     *
     * @param m3gVersion TODO
     *
     * @throws IOException
     *  When something nasty happened.
     */
    void serialise(Serialiser serialiser)
        throws IOException;
}
