package m3x.m3g;

import m3x.m3g.primitives.Serialisable;
import java.io.IOException;

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
    private int alphaFactor;
    private int scope;
    private boolean hasAlignment;
    private int zTarget;
    private int yTarget;
    private Node zReference;
    private Node yReference;
    private Node parent;

    private static final void requireValidTarget(int target)
    {
        if (target < NONE || target > Z_AXIS)
        {
            throw new IllegalArgumentException("Invalid target: " + target);
        }
    }

    private final void requireNotThis(Node ref)
    {
        if (ref == this)
        {
            throw new IllegalArgumentException("self reference is not allowed");
        }
    }

    public Node()
    {
        super();
        setParent(null);
        setRenderingEnabled(true);
        setPickingEnabled(true);
        setAlphaFactor(1.0f);
        setScope(-1);
        setAlignment(null, NONE, null, NONE);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setRenderingEnabled(deserialiser.readBoolean());
        setPickingEnabled(deserialiser.readBoolean());
        setAlphaFactorByte(deserialiser.readUnsignedByte());
        setScope(deserialiser.readInt());
        this.hasAlignment = deserialiser.readBoolean();
        if (this.hasAlignment)
        {
            final int zTarget = deserialiser.readUnsignedByte();
            final int yTarget = deserialiser.readUnsignedByte();
            Node zRef = (Node)deserialiser.readWeakReference();
            Node yRef = (Node)deserialiser.readWeakReference();
            setAlignment(zRef, zTarget, yRef, yTarget);
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

    private int getAlphaFactorByte()
    {
        return this.alphaFactor;
    }

    public float getAlphaFactor()
    {
        final int factor = getAlphaFactorByte();
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

    public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget)
    {
        requireValidTarget(zTarget);
        requireValidTarget(yTarget);
        requireNotThis(zRef);
        requireNotThis(yRef);

        this.zReference = zRef;
        this.zTarget = zTarget;
        this.yReference = yRef;
        this.yTarget = yTarget;
    }

    private void setAlphaFactorByte(int alphaFactor)
    {
        this.alphaFactor = alphaFactor;
    }

    public void setAlphaFactor(float alphaFactor)
    {
        int factor = (int)alphaFactor * 256;
        factor = Math.max(0, factor);
        factor = Math.min(255, factor);
        setAlphaFactorByte(factor);
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
