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

package m3x.awt;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;


/**
 * @author jgasseli
 */
public class BaseFrame extends Frame
{
    private int refreshRate = 60;
    private int windowedWidth;
    private int windowedHeight;
    private boolean isFullscreen;

    private final class WindowAdapter extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            super.windowClosing(e);
            close();
        }
    }

    /**
     * Close the frame and stop any threads used
     */
    protected void close()
    {
        dispose();
    }

    private final void init(String title)
    {
        setTitle(title);
        setSize(800, 600);
        
        final WindowAdapter adapter = new WindowAdapter();
        addWindowFocusListener(adapter);
        addWindowListener(adapter);
        addWindowStateListener(adapter);

        windowedWidth = getWidth();
        windowedHeight = getHeight();
    }

    public BaseFrame()
    {
        super();
        init(getClass().getSimpleName());
    }

    public BaseFrame(String title)
    {
        super();
        init(title);
    }
    
    public int getRefreshRate()
    {
        return this.refreshRate;
    }

    private final void setRefreshRate(DisplayMode dm)
    {
        refreshRate = dm.getRefreshRate();
        if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN)
        {
            //default to 60Hz
            refreshRate = 60;
        }
    }

    public final void toggleFullscreen()
    {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        final GraphicsDevice gd;
        if (gc != null)
        {
            gd = gc.getDevice();
        }
        else
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            gd = ge.getDefaultScreenDevice();
        }

        if (isFullscreen)
        {
            setWindowed(gd);
        }
        else
        {
            setFullscreen(gd);
        }
    }

    public final void setWindowed(GraphicsDevice gd)
    {
        DisplayMode dm = gd.getDisplayMode();
        setRefreshRate(dm);

        final boolean wasDisplayable = isDisplayable();
        if (wasDisplayable)
        {
            dispose();
        }
        gd.setFullScreenWindow(null);
        if (isUndecorated())
        {
            setUndecorated(false);
        }
        setSize(windowedWidth, windowedHeight);
        if (wasDisplayable)
        {
            setVisible(true);
        }

        isFullscreen = false;
    }

    public final void setFullscreen(GraphicsDevice gd)
    {
        DisplayMode dm = gd.getDisplayMode();
        setRefreshRate(dm);

        final boolean wasDisplayable = isDisplayable();

        try
        {
            //try setting fullscreen mode
            if (gd.isFullScreenSupported())
            {
                //save the dimensions for later
                windowedWidth = getWidth();
                windowedHeight = getHeight();
                if (wasDisplayable)
                {
                    dispose();
                }
                if (!isUndecorated())
                {
                    setUndecorated(true);
                }
                setSize(dm.getWidth(), dm.getHeight());
                gd.setFullScreenWindow(this);
                if (wasDisplayable)
                {
                    setVisible(true);
                }
                isFullscreen = true;
            }
        }
        catch (Throwable t)
        {
            //unable to set full screen
            gd.setFullScreenWindow(null);
            if (isUndecorated())
            {
                setUndecorated(false);
            }
            setSize(windowedWidth, windowedHeight);
        }
    }

    public final void present(boolean fullscreen)
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        assert(ge != null);

        GraphicsDevice gd = ge.getDefaultScreenDevice();
        assert(gd != null);

        if (fullscreen)
        {
            setFullscreen(gd);
        }
        else
        {
            setWindowed(gd);
        }

        setVisible(true);
    }
}
