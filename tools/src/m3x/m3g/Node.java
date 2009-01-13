package m3x.m3g;

import m3x.m3g.primitives.Serialisable;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#Node<br>
  Boolean       enableRendering;
  Boolean       enablePicking;
  Byte          alphaFactor;
  UInt32        scope;
  Boolean       hasAlignment;
  IF hasAlignment==TRUE, THEN
    Byte          zTarget;
    Byte          yTarget;

    ObjectIndex   zReference;
    ObjectIndex   yReference;
  END

 * @author jsaarinen
 * @author jgasseli
 */
public abstract class Node extends Transformable implements Serialisable
{
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;
    private boolean renderingEnabled;
    private boolean pickingEnabled;
    private byte alphaFactor;
    private int scope;
    private boolean hasAlignment;
    private int zTarget;
    private int yTarget;
    private Node zReference;
    private Node yReference;
    private Node parent;

    public Node(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope)
    {
        super(animationTracks, userParameters, transform);
        this.renderingEnabled = enableRendering;
        this.pickingEnabled = enablePicking;
        this.alphaFactor = alphaFactor;
        this.scope = scope;
        this.hasAlignment = false;
        this.zTarget = 0;
        this.yTarget = 0;
        this.zReference = null;
        this.yReference = null;
    }

    public Node(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, int zTarget, int yTarget,
        Node zReference, Node yReference)
    {
        this(animationTracks, userParameters, transform,
            enableRendering, enablePicking, alphaFactor, scope);
        this.hasAlignment = true;
        validateZTarget(zTarget);
        this.zTarget = zTarget;
        validateYTarget(yTarget);
        this.yTarget = yTarget;
        this.zReference = zReference;
        this.yReference = yReference;
    }

    private static void validateYTarget(int yTarget)
    {
        if (yTarget < NONE || yTarget > Z_AXIS)
        {
            throw new IllegalArgumentException("Invalid yTarget: " + yTarget);
        }
    }

    private static void validateZTarget(int zTarget)
    {
        if (zTarget < NONE || zTarget > Z_AXIS)
        {
            throw new IllegalArgumentException("Invalid zTarget: " + zTarget);
        }
    }

    public Node()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.renderingEnabled = deserialiser.readBoolean();
        this.pickingEnabled = deserialiser.readBoolean();
        this.alphaFactor = deserialiser.readByte();
        this.scope = deserialiser.readInt();
        this.hasAlignment = deserialiser.readBoolean();
        if (this.hasAlignment)
        {
            this.zTarget = deserialiser.readUnsignedByte();
            validateZTarget(this.zTarget);
            this.yTarget = deserialiser.readUnsignedByte();
            validateYTarget(this.yTarget);
            this.zReference = (Node)deserialiser.readWeakReference();
            this.yReference = (Node)deserialiser.readWeakReference();
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeBoolean(this.renderingEnabled);
        serialiser.writeBoolean(this.pickingEnabled);
        serialiser.write(this.alphaFactor);
        serialiser.writeInt(this.scope);
        serialiser.writeBoolean(this.hasAlignment);
        if (this.hasAlignment)
        {
            serialiser.write(this.zTarget);
            serialiser.write(this.yTarget);
            serialiser.writeReference(this.zReference);
            serialiser.writeReference(this.yReference);
        }
    }

    public boolean isRenderingEnabled()
    {
        return this.renderingEnabled;
    }

    public boolean isPickingEnabled()
    {
        return this.pickingEnabled;
    }

    public void setRenderingEnabled(boolean enabled)
    {
        this.renderingEnabled = enabled;
    }

    public void setPickingEnabled(boolean enabled)
    {
        this.pickingEnabled = enabled;
    }

    private byte getAlphaFactorByte()
    {
        return this.alphaFactor;
    }

    public float getAlphaFactor()
    {
        int factor = getAlphaFactorByte();
        if (factor < 0)
        {
            factor += 256;
        }
        return factor / 255.0f;
    }

    public int getScope()
    {
        return this.scope;
    }

    public void setScope(int scope)
    {
        this.scope = scope;
    }

    public Node getParent()
    {
        return parent;
    }

    private void setAlphaFactorByte(byte alphaFactor)
    {
        this.alphaFactor = alphaFactor;
    }

    public void setAlphaFactor(float alphaFactor)
    {
        int factor = (int)alphaFactor * 256;
        factor = Math.max(0, factor);
        factor = Math.min(255, factor);
        if (factor > 127)
        {
            factor -= 256;
        }
        setAlphaFactorByte((byte)factor);
    }

    protected void setParent(Node parent)
    {
        if ((parent != null) && (getParent() != null))
        {
            throw new IllegalStateException("node already has a parent");
        }
        this.parent = parent;
    }
}
