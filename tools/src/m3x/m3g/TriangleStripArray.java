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

package m3x.m3g;

import m3x.m3g.primitives.SectionSerializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

/**
 * The TriangleStripArray class mimics the JSR184 class definition with some
 * minor changes to allow control over serialization parameters. The binary
 * encoding can be changed and the value checks for strip lengths and indices
 * are tighter than in JSR184. This is to increase the likelyhood of generating
 * valid m3g 1.0 binary data.
 *
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
public class TriangleStripArray extends IndexBuffer implements SectionSerializable
{
    private int[] stripLengths;


    /**
     * Creates a new TriangleStripArray object ready to populate with data.
     * The encoding defaults to SHORT and IMPLICIT.
     */
    public TriangleStripArray()
    {
        super(ENCODING_SHORT);
    }

    /**
     * Creates a TriangleStripArray with implicit indices. The encoding defaults
     * to SHORT and IMPLICIT.
     *
     * @param firstIndex the index to start the implicit indices from.
     * @param stripLengths the array of strip lengths used to create indices from.
     */
    public TriangleStripArray(int firstIndex, int[] stripLengths)
    {
        super(ENCODING_SHORT);
        setFirstIndex(firstIndex);
        setStripLengths(stripLengths);
        //verify that all indices are valid
        int sum = 0;
        for (int l : stripLengths)
        {
            sum += l;
        }
        if ((firstIndex + sum) > 65536)
        {
            throw new IndexOutOfBoundsException("firstIndex + sum(stripLengths) > 65536");
        }
    }

    /**
     * Creates a TriangleStripArray with explicit indices. The encoding defaults
     * to SHORT and EXPLICIT.
     * 
     * @param indices the explicit indices to use
     * @param stripLengths the array of strip lengths to use.
     */
    public TriangleStripArray(int[] indices, int[] stripLengths)
    {
        super(ENCODING_EXPLICIT | ENCODING_SHORT);
        setIndices(indices);
        setStripLengths(stripLengths);
        //verify that all indices are valid
        int sum = 0;
        for (int l : stripLengths)
        {
            sum += l;
        }
        if (indices.length < sum)
        {
            throw new IllegalArgumentException("indices.length < sum(stripLengths)");
        }
    }

    @Override
    public void deserialise(Deserializer deserialiser)
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
    public void serialise(Serializer serialiser)
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

    /**
     * Sets the triangle strip lengths. The values are copied in once they are
     * verified to be valid. If not; an exception is thrown and the previously
     * set indices are preserved.
     *
     * @param stripLengths
     * @throws NullPointerException if stripLengths is null
     * @throws IllegalArgumentException if stripLengths is empty
     * @throws IllegalArgumentException if any of the lengths < 3
     */
    public void setStripLengths(int[] stripLengths)
    {
        if (stripLengths == null)
        {
            throw new NullPointerException("stripLengths is null");
        }
        final int length = stripLengths.length;
        if (length == 0)
        {
            throw new IllegalArgumentException("stripLengths is empty");
        }
        for (int i = 0; i < length; ++i)
        {
            if (stripLengths[i] < 3)
            {
                throw new IllegalArgumentException("stripLengths[" + i + "] is < 3");
            }
        }
        //copy in the values
        this.stripLengths = new int[length];
        System.arraycopy(stripLengths, 0, this.stripLengths, 0, length);
    }
}
