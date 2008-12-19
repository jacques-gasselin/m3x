package m3x.m3g;

import m3x.m3g.primitives.Serializable;

/**
 * See http://java2me.org/m3g/file-format.html#IndexBuffer<br>
 * 
 * @author jsaarinen
 */
public abstract class IndexBuffer extends Object3D implements Serializable
{
    public IndexBuffer(AnimationTrack[] animationTracks, UserParameter[] userParameters)
    {
        super(animationTracks, userParameters);
    }

    public IndexBuffer()
    {
        super();
    }
}
