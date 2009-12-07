/**
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

package m3x.m3g.primitives;

import java.io.IOException;
import m3x.m3g.Deserializer;
import m3x.m3g.Serializer;

/**
 * All M3G objects that can be serialized will implement
 * this interface for object serialization.
 * 
 * @author jsaarinen
 * @author jgasseli
 */
public interface Serializable
{  
    /**
     * Deserialises an M3G object. The desrialiser object takes care of
     * checksums, compression and endianess.
     * Implementing classes are responsible for reading the correct
     * values from the deserialiser.
     *
     * @param deserializer The object used to abstract reading values from
     * an m3g data source.
     *
     * @throws IOException
     */
    void deserialise(Deserializer deserializer)
        throws IOException;
  
    /**
     * Implementations of this method should output the corresponding
     * M3G object data to the output stream.
     *
     * It is up to the caller of this method to set up and clean up
     * the output stream.
     *
     * @param serializer the serializer to write with.
     * @throws IOException if serializing fails on IO.
     */
    void serialise(Serializer serializer)
        throws IOException;
}
