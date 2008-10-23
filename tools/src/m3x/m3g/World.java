package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#World<br>
  ObjectIndex   activeCamera;<br>
  ObjectIndex   background;<br>
  <br>
 * @author jsaarinen
 */
public class World extends Group implements M3GTypedObject
{
    private ObjectIndex activeCamera;
    private ObjectIndex background;

    public World(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, ObjectIndex[] children,
        ObjectIndex activeCamera, ObjectIndex background) throws FileFormatException
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
    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.activeCamera = new ObjectIndex();
        this.activeCamera.deserialize(dataInputStream, m3gVersion);
        this.background = new ObjectIndex();
        this.background.deserialize(dataInputStream, m3gVersion);
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

    public ObjectIndex getActiveCamera()
    {
        return this.activeCamera;
    }

    public ObjectIndex getBackground()
    {
        return this.background;
    }
}
