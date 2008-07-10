package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
  ObjectIndex   activeCamera;
  ObjectIndex   background;

 * @author jsaarinen
 */
public class World extends Group implements M3GTypedObject
{
  private ObjectIndex activeCamera;
  private ObjectIndex background;

  public World(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex[] children,
      ObjectIndex activeCamera, ObjectIndex background)
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

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.activeCamera = new ObjectIndex();
    this.activeCamera.deserialize(dataInputStream, m3gVersion);
    this.background = new ObjectIndex();
    this.background.deserialize(dataInputStream, m3gVersion);
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.activeCamera.serialize(dataOutputStream, m3gVersion);
    this.background.serialize(dataOutputStream, m3gVersion);
  }
  
  public byte getObjectType()
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
