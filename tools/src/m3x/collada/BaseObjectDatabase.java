/**
 * Copyright (c) 2008-2010, Jacques Gasselin de Richebourg
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

package m3x.collada;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * @author jgasseli
 */
public abstract class BaseObjectDatabase
{
    private static BaseObjectDatabase current;

    private final Hashtable<String, BaseObject> objectsById =
            new Hashtable<String, BaseObject>();

    BaseObjectDatabase()
    {
        current = this;
    }

    static void registerObjectCreation(BaseObject object)
    {
        final BaseObjectDatabase db = current;

        //check for the id property using reflection
        for (Method method : object.getClass().getMethods())
        {
            final String methodName = method.getName();
            if (methodName.equals("getId"))
            {
                try
                {
                    final String id = (String) method.invoke(object, (Object[])null);
                    db.objectsById.put(id, object);
                }
                catch (IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
                catch (IllegalArgumentException ex)
                {
                    ex.printStackTrace();
                }
                catch (InvocationTargetException ex)
                {
                    ex.printStackTrace();
                }
                
            }
        }
    }

    public BaseObject getObjectByID(String id)
    {
        return objectsById.get(id);
    }
}
