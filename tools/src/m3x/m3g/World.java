package m3x.m3g;

import m3x.m3g.primitives.TypedObject;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#World<br>
  ObjectIndex   activeCamera;<br>
  ObjectIndex   background;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class World extends Group implements TypedObject
{
    private Camera activeCamera;
    private Background background;

    public World(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Node[] children,
        Camera activeCamera, Background background)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope, children);
        this.activeCamera = activeCamera;
        this.background = background;
    }

    public World()
    {
        super();
    }

    @Override
    public void deserialize(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        setActiveCamera((Camera)deserialiser.readReference());
        setBackground((Background)deserialiser.readReference());
    }

    @Override
    public void serialize(Serialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.writeReference(getActiveCamera());
        serialiser.writeReference(getBackground());
    }

    @Override
    public int getObjectType()
    {
        return ObjectTypes.WORLD;
    }

    public Camera getActiveCamera()
    {
        return this.activeCamera;
    }

    public Background getBackground()
    {
        return this.background;
    }

    private void setActiveCamera(Camera activeCamera)
    {
        this.activeCamera = activeCamera;
    }

    private void setBackground(Background background)
    {
        this.background = background;
    }
}
