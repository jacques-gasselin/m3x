package m3x.m3g.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import m3x.m3g.FileFormatException;
import m3x.m3g.M3GSupport;
import m3x.m3g.M3GTypedObject;
import m3x.m3g.ObjectTypes;
import m3x.m3g.primitives.Matrix;
import m3x.m3g.primitives.ObjectIndex;

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

    public Camera(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, byte zTarget, byte yTarget,
        ObjectIndex zReference, ObjectIndex yReference, Matrix projectionMatrix) throws FileFormatException
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

    public Camera(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, Matrix projectionMatrix) throws FileFormatException
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

    public Camera(ObjectIndex[] animationTracks, UserParameter[] userParameters,
        Matrix transform, boolean enableRendering, boolean enablePicking,
        byte alphaFactor, int scope, int projectionType, float fovy,
        float aspectRatio, float near, float far) throws FileFormatException
    {
        super(animationTracks, userParameters, transform, enableRendering,
            enablePicking, alphaFactor, scope);
        if (projectionType != PROJECTION_TYPE_PARALLEL && projectionType != PROJECTION_TYPE_PERSPECTIVE)
        {
            throw new FileFormatException("Invalid projectionType: " + projectionType);
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

    public void deserialize(DataInputStream dataInputStream, String m3gVersion)
        throws IOException, FileFormatException
    {
        super.deserialize(dataInputStream, m3gVersion);
        this.projectionType = dataInputStream.readByte() & 0xFF;
        if (this.projectionType == PROJECTION_TYPE_GENERIC)
        {
            this.projectionMatrix = new Matrix();
            this.projectionMatrix.deserialize(dataInputStream, m3gVersion);
        }
        else if (this.projectionType == PROJECTION_TYPE_PARALLEL ||
            this.projectionType == PROJECTION_TYPE_PERSPECTIVE)
        {
            this.fovy = M3GSupport.readFloat(dataInputStream);
            this.aspectRatio = M3GSupport.readFloat(dataInputStream);
            this.near = M3GSupport.readFloat(dataInputStream);
            this.far = M3GSupport.readFloat(dataInputStream);
        }
        else
        {
            throw new FileFormatException("Invalid projection type: " + this.projectionType);
        }
    }

    public void serialize(DataOutputStream dataOutputStream, String m3gVersion)
        throws IOException
    {
        super.serialize(dataOutputStream, m3gVersion);
        dataOutputStream.write(this.projectionType);
        if (this.projectionType == PROJECTION_TYPE_GENERIC)
        {
            this.projectionMatrix.serialize(dataOutputStream, m3gVersion);
        }
        else
        {
            M3GSupport.writeFloat(dataOutputStream, this.fovy);
            M3GSupport.writeFloat(dataOutputStream, this.aspectRatio);
            M3GSupport.writeFloat(dataOutputStream, this.near);
            M3GSupport.writeFloat(dataOutputStream, this.far);
        }
    }

    public byte getObjectType()
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
