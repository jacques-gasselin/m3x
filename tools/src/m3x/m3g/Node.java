package m3x.m3g;

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
 */
public abstract class Node extends Transformable implements M3GSerializable
{
    public static final int NONE = 144;
    public static final int ORIGIN = 145;
    public static final int X_AXIS = 146;
    public static final int Y_AXIS = 147;
    public static final int Z_AXIS = 148;
    private boolean enableRendering;
    private boolean enablePicking;
    private byte alphaFactor;
    private int scope;
    private boolean hasAlignment;
    private int zTarget;
    private int yTarget;
    private Node zReference;
    private Node yReference;

    public Node(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope)
    {
        super(animationTracks, userParameters, transform);
        this.enableRendering = enableRendering;
        this.enablePicking = enablePicking;
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
        Node zReference, Node yReference) throws FileFormatException
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

    private static void validateYTarget(int yTarget) throws FileFormatException
    {
        if (yTarget < NONE || yTarget > Z_AXIS)
        {
            throw new FileFormatException("Invalid yTarget: " + yTarget);
        }
    }

    private static void validateZTarget(int zTarget) throws FileFormatException
    {
        if (zTarget < NONE || zTarget > Z_AXIS)
        {
            throw new FileFormatException("Invalid zTarget: " + zTarget);
        }
    }

    public Node()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.enableRendering = deserialiser.readBoolean();
        this.enablePicking = deserialiser.readBoolean();
        this.alphaFactor = deserialiser.readByte();
        this.scope = deserialiser.readInt();
        this.hasAlignment = deserialiser.readBoolean();
        if (this.hasAlignment)
        {
            this.zTarget = deserialiser.readUnsignedByte();
            validateZTarget(this.zTarget);
            this.yTarget = deserialiser.readUnsignedByte();
            validateYTarget(this.yTarget);
            this.zReference = (Node)deserialiser.readWeakObjectReference();
            this.yReference = (Node)deserialiser.readWeakObjectReference();
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.writeBoolean(this.enableRendering);
        dataOutputStream.writeBoolean(this.enablePicking);
        dataOutputStream.write(this.alphaFactor);
        M3GSupport.writeInt(dataOutputStream, this.scope);
        dataOutputStream.writeBoolean(this.hasAlignment);
        if (this.hasAlignment)
        {
            dataOutputStream.write(this.zTarget);
            dataOutputStream.write(this.yTarget);
            this.zReference.serialize(dataOutputStream, m3gVersion);
            this.yReference.serialize(dataOutputStream, m3gVersion);
        }
    }

    public boolean isEnableRendering()
    {
        return this.enableRendering;
    }

    public boolean isEnablePicking()
    {
        return this.enablePicking;
    }

    public byte getAlphaFactor()
    {
        return this.alphaFactor;
    }

    public int getScope()
    {
        return this.scope;
    }
}
