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

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;
import m3x.m3g.util.Object3DReferences;

/**
 * See http://java2me.org/m3g/file-format.html#Appearance<br>
  Byte          layer;<br>
  ObjectIndex   compositingMode;<br>
  ObjectIndex   fog;<br>
  ObjectIndex   polygonMode;<br>
  ObjectIndex   material;<br>
  ObjectIndex[] textures;<br>

 * @author jsaarinen
 * @author jgasseli
 */
public class Appearance extends Object3D
{
    private int layer;
    private CompositingMode compositingMode;
    private Fog fog;
    private PolygonMode polygonMode;
    private Material material;
    private Texture2D[] textures;

    public Appearance()
    {
        super();
        setLayer(0);
        setCompositingMode(null);
        setFog(null);
        setPolygonMode(null);
        setMaterial(null);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!super.equals(obj))
        {
            return false;
        }
        final Appearance other = (Appearance) obj;
        if (getLayer() != other.getLayer())
        {
            return false;
        }
        return true;
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setLayer(deserialiser.readByte());
        setCompositingMode((CompositingMode)deserialiser.readReference());
        setFog((Fog)deserialiser.readReference());
        setPolygonMode((PolygonMode)deserialiser.readReference());
        setMaterial((Material)deserialiser.readReference());
        final int textureCount = deserialiser.readInt();
        setTextureCount(textureCount);
        for (int i = 0; i < textureCount; ++i)
        {
            setTexture(i, (Texture2D)deserialiser.readReference());
        }
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeByte(getLayer());
        serialiser.writeReference(getCompositingMode());
        serialiser.writeReference(getFog());
        serialiser.writeReference(getPolygonMode());
        serialiser.writeReference(getMaterial());
        final int textureCount = getTextureCount();
        serialiser.writeInt(textureCount);
        for (int i = 0; i < textureCount; ++i)
        {
            serialiser.writeReference(getTexture(i));
        }
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getCompositingMode());
        queue.add(getFog());
        queue.add(getPolygonMode());
        queue.add(getMaterial());
        for (int i = 0; i < getTextureCount(); ++i)
        {
            queue.add(getTexture(i));
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.APPEARANCE;
    }

    public int getLayer()
    {
        return this.layer;
    }

    public CompositingMode getCompositingMode()
    {
        return this.compositingMode;
    }

    public Fog getFog()
    {
        return this.fog;
    }

    public PolygonMode getPolygonMode()
    {
        return this.polygonMode;
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public int getTextureCount()
    {
        if (this.textures == null)
        {
            return 0;
        }
        return this.textures.length;
    }

    private final void requireTextureInRange(int index)
    {
        if (index < 0)
        {
            throw new IllegalArgumentException("index < 0");
        }
        if (index >= getTextureCount())
        {
            throw new IllegalArgumentException("index > getTextureCount()");
        }
    }

    private static final void requireLayerInRange(int layer)
    {
        if (layer < -63)
        {
            throw new IllegalArgumentException("layer < -63");
        }
        if (layer > 63)
        {
            throw new IllegalArgumentException("layer > 63");
        }
    }


    public Texture2D getTexture(int index)
    {
        requireTextureInRange(index);
        
        return this.textures[index];
    }

    public void setCompositingMode(CompositingMode compositingMode)
    {
        this.compositingMode = compositingMode;
    }

    public void setFog(Fog fog)
    {
        this.fog = fog;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public void setPolygonMode(PolygonMode polygonMode)
    {
        this.polygonMode = polygonMode;
    }

    public void setTextureCount(int count)
    {
        if (count == 0)
        {
            this.textures = null;
        }
        else
        {
            this.textures = new Texture2D[count];
        }
    }

    public void setTexture(int index, Texture2D texture)
    {
        requireTextureInRange(index);

        this.textures[index] = texture;
    }

    public void setLayer(int layer)
    {
        requireLayerInRange(layer);
        
        this.layer = layer;
    }

}
