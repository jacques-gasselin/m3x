package m3x.m3g;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.ObjectIndex;

/**
 * See http://java2me.org/m3g/file-format.html#AnimationController<br>
    Float32       speed;<br>
    Float32       weight;<br>
    Int32         activeIntervalStart;<br>
    Int32         activeIntervalEnd;<br>
    Float32       referenceSequenceTime;<br>
    Int32         referenceWorldTime;<br>

 * @author jsaarinen
 *
 */
public class AnimationController extends Object3D implements M3GTypedObject
{
    private float speed;
    private float weight;
    private int activeIntervalStart;
    private int activeIntervalEnd;
    private float referenceSequenceTime;
    private int referenceWorldTime;

    public AnimationController(ObjectIndex[] animationTracks,
        UserParameter[] userParameters, float speed, float weight,
        int activeIntervalStart, int activeIntervalEnd,
        float referenceSequenceTime, int referenceWorldTime)
    {
        super(animationTracks, userParameters);
        this.setSpeed(speed);
        this.setWeight(weight);
        this.setActiveIntervalStart(activeIntervalStart);
        this.setActiveIntervalEnd(activeIntervalEnd);
        this.setReferenceSequenceTime(referenceSequenceTime);
        this.setReferenceWorldTime(referenceWorldTime);
    }

    public AnimationController()
    {
        super();
    }

    /**
     * Deserialization done as specified in the class JavaDoc.
     */
    @Override
    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.setSpeed(M3GSupport.readFloat(dataInputStream));
        this.setWeight(M3GSupport.readFloat(dataInputStream));
        this.setActiveIntervalStart(M3GSupport.readInt(dataInputStream));
        this.setActiveIntervalEnd(M3GSupport.readInt(dataInputStream));
        this.setReferenceSequenceTime(M3GSupport.readFloat(dataInputStream));
        this.setReferenceWorldTime(M3GSupport.readInt(dataInputStream));
    }

    /**
     * Serialization done as specified in the class JavaDoc.
     */
    @Override
    public void serialize(DataOutputStream dataOutputStream, String m3gVersion) throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        M3GSupport.writeFloat(dataOutputStream,this.getSpeed());
        M3GSupport.writeFloat(dataOutputStream,this.getWeight());
        M3GSupport.writeInt(dataOutputStream,this.getActiveIntervalStart());
        M3GSupport.writeInt(dataOutputStream,this.getActiveIntervalEnd());
        M3GSupport.writeFloat(dataOutputStream,this.getReferenceSequenceTime());
        M3GSupport.writeInt(dataOutputStream,this.getReferenceWorldTime());
    }

    public int getObjectType()
    {
        return ObjectTypes.ANIMATION_CONTROLLER;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public float getWeight()
    {
        return this.weight;
    }

    public int getActiveIntervalStart()
    {
        return this.activeIntervalStart;
    }

    public int getActiveIntervalEnd()
    {
        return this.activeIntervalEnd;
    }

    public float getReferenceSequenceTime()
    {
        return this.referenceSequenceTime;
    }

    public int getReferenceWorldTime()
    {
        return this.referenceWorldTime;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public void setWeight(float weight)
    {
        this.weight = weight;
    }

    public void setActiveIntervalStart(int activeIntervalStart)
    {
        this.activeIntervalStart = activeIntervalStart;
    }

    public void setActiveIntervalEnd(int activeIntervalEnd)
    {
        this.activeIntervalEnd = activeIntervalEnd;
    }

    public void setReferenceSequenceTime(float referenceSequenceTime)
    {
        this.referenceSequenceTime = referenceSequenceTime;
    }

    public void setReferenceWorldTime(int referenceWorldTime)
    {
        this.referenceWorldTime = referenceWorldTime;
    }
}
