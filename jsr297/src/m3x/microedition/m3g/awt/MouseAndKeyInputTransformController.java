/*
 * Copyright (c) 2008-2009, Jacques Gasselin de Richebourg
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

package m3x.microedition.m3g.awt;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import m3x.microedition.m3g.TransformController;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import javax.microedition.m3g.Transform;

/**
 * @author jgasseli
 */
public abstract class MouseAndKeyInputTransformController
        implements TransformController, MouseListener,
        MouseMotionListener, MouseWheelListener, KeyListener
{
    private Transform transform;
    private Component component;

    public MouseAndKeyInputTransformController(Component component)
    {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
        component.addKeyListener(this);

        this.component = component;
    }

    public final Component getComponent()
    {
        return this.component;
    }

    public Transform getTransform()
    {
        return this.transform;
    }
    
    public void setTransform(Transform transform)
    {
        this.transform = transform;
    }

    public void update(double seconds)
    {
        return;
    }

    public void mouseClicked(MouseEvent e)
    {
        return;
    }

    public void mousePressed(MouseEvent e)
    {
        return;
    }

    public void mouseReleased(MouseEvent e)
    {
        return;
    }

    public void mouseEntered(MouseEvent e)
    {
        return;
    }

    public void mouseExited(MouseEvent e)
    {
        return;
    }

    public void mouseDragged(MouseEvent e)
    {
        return;
    }

    public void mouseMoved(MouseEvent e)
    {
        return;
    }

    public void keyTyped(KeyEvent e)
    {
        return;
    }

    public void keyPressed(KeyEvent e)
    {
        return;
    }

    public void keyReleased(KeyEvent e)
    {
        return;
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        return;
    }
}
