package m3x.microedition.m3g.awt;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.microedition.m3g.Transform;

/**
 * A turntable controller operating like the one in Blender 2.4+
 * 
 * @author jgasseli
 */
public class BlenderTurntableCameraController extends MouseAndKeyInputTransformController
{
    private float yaw;
    private float pitch;
    private float dolly;

    private float x, y, z;

    private int mx, my;

    private boolean leftDown;
    private boolean wheelDown;

    public BlenderTurntableCameraController(Component component)
    {
        this(component, 0, 0, 20);
    }

    public BlenderTurntableCameraController(Component component,
            float yaw, float pitch, float dolly)
    {
        super(component);
        
        setTransform(new Transform());
        
        this.yaw = yaw;
        this.pitch = pitch;
        this.dolly = dolly;
    }


    @Override
    public void mouseDragged(MouseEvent e)
    {
        final int x = e.getX();
        final int y = e.getY();
        final int dx = x - mx;
        final int dy = y - my;
        mx = x;
        my = y;

        final boolean emulateWheelDown = (leftDown && e.isAltDown());
        if (emulateWheelDown || wheelDown)
        {
            if (e.isShiftDown())
            {
                //pan
                this.x += dx * 0.2f;
                this.y += dy * 0.2f;
            }
            else if (e.isControlDown())
            {
                //dolly
                dolly += dy * 0.1f;
            }
            else
            {
                yaw += -dx * 0.1f;
                pitch += -dy * 0.1f;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        //reset the delta movement origin
        mx = e.getX();
        my = e.getY();

        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
            {
                leftDown = true;
                break;
            }
            case MouseEvent.BUTTON2:
            {
                wheelDown = true;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        switch (e.getButton())
        {
            case MouseEvent.BUTTON1:
            {
                leftDown = false;
                break;
            }
            case MouseEvent.BUTTON2:
            {
                wheelDown = false;
                break;
            }
        }
    }

    @Override
    public void update(double seconds)
    {
        Transform t = getTransform();
        if (t != null)
        {
            t.setIdentity();
            t.postTranslate(x, y, z);
            t.postRotate(pitch, 1, 0, 0);
            t.postRotate(yaw, 0, 1, 0);
            t.postTranslate(0, 0, dolly);
        }
    }

    
}
