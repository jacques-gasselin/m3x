package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import m3x.m3g.primitives.ColorRGBA;

/**
 * See http://java2me.org/m3g/file-format.html#VertexBuffer<br>
  ColorRGBA     defaultColor;<br>
  ObjectIndex   positions;<br>
  Float32[3]    positionBias;<br>
  Float32       positionScale;<br>
  ObjectIndex   normals;<br>
  ObjectIndex   colors;<br>
  UInt32        texcoordArrayCount;<br>
  FOR each texture coordinate array...<br>
       ObjectIndex   texCoords;<br>
       Float32[3]    texCoordBias;<br>
       Float32       texCoordScale;<br>
  <br>
 * @author jsaarinen
 */
public class VertexBuffer extends Object3D implements M3GTypedObject
{
    public VertexBuffer()
    {
        super();
    }

    public static class TextureCoordinate implements M3GSerializable
    {

        private VertexArray textureCoordinates;
        private float[] textureCoordinatesBias;
        private float textureCoordinatesScale;

        public TextureCoordinate()
        {
        }

        public TextureCoordinate(VertexArray textureCoordinates,
            float[] textureCoordinatesBias, float textureCoordinatesScale)
        {
            this.textureCoordinates = textureCoordinates;
            this.textureCoordinatesBias = textureCoordinatesBias;
            this.textureCoordinatesScale = textureCoordinatesScale;
        }

        public VertexArray getTextureCoordinates()
        {
            return this.textureCoordinates;
        }

        public float[] getTextureCoordinatesBias()
        {
            return this.textureCoordinatesBias;
        }

        public float getTextureCoordinatesScale()
        {
            return this.textureCoordinatesScale;
        }

        public void deserialize(M3GDeserialiser deserialiser)
            throws IOException, FileFormatException
        {
            this.textureCoordinates = (VertexArray)deserialiser.readObjectReference();
            this.textureCoordinatesBias = new float[3];
            this.textureCoordinatesBias[0] = deserialiser.readFloat();
            this.textureCoordinatesBias[1] = deserialiser.readFloat();
            this.textureCoordinatesBias[2] = deserialiser.readFloat();
            this.textureCoordinatesScale = deserialiser.readFloat();
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

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof TextureCoordinate))
            {
                return false;
            }
            TextureCoordinate another = (TextureCoordinate) obj;
            return this.textureCoordinates.equals(another.textureCoordinates) &&
                Arrays.equals(this.textureCoordinatesBias, another.textureCoordinatesBias) &&
                this.textureCoordinatesScale == another.textureCoordinatesScale;
        }
    }
    private ColorRGBA defaultColor;
    private VertexArray positions;
    private float[] positionBias;
    private float positionScale;
    private VertexArray normals;
    private VertexArray colors;
    private int textureCoordinateArrayCount;
    private TextureCoordinate[] textureCoordinates;

    public VertexBuffer(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, ColorRGBA defaultColor,
        VertexArray positions, float[] positionBias, float positionScale,
        VertexArray normals, VertexArray colors,
        TextureCoordinate[] textureCoordinates)
    {
        super(animationTracks, userParameters);
        this.defaultColor = defaultColor;
        this.positions = positions;
        this.positionBias = positionBias;
        this.positionScale = positionScale;
        this.normals = normals;
        this.colors = colors;
        this.textureCoordinateArrayCount = textureCoordinates.length;
        this.textureCoordinates = textureCoordinates;
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException, FileFormatException
    {
        super.deserialize(deserialiser);
        this.defaultColor = new ColorRGBA();
        this.defaultColor.deserialize(deserialiser);
        this.positions = (VertexArray)deserialiser.readObjectReference();
        this.positionBias = new float[3];
        this.positionBias[0] = deserialiser.readFloat();
        this.positionBias[1] = deserialiser.readFloat();
        this.positionBias[2] = deserialiser.readFloat();
        this.positionScale = deserialiser.readFloat();
        this.normals = (VertexArray)deserialiser.readObjectReference();
        this.colors = (VertexArray)deserialiser.readObjectReference();
        this.textureCoordinateArrayCount = deserialiser.readInt();
        if (this.textureCoordinateArrayCount < 0)
        {
            throw new FileFormatException("Invalid texture coordinate array length: " + this.textureCoordinateArrayCount);
        }
        this.textureCoordinates = new TextureCoordinate[this.textureCoordinateArrayCount];
        for (int i = 0; i < this.textureCoordinates.length; i++)
        {
            this.textureCoordinates[i] = new TextureCoordinate();
            this.textureCoordinates[i].deserialize(deserialiser);
        }
    }

    @Override
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

    public int getObjectType()
    {
        return ObjectTypes.VERTEX_BUFFER;
    }

    public ColorRGBA getDefaultColor()
    {
        return this.defaultColor;
    }

    public VertexArray getPositions()
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

    public VertexArray getNormals()
    {
        return this.normals;
    }

    public VertexArray getColors()
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
