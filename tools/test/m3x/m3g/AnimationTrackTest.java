package m3x.m3g;

import java.io.ByteArrayOutputStream;

public class AnimationTrackTest extends AbstractTestCase
{
    private AnimationTrack track;

    public AnimationTrackTest()
    {
        track = new AnimationTrack();
        track.setTargetProperty(AnimationTrack.TRANSLATION);

        KeyframeSequence keys = new KeyframeSequence();
        KeyframeSequence.FloatKeyFrame[] frames = new KeyframeSequence.FloatKeyFrame[3];
        frames[0] = new KeyframeSequence.FloatKeyFrame(0, new float[]{0, 0, 1});
        frames[1] = new KeyframeSequence.FloatKeyFrame(0, new float[]{1, 0, 0});
        frames[2] = new KeyframeSequence.FloatKeyFrame(0, new float[]{0, 1, 0});
        keys.setKeyframes(frames);
        keys.setInterpolationType(KeyframeSequence.STEP);
        track.setKeyframeSequence(keys);
    }

    public void testSaveAndLoad()
    {
        Object3D[] roots = new Object3D[]{ track };

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Saver.save(out, roots, "1.0", "AnimationTrackTest");

            byte[] data = out.toByteArray();

            Object3D[] loadRoots = Loader.load(data);

            for (int i = 0; i < roots.length; ++i)
            {
                doTestAccessors(roots[i], loadRoots[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

}
