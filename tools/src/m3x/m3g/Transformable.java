package m3x.m3g;

import java.util.List;
import m3x.m3g.primitives.Serialisable;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.Vector3D;

/**
 * See http://java2me.org/m3g/file-format.html#Transformable<br>
  Boolean       hasComponentTransform;<br>
  IF hasComponentTransform==TRUE, THEN<br>
  Vector3D      translation;<br>
  Vector3D      scale;<br>
  Float32       orientationAngle;<br>
  Vector3D      orientationAxis;<br>
  END<br>
  Boolean       hasGeneralTransform;<br>
  IF hasGeneralTransform==TRUE, THEN<br>
    Matrix        transform;<br>
  END<br>
  <br>
 * @author jsaarinen
 * @author jgasseli
 */
public abstract class Transformable extends Object3D implements Serialisable
{
    private boolean hasComponentTransform;
    private Vector3D translation;
    private Vector3D scale;
    private float orientationAngle;
    private Vector3D orientationAxis;
    private boolean hasGeneralTransform;
    private Matrix transform;

    public Transformable(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Vector3D translation, Vector3D scale,
        float orientationAngle, Vector3D orientationAxis)
    {
        super(animationTracks, userParameters);
        this.hasComponentTransform = true;
        this.translation = translation;
        this.scale = scale;
        this.orientationAngle = orientationAngle;
        this.orientationAxis = orientationAxis;
        this.hasGeneralTransform = false;
        this.transform = null;
    }

    public Transformable()
    {
        super();
    }

    public Transformable(AnimationTrack[] animationTracks,
        UserParameter[] userParameters, Matrix transform)
    {
        super(animationTracks, userParameters);
        this.hasComponentTransform = false;
        this.translation = null;
        this.scale = null;
        this.orientationAngle = 0.0f;
        this.orientationAxis = null;
        this.hasGeneralTransform = true;
        this.transform = transform;
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.hasComponentTransform = deserialiser.readBoolean();
        if (this.hasComponentTransform)
        {
            this.translation = new Vector3D();
            this.translation.deserialise(deserialiser);
            this.scale = new Vector3D();
            this.scale.deserialise(deserialiser);
            this.orientationAngle = deserialiser.readFloat();
            this.orientationAxis = new Vector3D();
            this.orientationAxis.deserialise(deserialiser);
            this.hasGeneralTransform = false;
        }
        this.hasGeneralTransform = deserialiser.readBoolean();
        if (this.hasGeneralTransform)
        {
            this.hasComponentTransform = false;
            this.transform = new Matrix();
            this.transform.deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        if (this.hasComponentTransform)
        {
            serialiser.writeBoolean(true);
            this.translation.serialise(serialiser);
            this.scale.serialise(serialiser);
            serialiser.writeFloat(this.orientationAngle);
            this.orientationAxis.serialise(serialiser);
            serialiser.writeBoolean(false);
        }
        else if (this.hasGeneralTransform)
        {
            serialiser.writeBoolean(false);
            serialiser.writeBoolean(true);
            this.transform.serialise(serialiser);
        }
    }

    public boolean isHasComponentTransform()
    {
        return this.hasComponentTransform;
    }

    public Vector3D getTranslation()
    {
        return this.translation;
    }

    public Vector3D getScale()
    {
        return this.scale;
    }

    public float getOrientationAngle()
    {
        return this.orientationAngle;
    }

    public Vector3D getOrientationAxis()
    {
        return this.orientationAxis;
    }

    public boolean isHasGeneralTransform()
    {
        return this.hasGeneralTransform;
    }

    public Matrix getTransform()
    {
        return this.transform;
    }

    public void setOrientation(float angle, List<Float> axis)
    {
        if (axis == null)
        {
            return;
        }
        if (axis.size() != 3)
        {
            throw new IllegalArgumentException("orientation axis needs 3 elements");
        }
        setOrientation(angle, axis.get(0), axis.get(1), axis.get(2));
    }

    public void setOrientation(float angle, float x, float y, float z)
    {
        this.orientationAngle = angle;
        this.orientationAxis.set(x, y, z);
    }

    public void setScale(List<Float> scale)
    {
        if (scale == null)
        {
            return;
        }
        if (scale.size() != 3)
        {
            throw new IllegalArgumentException("scale needs 3 elements");
        }
        setScale(scale.get(0), scale.get(1), scale.get(2));
    }

    public void setScale(float x, float y, float z)
    {
        scale.set(x, y, z);
    }

    public void setTransform(List<Float> transform)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setTranslation(List<Float> translation)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
