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

package m3x.md2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import m3x.util.LittleEndianDeserializer;


/**
 *
 * @author jgasseli
 */
public final class Deserialiser extends LittleEndianDeserializer
{
    private MD2 rootObject;

    public Deserialiser()
    {
        super();
        rootObject = new MD2();
    }

    /**
     * Checks the stream for the correct file identifier.
     * Do this before actually deserialising the stream.
     * @param in
     * @return true if the file identifier is valid.
     * @throws java.io.IOException
     */
    public boolean verifyFileIdentifier(InputStream in)
        throws IOException
    {
        //compare the bytes at the current position to the
        //FileIdentifier constant.
        final byte[] bytes = MD2.MAGIC;
        byte[] fileIdentifier = new byte[bytes.length];
        int pos = 0;
        while (pos < fileIdentifier.length)
        {
            int nread = in.read(fileIdentifier, pos, fileIdentifier.length - pos);
            if (nread == -1)
            {
                //EOF
                throw new EOFException();
            }
            pos += nread;
        }
        return Arrays.equals(fileIdentifier, bytes);
    }

    public void deserialise(InputStream stream) throws IOException
    {
        pushInputStream(stream);

        rootObject.deserialise(this);

        popInputStream();
    }
}
