/*
 * Copyright (c) 2009-2010, Jacques Gasselin de Richebourg
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

package m3x.microedition.m3g;

import javax.microedition.m3g.Transform;

/**
 * A controller for M3G Transform objects.
 * 
 * @author jgasseli
 */
public interface TransformController
{
    /**
     * Gets the transform set in setTransform(Transform).
     * @return the currently set transform
     * @see #setTransform(javax.microedition.m3g.Transform)
     */
    public Transform getTransform();

    /**
     * Sets the transform object to update at each call to update(double).
     * Passing null as the parameter will unset the current transform and
     * disable updates.
     * @param transform the transform to set, or null to unset
     * @see #update(double)
     * @see #getTransform()
     */
    public void setTransform(Transform transform);

    /**
     * Updates the currently set transform with regards to all the input data the controller
     * has received. The seconds parameter may be used for intepolation of values
     * over time, but may also be ignored.
     *
     * If there is no current transform set, this method may still interpolate
     * values and otherwise maintain state. Thus unsetting the transform does
     * not guarantee that this method becomes a no-op call.
     *
     * @param seconds the nuber of seconds that has passed since the last update.
     * @see #setTransform(javax.microedition.m3g.Transform)
     */
    public void update(double seconds);
}
