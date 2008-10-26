package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.ObjectIndex;
import m3x.m3g.util.LittleEndianDataInputStream;

/**
 * See http://java2me.org/m3g/file-format.html#Sprite<br>
  ObjectIndex   image;<br>
  ObjectIndex   appearance;<br>
  <br>
  Boolean       isScaled;<br>
  <br>
  Int32         cropX;<br>
  Int32         cropY;<br>
  Int32         cropWidth;<br>
  <br>
 * @author jsaarinen
 */
public class Sprite extends Object3D implements M3GTypedObject
{
    private ObjectIndex image;
    private ObjectIndex appearance;
    private boolean isScaled;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;

    public Sprite(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        ObjectIndex image, ObjectIndex appearance, boolean isScaled, int cropX,
        int cropY, int cropWidth, int cropHeight)
    {
        super(animationTracks, userParameters);
        this.image = image;
        this.appearance = appearance;
        this.isScaled = isScaled;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
    }

    public Sprite()
    {
        super();
    }

    public void deserialize(LittleEndianDataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.image = new ObjectIndex();
        this.image.deserialize(dataInputStream, m3gVersion);
        this.appearance = new ObjectIndex();
        this.appearance.deserialize(dataInputStream, m3gVersion);
        this.isScaled = dataInputStream.readBoolean();
        this.cropX = dataInputStream.readInt();
        this.cropY = dataInputStream.readInt();
        this.cropWidth = dataInputStream.readInt();
        this.cropHeight = dataInputStream.readInt();
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        this.image.serialize(dataOutputStream, m3gVersion);
        this.appearance.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.writeBoolean(this.isScaled);
        M3GSupport.writeInt(dataOutputStream, this.cropX);
        M3GSupport.writeInt(dataOutputStream, this.cropY);
        M3GSupport.writeInt(dataOutputStream, this.cropWidth);
        M3GSupport.writeInt(dataOutputStream, this.cropHeight);
    }

    public int getObjectType()
    {
        return ObjectTypes.SPRITE;
    }

    public ObjectIndex getImage()
    {
        return this.image;
    }

    public ObjectIndex getAppearance()
    {
        return this.appearance;
    }

    public boolean isScaled()
    {
        return this.isScaled;
    }

    public int getCropX()
    {
        return this.cropX;
    }

    public int getCropY()
    {
        return this.cropY;
    }

    public int getCropWidth()
    {
        return this.cropWidth;
    }

    public int getCropHeight()
    {
        return this.cropHeight;
    }
}
