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

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author jgasseli
 */
public class MD2 implements Serialisable
{
    private int textureWidth;
    private int textureHeight;
    private int vertexCount;

    private List<VertexArray> frames;
    private TriangleArray triangles;
    private List<String> textureFiles;
    private TextureCoordinateArray texcoords;

    protected static final byte[] MAGIC = {'I', 'D', 'P', '2'};
    
    public MD2()
    {
        frames = new Vector<VertexArray>();
        textureFiles = new Vector<String>();
    }

    private final void updateVertexCount()
    {
        //fast exit for zero size
        if (frames.size() == 0)
        {
            vertexCount = 0;
            return;
        }

        final int count = frames.get(0).getVertexCount();
        for (VertexArray frame : frames)
        {
            if (frame.getVertexCount() != count)
            {
                throw new IllegalStateException(
                        "different sized vertex frames are not allowed");
            }
        }
    }

    public final int getVertexCount()
    {
        updateVertexCount();
        
        return vertexCount;
    }

    public void deserialise(Deserialiser deserialiser) throws IOException
    {
        //read in the header data
        final int version = deserialiser.readInt();
        if (version != 8)
        {
            throw new IllegalArgumentException("MD2 version must be 8");
        }

        textureWidth = deserialiser.readInt();
        textureHeight = deserialiser.readInt();

        final int frameByteSize = deserialiser.readInt();
        final int textureCount = deserialiser.readInt();
        vertexCount = deserialiser.readInt();

        final int textureCoordCount = deserialiser.readInt();
        final int triangleCount = deserialiser.readInt();

        //ignored here, only really useful on old GL implementations.
        final int glCommandCount = deserialiser.readInt();

        final int frameCount = deserialiser.readInt();

        //TODO read the rest of the structure
        throw new UnsupportedOperationException("not complete yet");
    }
}
