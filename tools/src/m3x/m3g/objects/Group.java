package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#Group<br>
 * ObjectIndex[] children;<br>
 * 
 * @author jsaarinen
 */
public class Group extends Node implements M3GTypedObject
{
  private ObjectIndex[] children;

  public Group(ObjectIndex[] animationTracks, UserParameter[] userParameters,
      Matrix transform, boolean enableRendering, boolean enablePicking,
      byte alphaFactor, int scope, ObjectIndex[] children) throws FileFormatException
  {
    super(animationTracks, userParameters, transform, enableRendering,
        enablePicking, alphaFactor, scope);
    assert (children != null);
    this.children = children;
  }
  
  public Group()
  {
    super();
  }

  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {    
    super.deserialize(dataInputStream, m3gVersion);
    int childrenLength = M3GSupport.readInt(dataInputStream);
    if (childrenLength < 0)
    {
      throw new FileFormatException("Number of children < 0: " + childrenLength);
    }
    this.children = new ObjectIndex[childrenLength];
    for (int i = 0; i < this.children.length; i++)
    {
      this.children[i] = new ObjectIndex();
      this.children[i].deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeInt(dataOutputStream, this.children.length);
    for (ObjectIndex child : this.children)
    {
      child.serialize(dataOutputStream, m3gVersion);
    }
  }
  
  public byte getObjectType()
  {
    return ObjectTypes.GROUP;
  }

  public ObjectIndex[] getChildren()
  {
    return this.children;
  }
}
