package m3x.m3g.util;

import m3x.m3g.Object3D;

/**
 *
 * @author jgasseli
 */
public class Object3DReferences
{
    private int offset;
    private final Object3D[] references;
    
    public Object3DReferences(int offset, Object3D[] references)
    {
        this.offset = offset;
        this.references = references;
    }

    public void add(Object3D object)
    {
        if (object != null)
        {
            if (references != null)
            {
                if (references.length < offset)
                {
                    throw new IllegalArgumentException("references.length < count");
                }
                references[offset] = object;
            }
            ++offset;
        }
    }

    public int size()
    {
        return offset;
    }
}
