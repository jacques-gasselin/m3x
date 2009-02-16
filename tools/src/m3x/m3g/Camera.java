/**
 * Copyright (c) 2008, Jacques Gasselin de Richebourg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package m3x.m3g;

import java.util.List;
import m3x.m3g.primitives.ObjectTypes;
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
 * @author jgasseli
 */
public class Camera extends Node
{
    public static final int GENERIC = 48;
    public static final int PARALLEL = 49;
    public static final int PERSPECTIVE = 50;

    private int projectionType;
    private Matrix projectionMatrix;
    private float fovy;
    private float aspectRatio;
    private float near;
    private float far;

    public Camera()
    {
        super();
    }

    @Override
    public void deserialise(Deserialiser deserialiser)
        throws IOException
    {
        super.deserialise(deserialiser);
        this.projectionType = deserialiser.readUnsignedByte();
        if (this.projectionType == GENERIC)
        {
            this.projectionMatrix = new Matrix();
            this.projectionMatrix.deserialise(deserialiser);
        }
        else if (this.projectionType == PARALLEL ||
            this.projectionType == PERSPECTIVE)
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
    public void serialise(Serialiser serialiser)
        throws IOException
    {
        super.serialise(serialiser);
        serialiser.write(this.projectionType);
        if (this.projectionType == GENERIC)
        {
            this.projectionMatrix.serialise(serialiser);
        }
        else
        {
            serialiser.writeFloat(this.fovy);
            serialiser.writeFloat(this.aspectRatio);
            serialiser.writeFloat(this.near);
            serialiser.writeFloat(this.far);
        }
    }

    public int getSectionObjectType()
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

    public void setGeneric(List<Float> projectionTransform)
    {
        this.projectionType = GENERIC;
        this.projectionMatrix = new Matrix(projectionTransform);
    }

    public void setParallel(float fovy, float aspectRatio, float near, float far)
    {
        this.projectionType = PARALLEL;
        this.fovy = fovy;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }

    public void setPerspective(float fovy, float aspectRatio, float near, float far)
    {
        this.projectionType = PERSPECTIVE;
        this.fovy = fovy;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }
}
