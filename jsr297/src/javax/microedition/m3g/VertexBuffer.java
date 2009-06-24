/*
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

package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public class VertexBuffer extends Object3D
{
    public VertexBuffer()
    {

    }

    public void commit()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getAttribute(String name, boolean signedNormalized[],
            float[] defaultValue)
    {
        throw new UnsupportedOperationException();
    }

    public int getAttributeCount()
    {
        throw new UnsupportedOperationException();
    }

    public String[] getAttributeNames()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getBoneIndices()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getBoneWeights()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getColors()
    {
        throw new UnsupportedOperationException();
    }

    public int getDefaultColor()
    {
        throw new UnsupportedOperationException();
    }

    public float getDefaultPointSize()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getNormals()
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getPositions(float[] scaleBias)
    {
        throw new UnsupportedOperationException();
    }

    public VertexArray getTexCoords(int index, float[] scaleBias)
    {
        throw new UnsupportedOperationException();
    }

    public int getVertexCount()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isMutable()
    {
        throw new UnsupportedOperationException();
    }
}
