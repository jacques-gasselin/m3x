package m3x.translation;


import java.util.List;

import m3x.m3g.FileFormatException;
import m3x.m3g.objects.KeyframeSequence;
import m3x.m3g.objects.Object3D;
import m3x.m3g.objects.KeyframeSequence.FloatKeyFrame;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.KeyframeInterpolationType;
import m3x.xml.KeyframePlaybackType;
import m3x.xml.Keyframes;
;

public class KeyframeSequenceTranslator extends AbstractTranslator
{

    public Object3D toM3G()
    {
        if (this.getBinaryObject() != null)
        {
            return this.getBinaryObject();
        }

        // do translation
        m3x.xml.KeyframeSequence seq = (m3x.xml.KeyframeSequence) this.getXmlObject();
        ObjectIndex[] animationTracks = this.getM3GAnimationTracks();
        Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];

        try
        {
            this.setBinaryObject(new m3x.m3g.objects.KeyframeSequence(animationTracks, userParameters, toM3G(seq.getInterpolation()), toM3G(seq.getRepeatMode()), seq.getDuration().intValue(), seq.getValidRangeFirst().intValue(), seq.getValidRangeLast().intValue(), seq.getKeyframeCount().intValue(), toM3G(seq.getKeyframes(), seq.getKeytimes())));
        }
        catch (FileFormatException e)
        {
            throw new IllegalArgumentException(e);
        }
        return this.getBinaryObject();
    }

    private FloatKeyFrame[] toM3G(Keyframes keyframes, List<Long> keyTimes)
    {
        List<Float> list = keyframes.getValue();
        int componentCount = keyframes.getComponentSize();
        FloatKeyFrame[] keyFrames = new FloatKeyFrame[list.size() / componentCount];
        for (int i = 0; i < keyFrames.length; i += componentCount)
        {
            float[] components = new float[componentCount];
            for (int j = 0; j < componentCount; j++)
            {
                components[j] = list.get(i + j).floatValue();
            }
            keyFrames[i] = new FloatKeyFrame(keyTimes.get(i).intValue(), components);
        }
        return keyFrames;
    }

    private int toM3G(KeyframePlaybackType repeatMode)
    {
        if (repeatMode.equals(KeyframePlaybackType.ADDITIVE_LOOP))
        {
            // TODO: what to return here
            throw new IllegalArgumentException(repeatMode.toString());
        }
        if (repeatMode.equals(KeyframePlaybackType.CONSTANT))
        {
            return KeyframeSequence.CONSTANT;
        }
        if (repeatMode.equals(KeyframePlaybackType.LOOP))
        {
            return KeyframeSequence.LOOP;
        }
        throw new IllegalArgumentException(repeatMode.toString());
    }

    private int toM3G(KeyframeInterpolationType interpolation)
    {
        if (interpolation.equals(KeyframeInterpolationType.LINEAR))
        {
            return KeyframeSequence.LINEAR;
        }
        if (interpolation.equals(KeyframeInterpolationType.SLERP))
        {
            return KeyframeSequence.SLERP;
        }
        if (interpolation.equals(KeyframeInterpolationType.SPLINE))
        {
            return KeyframeSequence.SPLINE;
        }
        if (interpolation.equals(KeyframeInterpolationType.SQUAD))
        {
            return KeyframeSequence.SQUAD;
        }
        if (interpolation.equals(KeyframeInterpolationType.STEP))
        {
            return KeyframeSequence.STEP;
        }
        throw new IllegalArgumentException(interpolation.toString());
    }

    public m3x.xml.Object3D toXML()
    {
        return null;
    }
}
