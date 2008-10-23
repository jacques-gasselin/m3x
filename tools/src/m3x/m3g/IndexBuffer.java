package m3x.m3g;

import m3x.m3g.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.M3GSerializable;

/**
 * See http://java2me.org/m3g/file-format.html#IndexBuffer<br>
 * 
 * @author jsaarinen
 */
public abstract class IndexBuffer extends Object3D implements M3GSerializable
{
    public IndexBuffer(ObjectIndex[] animationTracks, UserParameter[] userParameters)
    {
        super(animationTracks, userParameters);
    }

    public IndexBuffer()
    {
        super();
    }
}
