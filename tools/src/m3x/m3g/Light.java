package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;

import m3x.m3g.primitives.ColorRGB;

/**
 * See http://java2me.org/m3g/file-format.html#Light<br>
  Float32       attenuationConstant;<br>
  Float32       attenuationLinear;<br>
  Float32       attenuationQuadratic;<br>
  ColorRGB      color;<br>
  Byte          mode;<br>
  Float32       intensity;<br>
  Float32       spotAngle;<br>
  Float32       spotExponent;<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public class Light extends Node
{
    public final static int AMBIENT = 128;
    public final static int DIRECTIONAL = 129;
    public final static int OMNI = 130;
    public final static int SPOT = 131;
    
    private float constantAttenuation;
    private float linearAttenuation;
    private float quadraticAttenuation;
    private ColorRGB color;
    private int mode;
    private float intensity;
    private float spotAngle;
    private float spotExponent;

    private static void requireValidMode(int mode)
    {
        if (mode < AMBIENT || mode > SPOT)
        {
            throw new IllegalArgumentException("Invalid light mode: " + mode);
        }
    }

    public Light()
    {
        super();
        this.color = new ColorRGB();
        setMode(DIRECTIONAL);
        setColor(0xffffff);
        setIntensity(1.0f);
        setAttenuation(1.0f, 0.0f, 0.0f);
        setSpotAngle(45.0f);
        setSpotExponent(0.0f);
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        final float constant = deserialiser.readFloat();
        final float linear = deserialiser.readFloat();
        final float quadratic = deserialiser.readFloat();
        setAttenuation(constant, linear, quadratic);
        this.color.deserialise(deserialiser);
        setMode(deserialiser.readUnsignedByte());
        setIntensity(deserialiser.readFloat());
        setSpotAngle(deserialiser.readFloat());
        setSpotExponent(deserialiser.readFloat());
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeFloat(getConstantAttenuation());
        serialiser.writeFloat(getLinearAttenuation());
        serialiser.writeFloat(getQuadraticAttenuation());
        this.color.serialise(serialiser);
        serialiser.writeByte(getMode());
        serialiser.writeFloat(getIntensity());
        serialiser.writeFloat(getSpotAngle());
        serialiser.writeFloat(getSpotExponent());
    }

    public int getSectionObjectType()
    {
        return ObjectTypes.LIGHT;
    }

    public float getConstantAttenuation()
    {
        return this.constantAttenuation;
    }

    public float getLinearAttenuation()
    {
        return this.linearAttenuation;
    }

    public float getQuadraticAttenuation()
    {
        return this.quadraticAttenuation;
    }

    public ColorRGB getColor()
    {
        return this.color;
    }

    public int getMode()
    {
        return this.mode;
    }

    public float getIntensity()
    {
        return this.intensity;
    }

    public float getSpotAngle()
    {
        return this.spotAngle;
    }

    public float getSpotExponent()
    {
        return this.spotExponent;
    }

    public void setMode(int mode)
    {
        requireValidMode(mode);
        this.mode = mode;
    }

    public void setAttenuation(float constant, float linear, float quadratic)
    {
        this.constantAttenuation = constant;
        this.linearAttenuation = linear;
        this.quadraticAttenuation = quadratic;
    }

    public void setColor(int rgb)
    {
        this.color.set(rgb);
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    public void setSpotAngle(float angle)
    {
        this.spotAngle = angle;
    }

    public void setSpotExponent(float exponent)
    {
        this.spotExponent = exponent;
    }
}
