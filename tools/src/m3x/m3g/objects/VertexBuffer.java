package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;

/**
  ColorRGBA     defaultColor;
  ObjectIndex   positions;
  Float32[3]    positionBias;
  Float32       positionScale;
  ObjectIndex   normals;
  ObjectIndex   colors;
  UInt32        texcoordArrayCount;
  FOR each texture coordinate array...
       ObjectIndex   texCoords;
       Float32[3]    texCoordBias;
       Float32       texCoordScale;

 * @author jsaarinen
 */
public class VertexBuffer extends Object3D implements M3GTypedObject
{

  public void deserialize(DataInputStream dataInputStream, String version)
      throws IOException, FileFormatException
  {
    // TODO Auto-generated method stub
    super.deserialize(dataInputStream, version);
  }

  public void serialize(DataOutputStream dataOutputStream, String version)
      throws IOException
  {
    // TODO Auto-generated method stub
    super.serialize(dataOutputStream, version);
  }

  public byte getObjectType()
  {
    return ObjectTypes.VERTEX_BUFFER;
  }
}
