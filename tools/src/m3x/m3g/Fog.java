package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;

/**
 * See See http://java2me.org/m3g/file-format.html#Fog<br>
  ColorRGB      color;<br>
  Byte          mode;<br>
  IF mode==EXPONENTIAL, THEN<br>
    Float32       density;<br>
  ELSE IF mode==LINEAR, THEN<br>
    Float32       near;<br>
    Float32       far;<br>
  END<br>

 * @author jsaarinen
 * @author jgasseli
 */
public class Fog extends Object3D
{
    public final static int MODE_EXPONENTIAL = 80;
    public final static int MODE_LINEAR = 81;
    private ColorRGB color;
    private int mode;
    private float density;
    private float near;
    private float far;

    public Fog(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        ColorRGB color, float density)
    {
        super(animationTracks, userParameters);
        this.color = color;
        this.mode = MODE_EXPONENTIAL;
        this.density = density;
        this.near = 0.0f;
        this.far = 0.0f;
    }

    public Fog(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        ColorRGB color, float near, float far)
    {
        super(animationTracks, userParameters);
        this.color = color;
        this.mode = MODE_LINEAR;
        this.density = 0.0f;
        this.near = near;
        this.far = far;
    }

    public Fog()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.color = new ColorRGB();
        this.color.deserialise(deserialiser);
        this.mode = deserialiser.readUnsignedByte();
        if (this.mode == MODE_EXPONENTIAL)
        {
            this.density = deserialiser.readFloat();
        }
        else if (this.mode == MODE_LINEAR)
        {
            this.near = deserialiser.readFloat();
            this.far = deserialiser.readFloat();
        }
        else
        {
            throw new IllegalArgumentException("Invalid fog mode: " + this.mode);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        this.color.serialise(serialiser);
        serialiser.write(this.mode);
        if (this.mode == MODE_EXPONENTIAL)
        {
            serialiser.writeFloat(this.density);
        }
        else if (this.mode == MODE_LINEAR)
        {
            serialiser.writeFloat(this.near);
            serialiser.writeFloat(this.far);
        }
        else
        {
            assert (false);
        }
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.FOG;
    }

    public ColorRGB getColor()
    {
        return this.color;
    }

    public int getMode()
    {
        return this.mode;
    }

    public float getDensity()
    {
        return this.density;
    }

    public float getNear()
    {
        return this.near;
    }

    public float getFar()
    {
        return this.far;
    }
}
