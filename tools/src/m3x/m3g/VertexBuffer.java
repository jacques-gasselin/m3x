package m3x.m3g;

import m3x.m3g.primitives.Serialisable;
import m3x.m3g.primitives.SectionSerialisable;
import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGBA;
import m3x.m3g.primitives.Vector3D;
import m3x.m3g.util.Object3DReferences;

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
 * @author jgasseli
 */
public class VertexBuffer extends Object3D implements SectionSerialisable
{
    private static class ScaleBiasedVertexArray implements Serialisable
    {

        private VertexArray array;
        private float scale;
        private Vector3D bias;

        public ScaleBiasedVertexArray()
        {
            this.bias = new Vector3D();
        }

        public VertexArray getArray()
        {
            return this.array;
        }

        public float[] getBias()
        {
            float[] ret = new float[3];
            ret[0] = bias.getX();
            ret[1] = bias.getY();
            ret[2] = bias.getZ();
            return ret;
        }

        public float getScale()
        {
            return this.scale;
        }

        public void setArray(VertexArray array)
        {
            if (array == null)
            {
                throw new NullPointerException("array is null");
            }
            this.array = array;
        }

        public void setBias(float[] bias)
        {
            if (bias == null)
            {
                bias = new float[3];
            }
            if (bias.length != 3)
            {
                throw new IllegalArgumentException("bias does not have 3 components");
            }
            this.bias.set(bias[0], bias[1], bias[2]);
        }

        public void setScale(float scale)
        {
            this.scale = scale;
        }

        public void deserialise(Deserialiser deserialiser)
            throws IOException
        {
            setArray((VertexArray)deserialiser.readReference());
            bias.deserialise(deserialiser);
            setScale(deserialiser.readFloat());
        }

        public void serialise(Serialiser serialiser)
            throws IOException
        {
            serialiser.writeReference(getArray());
            bias.serialise(serialiser);
            serialiser.writeFloat(getScale());
        }
    }

    private ColorRGBA defaultColor;
    private ScaleBiasedVertexArray positions;
    private VertexArray normals;
    private VertexArray colors;
    private ScaleBiasedVertexArray[] textureCoordinates;

    public VertexBuffer()
    {
        super();
        this.defaultColor = new ColorRGBA();
        this.positions = new ScaleBiasedVertexArray();
        setDefaultColor(0xffffffff);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.defaultColor = new ColorRGBA();
        this.defaultColor.deserialise(deserialiser);
        this.positions.deserialise(deserialiser);
        this.normals = (VertexArray)deserialiser.readReference();
        this.colors = (VertexArray)deserialiser.readReference();
        int textureCoordinateArrayCount = deserialiser.readInt();
        setTexCoordCount(textureCoordinateArrayCount);
        for (int i = 0; i < textureCoordinateArrayCount; i++)
        {
            this.textureCoordinates[i] = new ScaleBiasedVertexArray();
            this.textureCoordinates[i].deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.defaultColor.serialise(serialiser);
        this.positions.serialise(serialiser);
        serialiser.writeReference(getNormals());
        serialiser.writeReference(getColors());
        serialiser.writeInt(getTexCoordCount());
        for (ScaleBiasedVertexArray textureCoordinate : this.textureCoordinates)
        {
            textureCoordinate.serialise(serialiser);
        }
    }

    @Override
    protected void setReferenceQueue(Object3DReferences queue)
    {
        super.setReferenceQueue(queue);
        queue.add(getPositionsArray());
        queue.add(getNormals());
        queue.add(getColors());
        for (int i = 0; i < getTexCoordCount(); ++i)
        {
            queue.add(getTexCoordsArray(i));
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.VERTEX_BUFFER;
    }

    public ColorRGBA getDefaultColor()
    {
        return this.defaultColor;
    }

    public VertexArray getPositionsArray()
    {
        return this.positions.getArray();
    }

    public float[] getPositionsBias()
    {
        return this.positions.getBias();
    }

    public float getPositionsScale()
    {
        return this.positions.getScale();
    }

    public VertexArray getNormals()
    {
        return this.normals;
    }

    public VertexArray getColors()
    {
        return this.colors;
    }

    public int getTexCoordCount()
    {
        if (this.textureCoordinates == null)
        {
            return 0;
        }
        return this.textureCoordinates.length;
    }

    public VertexArray getTexCoordsArray(int index)
    {
        return this.textureCoordinates[index].getArray();
    }

    public float[] getTexCoordsBias(int index)
    {
        return this.textureCoordinates[index].getBias();
    }

    public float getTexCoordsScale(int index)
    {
        return this.textureCoordinates[index].getScale();
    }

    public void setColors(VertexArray va)
    {
        this.colors = va;
    }

    public void setNormals(VertexArray va)
    {
        this.normals = va;
    }

    public void setPositions(VertexArray va, float scale, float[] bias)
    {
        this.positions.setArray(va);
        this.positions.setScale(scale);
        this.positions.setBias(bias);
    }

    public void setTexCoordCount(int texCoordCount)
    {
        if (texCoordCount < 0)
        {
            throw new IllegalArgumentException(
                "Invalid texture coordinate array length: "
                + texCoordCount);
        }
        this.textureCoordinates = new ScaleBiasedVertexArray[texCoordCount];
        for (int i = 0; i < texCoordCount; ++i)
        {
            this.textureCoordinates[i] = new ScaleBiasedVertexArray();
        }
    }

    public void setTexCoords(int index, VertexArray va, float scale, float[] bias)
    {
        this.textureCoordinates[index].setArray(va);
        this.textureCoordinates[index].setScale(scale);
        this.textureCoordinates[index].setBias(bias);
    }

    private void setDefaultColor(int rgba)
    {
        this.defaultColor.set(rgba);
    }
}
