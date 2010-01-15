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

import java.util.List;
import m3x.m3g.primitives.Serializable;
import m3x.m3g.primitives.SectionSerializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;
import java.util.IdentityHashMap;

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
public class VertexBuffer extends Object3D implements SectionSerializable
{

    private static final class VertexCounter
    {
        private int count;
        private final IdentityHashMap<VertexArray, VertexArray> arrays =
                new IdentityHashMap<VertexArray, VertexArray>();

        private VertexCounter()
        {

        }

        private int getCount()
        {
            return this.count;
        }

        private void replace(VertexArray oldVA, VertexArray newVA)
        {
            remove(oldVA);
            add(newVA);
        }

        private void remove(VertexArray va)
        {
            //no-op for null arrays
            if (va == null)
            {
                return;
            }

            //is this vertex array in the buffer?
            if (this.arrays.remove(va) == null)
            {
                throw new IllegalStateException("va does not belong to the counter");
            }
            if (this.count == 0)
            {
                throw new IllegalStateException("va was never counted");
            }
            if (this.count != va.getVertexCount())
            {
                throw new IllegalStateException("va.getVertexCount() != getVertexCount()");
            }

            //is the counter empty now?
            if (this.arrays.size() == 0)
            {
                this.count = 0;
            }
        }

        private void add(VertexArray va)
        {
            //no-op for null arrays
            if (va == null)
            {
                return;
            }

            //is this vertex array in the buffer?
            if (this.arrays.containsKey(va))
            {
                throw new IllegalStateException("va already belongs to the counter."
                        + this.arrays);
            }
            final int vaCount = va.getVertexCount();
            //is it the first vertex array?
            if (this.count == 0)
            {
                if (this.arrays.size() != 0)
                {
                    throw new IllegalStateException("current count is incorrect");
                }

                this.count = vaCount;
            }
            else if (this.count != vaCount)
            {
                throw new IllegalArgumentException("va.getVertexCount() != getVertexCount()");
            }

            this.arrays.put(va, va);
        }
    }

    private static final class ScaleBiasedVertexArray implements Serializable
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
            return bias.get();
        }

        public float getScale()
        {
            return this.scale;
        }

        private static final void requireArrayNotNull(VertexArray array)
        {
            if (array == null)
            {
                throw new NullPointerException("array is null");
            }
        }

        private static final void requireValidBias(float[] bias, VertexArray array)
        {
            if (array == null || bias == null)
            {
                return;
            }

            if (bias.length < Math.min(3, array.getComponentCount()))
            {
                throw new IllegalArgumentException("bias.length < min(3, array.getComponentCount())");
            }
        }

        public void set(VertexArray array, float scale, float[] bias)
        {
            //apply the tests early to assert atomicity
            requireArrayNotNull(array);
            requireValidBias(bias, array);

            setArray(array);
            setScale(scale);
            setBias(bias);
        }

        public void setArray(VertexArray array)
        {
            requireArrayNotNull(array);
            
            this.array = array;
        }

        public void setBias(float[] bias)
        {
            requireValidBias(bias, this.array);
            if (bias == null)
            {
                this.bias.set(0.0f, 0.0f, 0.0f);
            }
            else
            {
                this.bias.set(bias);
            }
        }

        public void setScale(float scale)
        {
            this.scale = scale;
        }

        public void deserialise(Deserializer deserialiser)
            throws IOException
        {
            setArray((VertexArray)deserialiser.readReference());
            bias.deserialise(deserialiser);
            setScale(deserialiser.readFloat());
        }

        public void serialise(Serializer serialiser)
            throws IOException
        {
            serialiser.writeReference(getArray());
            bias.serialise(serialiser);
            serialiser.writeFloat(getScale());
        }
    }

    private final ColorRGBA defaultColor = new ColorRGBA();
    private final ScaleBiasedVertexArray positions = new ScaleBiasedVertexArray();
    private VertexArray normals;
    private VertexArray colors;
    private ScaleBiasedVertexArray[] textureCoordinates;
    private final VertexCounter vertexCounter = new VertexCounter();

    public VertexBuffer()
    {
        super();
        setDefaultColor(0xffffffff);
    }

    @Override
    public void deserialise(Deserializer deserialiser)
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
    public void serialise(Serializer serialiser)
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

    public int getVertexCount()
    {
        return this.vertexCounter.getCount();
    }

    public synchronized void setColors(VertexArray va)
    {
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.colors, va);

        this.colors = va;
    }

    public synchronized void setNormals(VertexArray va)
    {
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.normals, va);
        
        this.normals = va;
    }

    public synchronized void setPositions(VertexArray va, float scale, float[] bias)
    {
        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.positions.getArray(), va);

        this.positions.set(va, scale, bias);
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

    private final void requireValidTextureIndex(int index)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException("index < 0");
        }
        if (index >= getTexCoordCount())
        {
            throw new IndexOutOfBoundsException("index >= getTexCoordCount()");
        }
    }

    public synchronized void setTexCoords(int index, VertexArray va, float scale, float[] bias)
    {
        requireValidTextureIndex(index);

        //check the vertex counts for potential mismatch
        this.vertexCounter.replace(this.positions.getArray(), va);
        
        this.textureCoordinates[index].set(va, scale, bias);
    }

    public void setDefaultColor(int argb)
    {
        this.defaultColor.set(argb);
    }

    public void setDefaultColor(List<Short> defaultColor)
    {
        if (defaultColor == null || defaultColor.size() == 0)
        {
            this.defaultColor.set(0xffffffff);
        }
        else
        {
            this.defaultColor.set(defaultColor);
        }
    }
}
