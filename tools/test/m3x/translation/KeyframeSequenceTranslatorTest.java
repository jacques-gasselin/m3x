package m3x.translation;

import m3x.m3g.Background;
import m3x.m3g.KeyframeSequence;
import m3x.xml.BackgroundRepeatType;
import m3x.xml.KeyframeInterpolationType;
import m3x.xml.KeyframePlaybackType;
import m3x.xml.Keyframes;

/**
 * TODO: add tests
 * 
 * @author jsaarinen
 */
public class KeyframeSequenceTranslatorTest extends TranslatorSupport
{
  public void testTranslator()
  {
    KeyframeSequenceTranslator translator = new KeyframeSequenceTranslator();
        
    m3x.xml.KeyframeSequence kfseq = new m3x.xml.KeyframeSequence();
    kfseq.setDuration(2);
    kfseq.setInterpolation(KeyframeInterpolationType.SLERP);
    kfseq.setKeyframeCount(3);
    Keyframes keyframes = new Keyframes();
    keyframes.setComponentCount(1);
    keyframes.getValue().add(0.1f);
    keyframes.getValue().add(0.2f);
    keyframes.getValue().add(0.3f);
    kfseq.setKeyframes(keyframes);
    kfseq.getKeytimes().add(1);
    kfseq.getKeytimes().add(2);
    kfseq.getKeytimes().add(3);
    kfseq.setRepeatMode(KeyframePlaybackType.LOOP);
    kfseq.setValidRangeFirst(0);
    kfseq.setValidRangeLast(1);
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.Section section = new m3x.xml.Section();
    m3xRoot.getSection().add(section);
    
    m3x.xml.AnimationTrack at = new m3x.xml.AnimationTrack();
    section.getObjects().add(at);
    m3x.xml.AnimationTrackInstance ati = new m3x.xml.AnimationTrackInstance();
    ati.setRef(at);
    kfseq.setAnimationTrackInstance(ati);

    translator.set(kfseq, m3xRoot, null);
    KeyframeSequence kfseq2 = (KeyframeSequence)translator.toM3G();
   
    assertTrue(kfseq.getDuration() == kfseq2.getDuration());
    assertTrue(kfseq.getKeyframeCount() == kfseq2.getFloatKeyFrames().length);
    assertTrue(kfseq.getValidRangeFirst() == kfseq2.getValidRangeFirst());
    assertTrue(kfseq.getValidRangeLast() == kfseq2.getValidRangeLast());
  }
}
