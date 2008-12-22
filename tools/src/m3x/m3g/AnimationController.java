package m3x.m3g;

import m3x.m3g.primitives.ObjectTypes;
import java.io.IOException;


/**
 * See http://java2me.org/m3g/file-format.html#AnimationController<br>
 *  Float32       speed;<br>
 *  Float32       weight;<br>
 *  Int32         activeIntervalStart;<br>
 *  Int32         activeIntervalEnd;<br>
 *  Float32       referenceSequenceTime;<br>
 *  Int32         referenceWorldTime;<br>
 *
 * @author jsaarinen
 * @author jgasseli
 */
public class AnimationController extends Object3D
{
    private float speed;
    private float weight;
    private int activeIntervalStart;
    private int activeIntervalEnd;
    private float referenceSequenceTime;
    private int referenceWorldTime;

    public AnimationController(AnimationTrack[] animationTracks,
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
     * @throws IOException
     * @throws FileFormatException
     */
    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        setSpeed(deserialiser.readFloat());
        setWeight(deserialiser.readFloat());
        setActiveIntervalStart(deserialiser.readInt());
        setActiveIntervalEnd(deserialiser.readInt());
        setReferenceSequenceTime(deserialiser.readFloat());
        setReferenceWorldTime(deserialiser.readInt());
    }

    /**
     * Serialization done as specified in the class JavaDoc.
     */
    @Override
    public void serialise(Serialiser serialiser) throws IOException
    {
        super.serialise(serialiser);
        serialiser.writeFloat(getSpeed());
        serialiser.writeFloat(getWeight());
        serialiser.writeInt(getActiveIntervalStart());
        serialiser.writeInt(getActiveIntervalEnd());
        serialiser.writeFloat(getReferenceSequenceTime());
        serialiser.writeInt(getReferenceWorldTime());
    }

    public int getSectionObjectType()
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
