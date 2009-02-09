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

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#Material<br>
  ColorRGB      ambientColor;<br>
  ColorRGBA     diffuseColor;<br>
  ColorRGB      emissiveColor;<br>
  ColorRGB      specularColor;<br>
  Float32       shininess;<br>
  Boolean       vertexColorTrackingEnabled;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Material extends Object3D
{
    private ColorRGB ambientColor;
    private ColorRGBA diffuseColor;
    private ColorRGB emissiveColor;
    private ColorRGB specularColor;
    private float shininess;
    private boolean vertexColorTrackingEnabled;

    public Material()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.ambientColor = new ColorRGB();
        this.ambientColor.deserialise(deserialiser);
        this.diffuseColor = new ColorRGBA();
        this.diffuseColor.deserialise(deserialiser);
        this.emissiveColor = new ColorRGB();
        this.emissiveColor.deserialise(deserialiser);
        this.specularColor = new ColorRGB();
        this.specularColor.deserialise(deserialiser);
        this.shininess = deserialiser.readFloat();
        this.vertexColorTrackingEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.ambientColor.serialise(serialiser);
        this.diffuseColor.serialise(serialiser);
        this.emissiveColor.serialise(serialiser);
        this.specularColor.serialise(serialiser);
        serialiser.writeFloat(this.shininess);
        serialiser.writeBoolean(this.vertexColorTrackingEnabled);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.MATERIAL;
    }

    public ColorRGB getAmbientColor()
    {
        return this.ambientColor;
    }

    public ColorRGBA getDiffuseColor()
    {
        return this.diffuseColor;
    }

    public ColorRGB getEmissiveColor()
    {
        return this.emissiveColor;
    }

    public ColorRGB getSpecularColor()
    {
        return this.specularColor;
    }

    public float getShininess()
    {
        return this.shininess;
    }

    public boolean isVertexColorTrackingEnabled()
    {
        return this.vertexColorTrackingEnabled;
    }
}
