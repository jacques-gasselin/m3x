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

import java.util.List;
import m3x.m3g.primitives.Serialisable;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.Vector3D;
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#VertexBuffer<br>
  ColorRGBA     defaultColor;<br>
  ObjectIndex   positions;<br>
  Float32[3]    positionBias;<br>
  Float32       positionScale;<br>
  ObjectIndex   normals;<br>
  ObjectIndex   colors;<br>
  UInt32        texcoordArrayCount;<br>
  FOR each texture coordinate array...<br>
       ObjectIndex   texCoords;<br>
       Float32[3]    texCoordBias;<br>
       Float32       texCoordScale;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class VertexBuffer extends Object3D implements SectionSerialisable
{
    private static class ScaleBiasedVertexArray implements Serialisable
    {

        private VertexArray array;
        private float scale;
        private final Vector3D bias = new Vector3D();

        public VertexArray getArray()
        {
            return this.array;
        }

        public float[] getBias()
        {
            float[] ret = new float[3];
            ret[0] = bias.getX();
            ret[1] = bias.getY();
            ret[2] = bias.getZ();
            return ret;
        }

        public float getScale()
        {
            return this.scale;
        }

        public void setArray(VertexArray array)
        {
            if (array == null)
            {
                throw new NullPointerException("array is null");
            }
            this.array = array;
        }

        public void setBias(float[] bias)
        {
            if (bias == null)
            {
                bias = new float[3];
            }
            if (bias.length != 3)
            {
                throw new IllegalArgumentException("bias does not have 3 components");
            }
            this.bias.set(bias[0], bias[1], bias[2]);
        }

        public void setScale(float scale)
        {
            this.scale = scale;
        }

        public void deserialise(Deserialiser deserialiser)
            throws IOException
        {
            setArray((VertexArray)deserialiser.readReference());
            bias.deserialise(deserialiser);
            setScale(deserialiser.readFloat());
        }

        public void serialise(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getArray());
            bias.serialise(serialiser);
            serialiser.writeFloat(getScale());
        }
    }

    private ColorRGBA defaultColor;
    private ScaleBiasedVertexArray positions;
    private VertexArray normals;
    private VertexArray colors;
    private ScaleBiasedVertexArray[] textureCoordinates;

    public VertexBuffer()
    {
        super();
        this.defaultColor = new ColorRGBA();
        this.positions = new ScaleBiasedVertexArray();
        setDefaultColor(0xffffffff);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.defaultColor.deserialise(deserialiser);
        this.positions.deserialise(deserialiser);
        this.normals = (VertexArray)deserialiser.readReference();
        this.colors = (VertexArray)deserialiser.readReference();
        final int textureCoordinateArrayCount = deserialiser.readInt();
        setTexCoordCount(textureCoordinateArrayCount);
        for (int i = 0; i < textureCoordinateArrayCount; ++i)
        {
            this.textureCoordinates[i] = new ScaleBiasedVertexArray();
            this.textureCoordinates[i].deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.defaultColor.serialise(serialiser);
        this.positions.serialise(serialiser);
        serialiser.writeReference(getNormals());
        serialiser.writeReference(getColors());
        final int textureCoordinateArrayCount = getTexCoordCount();
        serialiser.writeInt(textureCoordinateArrayCount);
        for (int i = 0; i < textureCoordinateArrayCount; ++i)
        {
            this.textureCoordinates[i].serialise(serialiser);
        }
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getPositionsArray());
        queue.add(getNormals());
        queue.add(getColors());
        final int textureCoordinateArrayCount = getTexCoordCount();
        for (int i = 0; i < textureCoordinateArrayCount; ++i)
        {
            queue.add(getTexCoordsArray(i));
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.VERTEX_BUFFER;
    }

    public ColorRGBA getDefaultColor()
    {
        return this.defaultColor;
    }

    public VertexArray getPositionsArray()
    {
        return this.positions.getArray();
    }

    public float[] getPositionsBias()
    {
        return this.positions.getBias();
    }

    public float getPositionsScale()
    {
        return this.positions.getScale();
    }

    public VertexArray getNormals()
    {
        return this.normals;
    }

    public VertexArray getColors()
    {
        return this.colors;
    }

    public int getTexCoordCount()
    {
        if (this.textureCoordinates == null)
        {
            return 0;
        }
        return this.textureCoordinates.length;
    }

    public VertexArray getTexCoordsArray(int index)
    {
        return this.textureCoordinates[index].getArray();
    }

    public float[] getTexCoordsBias(int index)
    {
        return this.textureCoordinates[index].getBias();
    }

    public float getTexCoordsScale(int index)
    {
        return this.textureCoordinates[index].getScale();
    }

    public void setColors(VertexArray va)
    {
        this.colors = va;
    }

    public void setNormals(VertexArray va)
    {
        this.normals = va;
    }

    public void setPositions(VertexArray va, float scale, float[] bias)
    {
        this.positions.setArray(va);
        this.positions.setScale(scale);
        this.positions.setBias(bias);
    }

    public void setTexCoordCount(int texCoordCount)
    {
        if (texCoordCount < 0)
        {
            throw new IllegalArgumentException(
                "Invalid texture coordinate array length: "
                + texCoordCount);
        }
        this.textureCoordinates = new ScaleBiasedVertexArray[texCoordCount];
        for (int i = 0; i < texCoordCount; ++i)
        {
            this.textureCoordinates[i] = new ScaleBiasedVertexArray();
        }
    }

    public void setTexCoords(int index, VertexArray va, float scale, float[] bias)
    {
        this.textureCoordinates[index].setArray(va);
        this.textureCoordinates[index].setScale(scale);
        this.textureCoordinates[index].setBias(bias);
    }

    public void setDefaultColor(int argb)
    {
        this.defaultColor.set(argb);
    }

    public void setDefaultColor(List<Short> defaultColor)
    {
        this.defaultColor.set(defaultColor);
    }
}
