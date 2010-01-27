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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;

public class LittleEndianDataOutputStream implements DataOutput
{
    private final OutputStream originalStream;
    private DataOutputStream dos;

    private LittleEndianDataOutputStream()
    {
        super();
        originalStream = null;
    }

    public LittleEndianDataOutputStream(OutputStream out)
    {
        originalStream = out;
        dos = new DataOutputStream(out);
    }

    public void startChecksum(Adler32 adler) throws IOException
    {
        //make sure the previous data is written
        dos.flush();
        dos = new DataOutputStream(
                new CheckedOutputStream(originalStream, adler));
    }

    public void endChecksum() throws IOException
    {
        //make sure all the checksum data is written
        dos.flush();
        dos = new DataOutputStream(originalStream);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        this.dos.write(b, off, len);
    }

    public void write(byte[] b) throws IOException
    {
        this.dos.write(b);
    }

    public void write(int b) throws IOException
    {
        this.dos.write(b);
    }

    public void writeBoolean(boolean v) throws IOException
    {
        this.dos.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException
    {
        this.dos.write(v);
    }

    public void writeBytes(String s) throws IOException
    {
        this.dos.writeBytes(s);
    }

    public void writeChar(int v) throws IOException
    {
        this.dos.writeChar(v);
    }

    public void writeChars(String s) throws IOException
    {
        this.dos.writeChars(s);
    }

    public void writeDouble(double v) throws IOException
    {
        long value = Double.doubleToLongBits(v);
        this.dos.writeLong(Long.reverseBytes(value));
    }

    public void writeFloat(float v) throws IOException
    {
        int value = Float.floatToIntBits(v);
        this.dos.writeInt(Integer.reverseBytes(value));
    }

    public void writeInt(int v) throws IOException
    {
        this.dos.writeInt(Integer.reverseBytes(v));
    }

    public void writeLong(long v) throws IOException
    {
        this.dos.writeLong(Long.reverseBytes(v));
    }

    public void writeShort(int v) throws IOException
    {
        this.dos.writeShort(Short.reverseBytes((short) v));
    }

    public void writeUTF(String str) throws IOException
    {
        this.dos.writeUTF(str);
    }

    public void writeUTF8(String str) throws IOException
    {
        if (str != null)
        {
            this.dos.write(str.getBytes("UTF-8"));
        }
        this.dos.writeByte(0);
    }

    public void flush() throws IOException
    {
        this.dos.flush();
    }
}
