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
    private Vector3D translation;
    private Vector3D scale;
    private float orientationAngle;
    private Vector3D orientationAxis;
    private Matrix transform;

    public Transformable()
    {
        super();
        this.translation = new Vector3D();
        this.scale = new Vector3D();
        this.orientationAxis = new Vector3D();
        
        setScale();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        boolean hasComponentTransform = deserialiser.readBoolean();
        if (hasComponentTransform)
        {
            this.translation = new Vector3D();
            this.translation.deserialise(deserialiser);
            this.scale = new Vector3D();
            this.scale.deserialise(deserialiser);
            this.orientationAngle = deserialiser.readFloat();
            this.orientationAxis = new Vector3D();
            this.orientationAxis.deserialise(deserialiser);
        }
        boolean hasGeneralTransform = deserialiser.readBoolean();
        if (hasGeneralTransform)
        {
            this.transform = new Matrix();
            this.transform.deserialise(deserialiser);
        }
    }

    @Override
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        final boolean hasComponentTransform = true;
        serialiser.writeBoolean(hasComponentTransform);
        if (hasComponentTransform)
        {
            this.translation.serialise(serialiser);
            this.scale.serialise(serialiser);
            serialiser.writeFloat(this.orientationAngle);
            this.orientationAxis.serialise(serialiser);
        }
        final boolean hasGeneralTransform = transform != null;
        serialiser.writeBoolean(hasGeneralTransform);
        if (hasGeneralTransform)
        {
            this.transform.serialise(serialiser);
        }
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

    public Matrix getTransform()
    {
        return this.transform;
    }

    public void setOrientation(float angle, List<Float> axis)
    {
        if (axis == null || axis.size() == 0)
        {
            setOrientation();
        }
        else
        {
            this.orientationAxis.set(axis);
            this.orientationAngle = angle;
        }
    }

    public void setOrientation(float angle, float x, float y, float z)
    {
        this.orientationAngle = angle;
        this.orientationAxis.set(x, y, z);
    }

    private void setOrientation()
    {
        setOrientation(0, 0, 0, 0);
    }

    public void setTransform(List<Float> transform)
    {
        if (transform == null || transform.size() == 0)
        {
            setTransform();
        }
        else
        {
            this.transform = new Matrix(transform);
        }
    }

    public void setScale(List<Float> scale)
    {
        if (scale == null || scale.size() == 0)
        {
            setScale();
        }
        else
        {
            this.scale.set(scale);
        }
    }

    public void setScale(float x, float y, float z)
    {
        scale.set(x, y, z);
    }

    private void setScale()
    {
        setScale(1.0f, 1.0f, 1.0f);
    }

    public void setTranslation(List<Float> translation)
    {
        if (translation == null || translation.size() == 0)
        {
            setTranslation();
        }
        else
        {
            this.translation.set(translation);
        }
    }

    public void setTranslation(float x, float y, float z)
    {
        this.translation.set(x, y, z);
    }

    private void setTransform()
    {
        this.transform = null;
    }
    
    private void setTranslation()
    {
        setTranslation(0.0f, 0.0f, 0.0f);
    }

}
