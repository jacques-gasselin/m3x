package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#PolygonMode<br>
  Byte          culling;
  Byte          shading;
  Byte          winding;
  Boolean       twoSidedLightingEnabled;
  Boolean       localCameraLightingEnabled;
  Boolean       perspectiveCorrectionEnabled;

 * @author jsaarinen
 */
public class PolygonMode extends Object3D implements M3GTypedObject
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

    public PolygonMode(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, int culling, int shading, int winding,
        boolean twoSidedLightingEnabled, boolean localCameraLightingEnabled,
        boolean perspectiveCorrectionEnabled) throws FileFormatException
    {
        super(animationTracks, userParameters);
        if (culling < CULL_BACK || culling > CULL_NONE)
        {
            throw new FileFormatException("Invalid culling mode: " + culling);
        }
        this.culling = culling;
        if (shading < SHADE_FLAT || shading > SHADE_SMOOTH)
        {
            throw new FileFormatException("Invalid shading mode: " + shading);
        }
        this.shading = shading;
        if (winding < WINDING_CCW || winding > WINDING_CW)
        {
            throw new FileFormatException("Invalid winding mode: " + winding);
        }
        this.winding = winding;
        this.twoSidedLightingEnabled = twoSidedLightingEnabled;
        this.localCameraLightingEnabled = localCameraLightingEnabled;
        this.perspectiveCorrectionEnabled = perspectiveCorrectionEnabled;
    }

    public PolygonMode()
    {
        super();
    }

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.culling = dataInputStream.readByte() & 0xFF;
        this.shading = dataInputStream.readByte() & 0xFF;
        this.winding = dataInputStream.readByte() & 0xFF;
        this.twoSidedLightingEnabled = dataInputStream.readBoolean();
        this.localCameraLightingEnabled = dataInputStream.readBoolean();
        this.perspectiveCorrectionEnabled = dataInputStream.readBoolean();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.culling);
        dataOutputStream.write(this.shading);
        dataOutputStream.write(this.winding);
        dataOutputStream.writeBoolean(this.twoSidedLightingEnabled);
        dataOutputStream.writeBoolean(this.localCameraLightingEnabled);
        dataOutputStream.writeBoolean(this.perspectiveCorrectionEnabled);
    }

    public byte getObjectType()
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
}
