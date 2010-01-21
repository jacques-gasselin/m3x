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

package javax.microedition.m3g;

import java.util.List;

/**
 * @author jgasseli
 */
public abstract class AppearanceBase extends Object3D
{
    private int layer;
    private boolean depthSortEnabled;
    private CompositingMode compositingMode;
    private PolygonMode polygonMode;

    public AppearanceBase()
    {
        
    }

    public CompositingMode getCompositingMode()
    {
        return this.compositingMode;
    }
    
    public int getLayer()
    {
        return this.layer;
    }

    public PolygonMode getPolygonMode()
    {
        return this.polygonMode;
    }

    @Override
    void getReferences(List<Object3D> references)
    {
        super.getReferences(references);

        if (compositingMode != null)
        {
            references.add(compositingMode);
        }

        if (polygonMode != null)
        {
            references.add(polygonMode);
        }
    }
    
    public boolean isDepthSortEnabled()
    {
        return this.depthSortEnabled;
    }

    public void setCompositingMode(CompositingMode compositingMode)
    {
        this.compositingMode = compositingMode;
    }

    public void setDepthSortEnabled(boolean enable)
    {
        this.depthSortEnabled = enable;
    }

    public void setLayer(int layer)
    {
        if (layer < -63)
        {
            throw new IndexOutOfBoundsException("layer < -63");
        }
        if (layer > 63)
        {
            throw new IndexOutOfBoundsException("layer > 63");
        }
        
        this.layer = layer;
    }

    public void setPolygonMode(PolygonMode polygonMode)
    {
        this.polygonMode = polygonMode;
    }
}


