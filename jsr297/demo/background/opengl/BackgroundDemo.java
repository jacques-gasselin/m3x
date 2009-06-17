package background.opengl;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.RenderTarget;
import javax.microedition.m3g.opengl.GLRenderTarget;

/**
 * @author jgasseli
 */
public class BackgroundDemo extends Frame
{
    private static class BackgroundCanvas extends GLCanvas implements Runnable
    {
        Background background;
        RenderTarget renderTarget;
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

        public void run()
        {
            while (true)
            {
                try
                {
                    Thread.sleep(40);
                }
                catch (InterruptedException e)
                {
                    //e.printStackTrace();
                }
                repaint();
            }
        }
    }

    private final class WindowAdapter extends java.awt.event.WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }

    BackgroundDemo()
    {
        super("BackgroundDemo");
        add(new BackgroundCanvas());
        addWindowListener(new WindowAdapter());
    }

    public static void main(String[] args)
    {
        Frame frame = new BackgroundDemo();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        assert(ge != null);

        GraphicsDevice gd = ge.getDefaultScreenDevice();
        assert(gd != null);

        try
        {
            //try setting fullscreen mode
            DisplayMode dm = gd.getDisplayMode();
            if (gd.isFullScreenSupported())
            {
                frame.setSize(dm.getWidth(), dm.getHeight());
                frame.setUndecorated(true);
                gd.setFullScreenWindow(frame);
            }
        }
        catch (Throwable t)
        {
            //unable to set full screen
            gd.setFullScreenWindow(null);
            frame.setUndecorated(true);
        }
        
        frame.setVisible(true);
    }

}
