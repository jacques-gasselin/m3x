package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * See http://java2me.org/m3g/file-format.html#World<br>
  ObjectIndex   activeCamera;<br>
  ObjectIndex   background;<br>
  <br>
 * @author jsaarinen
 */
public class World extends Group implements M3GTypedObject
{
    private Camera activeCamera;
    private Background background;

    public World(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Node[] children,
        Camera activeCamera, Background background) throws FileFormatException
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
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.activeCamera = (Camera)deserialiser.readObjectReference();
        this.background = (Background)deserialiser.readObjectReference();
    }

    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.activeCamera.serialize(dataOutputStream, m3gVersion);
        this.background.serialize(dataOutputStream, m3gVersion);
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
}
