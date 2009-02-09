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

package m3x.translation.m3g;

/**
 *
 * @author jgasseli
 */
public abstract class XmlToBinaryConverter implements BinaryConverter
{
    @SuppressWarnings("unchecked")
    public static final <T extends m3x.xml.Object3D, I extends m3x.xml.InstanceType>
    T getObjectOrInstance(T object, I instance)
    {
        if (object != null)
        {
            return object;
        }
        if (instance == null)
        {
            return null;
        }
        return (T) instance.getRef();
    }

    @SuppressWarnings("unchecked")
    public static final <T extends m3x.xml.Object3D, I extends m3x.xml.InstanceType>
    T getObjectOrInstance(Object objectOrInstance)
    {
        if (objectOrInstance == null)
        {
            return null;
        }
        try
        {
            T object = (T) objectOrInstance;
            return object;
        }
        catch (Throwable t)
        {
            assert(t instanceof ClassCastException);
        }
        
        try
        {
            I instance = (I) objectOrInstance;
            return (T) instance.getRef();
        }
        catch (Throwable t)
        {
            assert(t instanceof ClassCastException);
        }

        throw new IllegalArgumentException(
            "objectOrInstance is not a valid object or instance");
    }


    public final m3x.m3g.Object3D toBinary(BinaryTranslator translator, Object from)
    {
        return toBinary((XmlToBinaryTranslator)translator, (m3x.xml.Object3D)from);
    }

    /**
     * Convert to an M3G binary object.
     *
     * @return an M3G object representation
     */
    public abstract m3x.m3g.Object3D toBinary(XmlToBinaryTranslator translator, m3x.xml.Object3D from);

}
