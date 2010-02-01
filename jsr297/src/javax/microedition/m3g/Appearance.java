/*
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

package javax.microedition.m3g;

import m3x.Require;
import java.util.List;

/**
 * @author jgasseli
 */
public class Appearance extends AppearanceBase
{
    private Fog fog;
    private Material material;
    private PointSpriteMode pointSpriteMode;
    
    private static final int MAX_TEXTURE_COORDS = 8;
    private final Texture[] textures = new Texture[MAX_TEXTURE_COORDS];
    
    public Appearance()
    {
        
    }

    public Fog getFog()
    {
        return this.fog;
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public PointSpriteMode getPointSpriteMode()
    {
        return this.pointSpriteMode;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (fog != null)
        {
            references.add(fog);
        }

        if (material != null)
        {
            references.add(material);
        }

        if (pointSpriteMode != null)
        {
            references.add(pointSpriteMode);
        }

        for (Texture t : textures)
        {
            if (t != null)
            {
                references.add(t);
            }
        }
    }
    
    public Texture getTextureBase(int index)
    {
        Require.indexInRange(index, MAX_TEXTURE_COORDS);

        return this.textures[index];
    }

    public Texture2D getTexture(int index)
    {
        final Texture texture = getTextureBase(index);
        if (texture instanceof Texture2D)
        {
            return (Texture2D) texture;
        }
        return null;
    }

    public TextureCube getTextureCube(int index)
    {
        final Texture texture = getTextureBase(index);
        if (texture instanceof TextureCube)
        {
            return (TextureCube) texture;
        }
        return null;
    }

    public void setFog(Fog fog)
    {
        this.fog = fog;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public void setPointSpriteMode(PointSpriteMode pointSpriteMode)
    {
        this.pointSpriteMode = pointSpriteMode;
    }

    public void setTexture(int index, Texture2D texture)
    {
        Require.indexInRange(index, MAX_TEXTURE_COORDS);

        this.textures[index] = texture;
    }

    public void setTextureCube(int index, TextureCube texture)
    {
        Require.indexInRange(index, MAX_TEXTURE_COORDS);

        this.textures[index] = texture;
    }
}
