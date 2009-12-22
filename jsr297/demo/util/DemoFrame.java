package util;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;

/**
 * @author jgasseli
 */
public abstract class DemoFrame extends Frame
{
    private int refreshRate = 60;
    
    private final class WindowAdapter extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    public DemoFrame()
    {
        super();
        setTitle(getClass().getSimpleName());
        addWindowListener(new WindowAdapter());
    }

    public DemoFrame(String title)
    {
        super(title);
        addWindowListener(new WindowAdapter());
    }

    public int getRefreshRate()
    {
        return this.refreshRate;
    }

    public final void present(boolean fullscreen)
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        assert(ge != null);

        GraphicsDevice gd = ge.getDefaultScreenDevice();
        assert(gd != null);

        DisplayMode dm = gd.getDisplayMode();
        refreshRate = dm.getRefreshRate();
        if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN)
        {
            //default to 60Hz
            refreshRate = 60;
        }
        
        if (fullscreen)
        {
            try
            {
                //try setting fullscreen mode
                if (gd.isFullScreenSupported())
                {
                    setSize(dm.getWidth(), dm.getHeight());
                    setUndecorated(true);
                    gd.setFullScreenWindow(this);
                }
            }
            catch (Throwable t)
            {
                //unable to set full screen
                gd.setFullScreenWindow(null);
                setUndecorated(true);
            }
        }
        else
        {
            setSize(800, 600);
        }

        setVisible(true);
    }
}
