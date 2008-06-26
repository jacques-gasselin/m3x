package m3x.m3g.objects;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.M3GSerializable;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.ObjectIndex;

public class Appearance extends Object3D implements M3GTypedObject
{
  private final byte layer;
  private final ObjectIndex compositingMpde;
  private final ObjectIndex fog;
  private final ObjectIndex polygonMode;
  private final ObjectIndex material;
  private final ObjectIndex[] textures;

  public Appearance(ObjectIndex[] animationTracks,
      UserParameter[] userParameters, byte layer, ObjectIndex compositingMpde,
      ObjectIndex fog, ObjectIndex polygonMode, ObjectIndex material,
      ObjectIndex[] textures)
  {
    super(animationTracks, userParameters);
    this.layer = layer;
    this.compositingMpde = compositingMpde;
    this.fog = fog;
    this.polygonMode = polygonMode;
    this.material = material;
    this.textures = textures;
  }

  
  public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
      throws IOException
  {
    super.serialize(dataOutputStream, m3gVersion);
    dataOutputStream.write(this.layer);
    this.compositingMpde.serialize(dataOutputStream, null);
    this.fog.serialize(dataOutputStream, null);
    this.polygonMode.serialize(dataOutputStream, null);
    this.material.serialize(dataOutputStream, null);
    for (int i = 0; i < this.textures.length; i++)
    {
      this.textures[i].serialize(dataOutputStream, null);
    }
  }

  
  public byte getObjectType()
  {
    return ObjectTypes.APPEARANCE;
  }
}
