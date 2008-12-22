package m3x.m3g;

import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.DataOutputStream;
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
public class PolygonMode extends Object3D implements SectionSerialisable
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

    public PolygonMode(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, int culling, int shading, int winding,
        boolean twoSidedLightingEnabled, boolean localCameraLightingEnabled,
        boolean perspectiveCorrectionEnabled)
    {
        super(animationTracks, userParameters);
        if (culling < CULL_BACK || culling > CULL_NONE)
        {
            throw new IllegalArgumentException("Invalid culling mode: " + culling);
        }
        this.culling = culling;
        if (shading < SHADE_FLAT || shading > SHADE_SMOOTH)
        {
            throw new IllegalArgumentException("Invalid shading mode: " + shading);
        }
        this.shading = shading;
        if (winding < WINDING_CCW || winding > WINDING_CW)
        {
            throw new IllegalArgumentException("Invalid winding mode: " + winding);
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

    @Override
    public void deserialise(Deserialiser deserialiser)
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
    public void serialise(Serialiser serialiser)
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
}
