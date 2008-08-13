package m3x.translation;

import java.util.List;

import m3x.m3g.objects.Object3D;
import m3x.m3g.primitives.ObjectIndex;
import m3x.xml.Deserialiser;
import m3x.xml.Object3DType;

public class VertexArrayTranslator extends AbstractTranslator
{

  public void set(Object3DType object, Deserialiser deserialiser)
  {
    super.set((m3x.xml.VertexArray)object, deserialiser);
  }

  public void set(Object3D object)
  {
   super.set((m3x.m3g.objects.VertexArray)object);
  }

  public Object3D toM3G()
  {
    if(this.m3gObject == null){
      m3x.xml.VertexArray va = (m3x.xml.VertexArray)m3xObject;
   // FIXME: currently only 1 animationtrack?? Spec says there can be 0..n?
      ObjectIndex[] animationTracks = null;
      if(va.getAnimationTrack() != null){
        animationTracks = new ObjectIndex[1]; 
        animationTracks[0] = new ObjectIndex((int)va.getAnimationTrack().getUserID()); //TODO: check if userID is what makes the reference....
      }
      else{
        animationTracks = new ObjectIndex[0];
      }
      Object3D.UserParameter[] userParameters = new Object3D.UserParameter[0];
      List<Integer> ints = va.getIntArray();
      switch(va.getComponentType()){
        case BYTE:
          System.out.println(va.getComponentCount());
          byte[] byteComponents = new byte[ints.size()];
          for(int i = 0; i < ints.size(); i++){
            byteComponents[i] = ints.get(i).byteValue();
          }
          m3gObject = new m3x.m3g.objects.VertexArray(
              animationTracks, 
              userParameters,
              byteComponents,false);
        case SHORT:
          short[] shortComponents = new short[ints.size()];
          for(int i = 0; i < ints.size(); i++){
            shortComponents[i] = ints.get(i).byteValue();
          }
          m3gObject = new m3x.m3g.objects.VertexArray(
              animationTracks, 
              userParameters,
              shortComponents,false);
      }
    }
    //else translation is done already
    return m3gObject;
  }

  public Object3DType toXML()
  {
    if(this.m3xObject == null){
      
    }
    return m3xObject;
  }

}
