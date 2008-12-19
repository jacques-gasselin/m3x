package m3x.m3g;

import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.primitives.Matrix;

/**
 * This class is the data structure for Camera object in M3G format, see the URL
 * below for more information. See http://java2me.org/m3g/file-format.html#Camera<br>
  <br>
  Byte          projectionType;<br>
  IF projectionType==GENERIC, THEN<br>
    Matrix        projectionMatrix;<br>
  ELSE<br>
    Float32       fovy;<br>
    Float32       AspectRatio;<br>
    Float32       near;<br>
    Float32       far;<br>
  END<br>
 * 
 * @author jsaarinen
 */
public class Camera extends Node implements M3GTypedObject
{
    public static final int PROJECTION_TYPE_GENERIC = 48;
    public static final int PROJECTION_TYPE_PARALLEL = 49;
    public static final int PROJECTION_TYPE_PERSPECTIVE = 50;

    private int projectionType;
    private Matrix projectionMatrix;
    private float fovy;
    private float aspectRatio;
    private float near;
    private float far;

    public Camera(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, int zTarget, int yTarget,
        Node zReference, Node yReference, Matrix projectionMatrix)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope, zTarget, yTarget, zReference,
            yReference);
        this.projectionType = PROJECTION_TYPE_GENERIC;
        this.projectionMatrix = projectionMatrix;
        this.fovy = 0.0f;
        this.aspectRatio = 0.0f;
        this.near = 0.0f;
        this.far = 0.0f;
    }

    public Camera(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Matrix projectionMatrix)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        this.projectionType = PROJECTION_TYPE_GENERIC;
        this.projectionMatrix = projectionMatrix;
        this.fovy = 0.0f;
        this.aspectRatio = 0.0f;
        this.near = 0.0f;
        this.far = 0.0f;
    }

    public Camera(AnimationTrack[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, int projectionType, float fovy,
        float aspectRatio, float near, float far)
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        if (projectionType != PROJECTION_TYPE_PARALLEL && projectionType != PROJECTION_TYPE_PERSPECTIVE)
        {
            throw new IllegalArgumentException("Invalid projectionType: " + projectionType);
        }
        this.projectionType = projectionType;
        this.projectionMatrix = null;
        this.fovy = fovy;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }

    public Camera()
    {
        super();
    }

    @Override
    public void deserialize(M3GDeserialiser deserialiser)
        throws IOException
    {
        super.deserialize(deserialiser);
        this.projectionType = deserialiser.readUnsignedByte();
        if (this.projectionType == PROJECTION_TYPE_GENERIC)
        {
            this.projectionMatrix = new Matrix();
            this.projectionMatrix.deserialize(deserialiser);
        }
        else if (this.projectionType == PROJECTION_TYPE_PARALLEL ||
            this.projectionType == PROJECTION_TYPE_PERSPECTIVE)
        {
            this.fovy = deserialiser.readFloat();
            this.aspectRatio = deserialiser.readFloat();
            this.near = deserialiser.readFloat();
            this.far = deserialiser.readFloat();
        }
        else
        {
            throw new IllegalArgumentException("Invalid projection type: " + this.projectionType);
        }
    }

    @Override
    public void serialize(M3GSerialiser serialiser)
        throws IOException
    {
        super.serialize(serialiser);
        serialiser.write(this.projectionType);
        if (this.projectionType == PROJECTION_TYPE_GENERIC)
        {
            this.projectionMatrix.serialize(serialiser);
        }
        else
        {
            serialiser.writeFloat(this.fovy);
            serialiser.writeFloat(this.aspectRatio);
            serialiser.writeFloat(this.near);
            serialiser.writeFloat(this.far);
        }
    }

    public int getObjectType()
    {
        return ObjectTypes.CAMERA;
    }

    public int getProjectionType()
    {
        return this.projectionType;
    }

    public Matrix getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public float getFovy()
    {
        return this.fovy;
    }

    public float getAspectRatio()
    {
        return this.aspectRatio;
    }

    public float getNear()
    {
        return this.near;
    }

    public float getFar()
    {
        return this.far;
    }
}
