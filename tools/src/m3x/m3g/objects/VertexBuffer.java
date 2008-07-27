package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.ObjectIndex;

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
  public static class TextureCoordinate implements M3GSerializable
  {
    public ObjectIndex textureCoordinates;
    public float[] textureCoordinatesBias;
    public float textureCoordinatesScale;

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {      
      this.textureCoordinates = new ObjectIndex();
      this.textureCoordinates.deserialize(dataInputStream, m3gVersion);
      this.textureCoordinatesBias = new float[3];
      this.textureCoordinatesBias[0] = M3GSupport.readFloat(dataInputStream);
      this.textureCoordinatesBias[1] = M3GSupport.readFloat(dataInputStream);
      this.textureCoordinatesBias[2] = M3GSupport.readFloat(dataInputStream);
      this.textureCoordinatesScale = M3GSupport.readFloat(dataInputStream);
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {      
      this.textureCoordinates.serialize(dataOutputStream, m3gVersion);
      for (float f : this.textureCoordinatesBias)
      {
        M3GSupport.writeFloat(dataOutputStream, f);
      }
      M3GSupport.writeFloat(dataOutputStream, this.textureCoordinatesScale);
    }
  }
  
  private ColorRGBA defaultColor;
  private ObjectIndex positions;
  private float[] positionBias;
  private float positionScale;
  private ObjectIndex normals;
  private ObjectIndex colors;
  private int textureCoordinateArrayCount;
  private TextureCoordinate[] textureCoordinates;
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {
    super.deserialize(dataInputStream, m3gVersion);
    this.defaultColor = new ColorRGBA();
    this.defaultColor.deserialize(dataInputStream, m3gVersion);
    this.positions = new ObjectIndex();
    this.positions.deserialize(dataInputStream, m3gVersion);
    this.positionBias = new float[3];
    this.positionBias[0] = M3GSupport.readFloat(dataInputStream);
    this.positionBias[1] = M3GSupport.readFloat(dataInputStream);
    this.positionBias[2] = M3GSupport.readFloat(dataInputStream);
    this.normals = new ObjectIndex();
    this.normals.deserialize(dataInputStream, m3gVersion);
    this.colors = new ObjectIndex();
    this.colors.deserialize(dataInputStream, m3gVersion);
    this.textureCoordinateArrayCount = M3GSupport.readInt(dataInputStream);
    if (this.textureCoordinateArrayCount < 0)
    {
      throw new FileFormatException("Invalid texture coordinate array length: " + this.textureCoordinateArrayCount);
    }
    this.textureCoordinates = new TextureCoordinate[this.textureCoordinateArrayCount];
    for (int i = 0; i < this.textureCoordinates.length; i++)
    {
      this.textureCoordinates[i] = new TextureCoordinate();
      this.textureCoordinates[i].deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    this.defaultColor.serialize(dataOutputStream, m3gVersion);
    this.positions.serialize(dataOutputStream, m3gVersion);
    for (float f : this.positionBias)
    {
      M3GSupport.writeFloat(dataOutputStream, f);
    }
    M3GSupport.writeFloat(dataOutputStream, this.positionScale);
    this.normals.serialize(dataOutputStream, m3gVersion);
    this.colors.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeInt(dataOutputStream, this.textureCoordinateArrayCount);
    for (TextureCoordinate textureCoordinate : this.textureCoordinates)
    {
      textureCoordinate.serialize(dataOutputStream, m3gVersion);
    }
  }

  public byte getObjectType()
  {
    return ObjectTypes.VERTEX_BUFFER;
  }

  public ColorRGBA getDefaultColor()
  {
    return this.defaultColor;
  }

  public ObjectIndex getPositions()
  {
    return this.positions;
  }

  public float[] getPositionBias()
  {
    return this.positionBias;
  }

  public float getPositionScale()
  {
    return this.positionScale;
  }

  public ObjectIndex getNormals()
  {
    return this.normals;
  }

  public ObjectIndex getColors()
  {
    return this.colors;
  }

  public int getTextureCoordinateArrayCount()
  {
    return this.textureCoordinateArrayCount;
  }

  public TextureCoordinate[] getTextureCoordinates()
  {
    return this.textureCoordinates;
  }
}
