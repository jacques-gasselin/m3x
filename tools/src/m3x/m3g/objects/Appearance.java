package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

/**
  Byte          layer;
  ObjectIndex   compositingMode;
  ObjectIndex   fog;
  ObjectIndex   polygonMode;
  ObjectIndex   material;
  ObjectIndex[] textures;

 * @author jsaarinen
 */
public class Appearance extends Object3D implements M3GTypedObject
{
  private byte layer;
  private ObjectIndex compositingMode;
  private ObjectIndex fog;
  private ObjectIndex polygonMode;
  private ObjectIndex material;
  private ObjectIndex[] textures;

  public Appearance(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, byte layer, ObjectIndex compositingMode,
      ObjectIndex fog, ObjectIndex polygonMode, ObjectIndex material,
      ObjectIndex[] textures)
  {
    super(animationTracks, userParameters);
    this.layer = layer;
    this.compositingMode = compositingMode;
    this.fog = fog;
    this.polygonMode = polygonMode;
    this.material = material;
    this.textures = textures;
  }
  
  public void deserialize(DataInputStream dataInputStream, String m3gVersion)
      throws IOException, FileFormatException
  {   
    super.deserialize(dataInputStream, m3gVersion);
    this.layer = dataInputStream.readByte();
    this.compositingMode = new ObjectIndex();
    this.compositingMode.deserialize(dataInputStream, m3gVersion);
    this.fog = new ObjectIndex();
    this.fog.deserialize(dataInputStream, m3gVersion);
    this.polygonMode = new ObjectIndex();
    this.polygonMode.deserialize(dataInputStream, m3gVersion);
    this.material = new ObjectIndex();
    this.material.deserialize(dataInputStream, m3gVersion);
    int texturesLength = M3GSupport.readInt(dataInputStream);
    this.textures = new ObjectIndex[texturesLength];
    for (int i = 0; i < this.textures.length; i++)
    {
      this.textures[i] = new ObjectIndex();
      this.textures[i].deserialize(dataInputStream, m3gVersion);
    }
  }

  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.layer);
    this.compositingMode.serialize(dataOutputStream, m3gVersion);
    this.fog.serialize(dataOutputStream, m3gVersion);
    this.polygonMode.serialize(dataOutputStream, m3gVersion);
    this.material.serialize(dataOutputStream, m3gVersion);
    M3GSupport.writeInt(dataOutputStream, this.textures.length);
    for (int i = 0; i < this.textures.length; i++)
    {
      this.textures[i].serialize(dataOutputStream, m3gVersion);
    }
  }

  public byte getObjectType()
  {
    return ObjectTypes.APPEARANCE;
  }

  public byte getLayer()
  {
    return this.layer;
  }

  public ObjectIndex getCompositingMode()
  {
    return this.compositingMode;
  }

  public ObjectIndex getFog()
  {
    return this.fog;
  }

  public ObjectIndex getPolygonMode()
  {
    return this.polygonMode;
  }

  public ObjectIndex getMaterial()
  {
    return this.material;
  }

  public ObjectIndex[] getTextures()
  {
    return this.textures;
  }
}
