package m3x.m3g.objects;

import m3x.m3g.AbstractTestCase;
import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.objects.KeyframeSequence.FloatKeyFrame;
import m3x.m3g.objects.KeyframeSequence.ByteKeyFrame;
import m3x.m3g.objects.KeyframeSequence.ShortKeyFrame;
import m3x.m3g.objects.Object3D.UserParameter;
import m3x.m3g.primitives.ColorRGB;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

public class KeyframeSequenceTest extends AbstractTestCase
{
  public void testSerializationAndDeserialization1()
  {       
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    FloatKeyFrame[] keyFrames = new FloatKeyFrame[1];
    float[] vectorValue = new float[] {0.1f, 0.2f, 0.3f};
    keyFrames[0] = new FloatKeyFrame(1, vectorValue);
    try
    {   
      KeyframeSequence seq = new KeyframeSequence(animationTracks,
                                                  userParameters,
                                                  KeyframeSequence.LINEAR,
                                                  KeyframeSequence.CONSTANT,
                                                  1,
                                                  2,
                                                  3,
                                                  3,
                                                  keyFrames);
      byte[] serialized = M3GSupport.objectToBytes(seq);
      KeyframeSequence deserialized = (KeyframeSequence)M3GSupport.bytesToObject(serialized, KeyframeSequence.class);
      this.doTestAccessors(deserialized, seq);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testSerializationAndDeserialization2()
  {       
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ByteKeyFrame[] keyFrames = new ByteKeyFrame[1];
    byte[] vectorValue = new byte[] {1, 2, 3};
    keyFrames[0] = new ByteKeyFrame(1, vectorValue);
    try
    {   
      KeyframeSequence seq = new KeyframeSequence(animationTracks,
                                                  userParameters,
                                                  KeyframeSequence.LINEAR,
                                                  KeyframeSequence.CONSTANT,
                                                  1,
                                                  2,
                                                  3,
                                                  3,
                                                  keyFrames,
                                                  new float[] {0.1f, 0.2f, 0.3f},
                                                  new float[] {0.4f, 0.5f, 0.6f});
      byte[] serialized = M3GSupport.objectToBytes(seq);
      KeyframeSequence deserialized = (KeyframeSequence)M3GSupport.bytesToObject(serialized, KeyframeSequence.class);
      this.doTestAccessors(deserialized, seq);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testSerializationAndDeserialization3()
  {       
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ShortKeyFrame[] keyFrames = new ShortKeyFrame[1];
    short[] vectorValue = new short[] {1, 2, 3};
    keyFrames[0] = new ShortKeyFrame(1, vectorValue);
    try
    {   
      KeyframeSequence seq = new KeyframeSequence(animationTracks,
                                                  userParameters,
                                                  KeyframeSequence.SLERP,
                                                  KeyframeSequence.LOOP,
                                                  1,
                                                  2,
                                                  3,
                                                  3,
                                                  keyFrames,
                                                  new float[] {0.1f, 0.2f, 0.3f},
                                                  new float[] {0.4f, 0.5f, 0.6f});
      byte[] serialized = M3GSupport.objectToBytes(seq);
      KeyframeSequence deserialized = (KeyframeSequence)M3GSupport.bytesToObject(serialized, KeyframeSequence.class);
      this.doTestAccessors(deserialized, seq);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testFileFormatException1()
  {       
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ShortKeyFrame[] keyFrames = new ShortKeyFrame[1];
    short[] vectorValue = new short[] {1, 2, 3};
    keyFrames[0] = new ShortKeyFrame(1, vectorValue);
    try
    {   
      KeyframeSequence seq = new KeyframeSequence(animationTracks,
                                                  userParameters,
                                                  -1,
                                                  KeyframeSequence.LOOP,
                                                  1,
                                                  2,
                                                  3,
                                                  3,
                                                  keyFrames,
                                                  new float[] {0.1f, 0.2f, 0.3f},
                                                  new float[] {0.4f, 0.5f, 0.6f});
      byte[] serialized = M3GSupport.objectToBytes(seq);
      KeyframeSequence deserialized = (KeyframeSequence)M3GSupport.bytesToObject(serialized, KeyframeSequence.class);
      this.doTestAccessors(deserialized, seq);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException not thrown!");
  }

  public void testFileFormatException2()
  {       
    ObjectIndex[] animationTracks = getAnimationTracks();
    UserParameter[] userParameters = getUserParameters();
    ShortKeyFrame[] keyFrames = new ShortKeyFrame[1];
    short[] vectorValue = new short[] {1, 2, 3};
    keyFrames[0] = new ShortKeyFrame(1, vectorValue);
    try
    {   
      KeyframeSequence seq = new KeyframeSequence(animationTracks,
                                                  userParameters,
                                                  KeyframeSequence.CONSTANT,
                                                  -1,
                                                  1,
                                                  2,
                                                  3,
                                                  3,
                                                  keyFrames,
                                                  new float[] {0.1f, 0.2f, 0.3f},
                                                  new float[] {0.4f, 0.5f, 0.6f});
      byte[] serialized = M3GSupport.objectToBytes(seq);
      KeyframeSequence deserialized = (KeyframeSequence)M3GSupport.bytesToObject(serialized, KeyframeSequence.class);
      this.doTestAccessors(deserialized, seq);
    }
    catch (Exception e)
    {
      return;
    }
    fail("FileFormatException not thrown!");
  }
}
