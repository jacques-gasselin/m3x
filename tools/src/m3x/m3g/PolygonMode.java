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

import m3x.m3g.primitives.SectionSerializable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;


/**
 * See http://java2me.org/m3g/file-format.html#PolygonMode<br>
  Byte          culling;
  Byte          shading;
  Byte          winding;
  Boolean       twoSidedLightingEnabled;
  Boolean       localCameraLightingEnabled;
  Boolean       perspectiveCorrectionEnabled;

 * @author jsaarinen
 * @author jgasseli
 */
public class PolygonMode extends Object3D implements SectionSerializable
{
    public static final int CULL_BACK = 160;
    public static final int CULL_FRONT = 161;
    public static final int CULL_NONE = 162;
    public static final int SHADE_FLAT = 164;
    public static final int SHADE_SMOOTH = 165;
    public static final int WINDING_CCW = 168;
    public static final int WINDING_CW = 169;
    private int culling;
    private int shading;
    private int winding;
    private boolean twoSidedLightingEnabled;
    private boolean localCameraLightingEnabled;
    private boolean perspectiveCorrectionEnabled;

    public PolygonMode()
    {
        super();
    }

    @Override
    public void deserialise(Deserializer deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.culling = deserialiser.readUnsignedByte();
        this.shading = deserialiser.readUnsignedByte();
        this.winding = deserialiser.readUnsignedByte();
        this.twoSidedLightingEnabled = deserialiser.readBoolean();
        this.localCameraLightingEnabled = deserialiser.readBoolean();
        this.perspectiveCorrectionEnabled = deserialiser.readBoolean();
    }

    @Override
    public void serialise(Serializer serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.culling);
        serialiser.write(this.shading);
        serialiser.write(this.winding);
        serialiser.writeBoolean(this.twoSidedLightingEnabled);
        serialiser.writeBoolean(this.localCameraLightingEnabled);
        serialiser.writeBoolean(this.perspectiveCorrectionEnabled);
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.POLYGON_MODE;
    }

    public int getCulling()
    {
        return this.culling;
    }

    public int getShading()
    {
        return this.shading;
    }

    public int getWinding()
    {
        return this.winding;
    }

    public boolean isTwoSidedLightingEnabled()
    {
        return this.twoSidedLightingEnabled;
    }

    public boolean isLocalCameraLightingEnabled()
    {
        return this.localCameraLightingEnabled;
    }

    public boolean isPerspectiveCorrectionEnabled()
    {
        return this.perspectiveCorrectionEnabled;
    }

    public void setCulling(int mode)
    {
        this.culling = mode;
    }

    public void setCulling(String mode)
    {
        setCulling(getFieldValue(mode, "mode"));
    }

    public void setLocalCameraLightingEnabled(boolean localCameraLightingEnabled)
    {
        this.localCameraLightingEnabled = localCameraLightingEnabled;
    }

    public void setPerspectiveCorrectionEnabled(boolean perspectiveCorrectionEnabled)
    {
        this.perspectiveCorrectionEnabled = perspectiveCorrectionEnabled;
    }

    public void setShading(int mode)
    {
        this.shading = mode;
    }

    public void setShading(String mode)
    {
        setShading(getFieldValue(mode, "mode"));
    }

    public void setTwoSidedLightingEnabled(boolean twoSidedLightingEnabled)
    {
        this.twoSidedLightingEnabled = twoSidedLightingEnabled;
    }

    public void setWinding(int mode)
    {
        this.winding = mode;
    }

    public void setWinding(String mode)
    {
        setWinding(getFieldValue(mode, "mode"));
    }

}
