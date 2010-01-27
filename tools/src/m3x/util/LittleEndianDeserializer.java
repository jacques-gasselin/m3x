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

package m3x.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

/**
 *
 * @author jgasseli
 */
public abstract class LittleEndianDeserializer implements DataInput
{
    private Stack<LittleEndianDataInputStream> inputStreams;

    public LittleEndianDeserializer()
    {
        inputStreams = new Stack<LittleEndianDataInputStream>();
    }

    public final void pushInputStream(InputStream stream)
    {
        inputStreams.push(new LittleEndianDataInputStream(stream));
    }

    public final void popInputStream()
    {
        inputStreams.pop();
    }

    public final LittleEndianDataInputStream getInputStream()
    {
        return inputStreams.peek();
    }

    public void readFully(byte[] b) throws IOException
    {
        getInputStream().readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException
    {
        getInputStream().readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException
    {
        return getInputStream().skipBytes(n);
    }

    public boolean readBoolean() throws IOException
    {
        return getInputStream().readBoolean();
    }

    public byte readByte() throws IOException
    {
        return getInputStream().readByte();
    }

    public int readUnsignedByte() throws IOException
    {
        return getInputStream().readUnsignedByte();
    }

    public short readShort() throws IOException
    {
        return getInputStream().readShort();
    }

    public int readUnsignedShort() throws IOException
    {
        return getInputStream().readUnsignedShort();
    }

    public char readChar() throws IOException
    {
        return getInputStream().readChar();
    }

    public int readInt() throws IOException
    {
        return getInputStream().readInt();
    }

    public long readLong() throws IOException
    {
        return getInputStream().readLong();
    }

    public float readFloat() throws IOException
    {
        return getInputStream().readFloat();
    }

    public double readDouble() throws IOException
    {
        return getInputStream().readDouble();
    }

    public String readLine() throws IOException
    {
        return getInputStream().readLine();
    }

    public String readUTF() throws IOException
    {
        return getInputStream().readUTF();
    }

    public String readUTF8() throws IOException
    {
        return getInputStream().readUTF8();
    }
}
