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

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

/**
 * See http://java2me.org/m3g/file-format.html#TriangleStripArray<br>
  Byte       encoding;
  IF encoding == 0, THEN
    UInt32        startIndex;
  ELSE IF encoding == 1, THEN
    Byte          startIndex;
  ELSE IF encoding == 2, THEN
    UInt16        startIndex;
  ELSE IF encoding == 128, THEN
    UInt32[]      indices;
  ELSE IF encoding == 129, THEN
    Byte[]        indices;
  ELSE IF encoding == 130, THEN
    UInt16[]      indices;
  END
  UInt32[]      stripLengths;

 * @author jsaarinen
 * @author jgasseli
 */
public class TriangleStripArray extends IndexBuffer implements SectionSerialisable
{
    private int[] stripLengths;

    public TriangleStripArray()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);

        final int stripLengthCount = deserialiser.readInt();
        int[] lengths = new int[stripLengthCount];
        for (int i = 0; i < stripLengthCount; i++)
        {
            lengths[i] = deserialiser.readInt();
        }
        setStripLengths(lengths);
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeInt(getStripLengthCount());
        for (int x : this.stripLengths)
        {
            serialiser.writeInt(x);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.TRIANGLE_STRIP_ARRAY;
    }

    public int getStripLengthCount()
    {
        if (this.stripLengths == null)
        {
            return 0;
        }
        return this.stripLengths.length;
    }

    public int[] getStripLengths()
    {
        return this.stripLengths;
    }

    public void setStripLengths(int[] stripLengths)
    {
        this.stripLengths = stripLengths;
    }
}
