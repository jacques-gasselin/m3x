/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A utility class for saving m3g binary root objects and their references to an
 * m3g binary file.
 * 
 * @author jgasseli
 */
public abstract class Saver
{
    private Saver()
    {
        
    }

    /**
     * Writes the root objects, roots, to an outputstream. This is the reverse
     * funtionality of Loader.load
     * 
     * @param outputStream the stream to write to
     * @param roots the root objects to save.
     * @param version the version of the file, must be a valid format version.
     * @param author the author of the file, or null.
     * @throws IOException if there was an error writing to {@code outputStream}
     * @see Loader#load(java.io.InputStream)
     */
    public static void save(OutputStream outputStream, Object3D[] roots,
            String version, String author)
        throws IOException
    {
        Serializer serialiser = new Serializer(version, author);
        serialiser.writeFileIdentifier(outputStream);
        serialiser.serialize(outputStream, roots);
    }
}
