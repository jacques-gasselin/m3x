/*
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

package background.opengl;

import java.awt.Color;
import java.awt.Graphics;
import com.jogamp.opengl.awt.GLCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.AbstractRenderTarget;
import javax.microedition.m3g.opengl.GLRenderTarget;
import m3x.awt.BaseFrame;

/**
 * @author jgasseli
 */
public class BackgroundDemo extends BaseFrame
{
    private static final long serialVersionUID = 1L;

    private final class BackgroundCanvas extends GLCanvas
            implements Runnable
    {
        private static final long serialVersionUID = 1L;

        Background background;
        AbstractRenderTarget renderTarget;
        float hue;
        final float saturation = 1.0f;
        final float brightness = 1.0f;

        public BackgroundCanvas()
        {
            renderTarget = new GLRenderTarget(this);
            background = new Background();

            new Thread(this).start();
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            Graphics3D g3d = Graphics3D.getInstance();

            try
            {
                g3d.bindTarget(renderTarget);
                final int color = Color.HSBtoRGB(hue, saturation, brightness);
                background.setColor(color);
                hue += 0.01f;
                g3d.clear(background);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            finally
            {
                g3d.releaseTarget();
            }
        }

        @Override
        public void run()
        {
            while (!isClosed())
            {
                try
                {
                    Thread.sleep(1000 / getRefreshRate());
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                repaint();
            }
        }
    }

    BackgroundDemo()
    {
        super("BackgroundDemo");
        add(new BackgroundCanvas());
    }

    public static void main(String[] args)
    {
        BaseFrame frame = new BackgroundDemo();
        frame.present(false);
    }

}
