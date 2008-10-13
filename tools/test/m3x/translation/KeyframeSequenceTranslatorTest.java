package m3x.translation;

import m3x.m3g.objects.Background;
import m3x.m3g.objects.KeyframeSequence;
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
    kfseq.setComponentCount(1L);
    kfseq.setDuration(2L);
    kfseq.setInterpolation(KeyframeInterpolationType.SLERP);
    kfseq.setKeyframeCount(3L);
    Keyframes keyframes = new Keyframes();
    keyframes.setComponentSize((short)1);
    keyframes.getValue().add(0.1f);
    keyframes.getValue().add(0.2f);
    keyframes.getValue().add(0.3f);
    kfseq.setKeyframes(keyframes);
    kfseq.getKeytimes().add(1L);
    kfseq.getKeytimes().add(2L);
    kfseq.getKeytimes().add(3L);
    kfseq.setRepeatMode(KeyframePlaybackType.LOOP);
    kfseq.setValidRangeFirst(0L);
    kfseq.setValidRangeLast(1L);
    
    m3x.xml.M3G m3xRoot = new m3x.xml.M3G();
    m3x.xml.SectionType section = new m3x.xml.SectionType();
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
