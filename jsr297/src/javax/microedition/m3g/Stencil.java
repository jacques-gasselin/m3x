/*
 * Copyright (c) 2010, Jacques Gasselin de Richebourg
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

package javax.microedition.m3g;

/**
 * @author jgasseli
 */
public class Stencil extends Object3D
{
    public static final int FRONT = 131072;
    public static final int BACK = 262144;
    
    public static final int ZERO = 64;
    public static final int KEEP = 65;
    public static final int REPLACE = 66;
    public static final int INCR = 67;
    public static final int DECR = 68;
    public static final int INVERT = 69;
    public static final int INCR_WRAP = 70;
    public static final int DECR_WRAP = 71;
    
    private final int[] funcRefMaskOpsFront = new int[6];
    private final int[] funcRefMaskOpsBack = new int[6];
    private int writeMaskFront;
    private int writeMaskBack;
    
    private static final int[] DEFAULT = {
        CompositingMode.ALWAYS, 0, 0xFFFFFFFF,
        KEEP, KEEP, KEEP
    };
    
    public Stencil()
    {
        super();
        
        System.arraycopy(DEFAULT, 0, funcRefMaskOpsFront, 0, 6);
        System.arraycopy(DEFAULT, 0, funcRefMaskOpsBack, 0, 6);
        
        writeMaskFront = 0xFFFFFFFF;
        writeMaskBack = 0xFFFFFFFF;
    }

    @Override
    void duplicate(Object3D target)
    {
        super.duplicate(target);
        
        final Stencil s = (Stencil) target;
        int[] funcRefMaskOps = new int[6];
        final int[] faces = { FRONT, BACK };
        for (int i = 0; i < 2; ++i)
        {
            final int face = faces[i];
            getStencil(face, funcRefMaskOps);
            s.setStencilFunc(face, funcRefMaskOps[0], funcRefMaskOps[1], funcRefMaskOps[2]);
            s.setStencilOps(face, funcRefMaskOps[3], funcRefMaskOps[4], funcRefMaskOps[5]);
            s.setStencilWriteMask(face, getStencilWriteMask(face));
        }
    }

    private void requireValidFace(int face)
    {
        if (!((face == BACK) || (face == FRONT)))
        {
            throw new IllegalArgumentException("face must be FRONT or BACK");
        }
    }
    
    private void requireValidFaceBitmask(int face)
    {
        if ((face & (FRONT | BACK)) == 0)
        {
            throw new IllegalArgumentException("face must be FRONT, BACK or FRONT | BACK");
        }
    }
    
    private void requireValidOp(int op, String name)
    {
        if (op < ZERO)
        {
            throw new IllegalArgumentException(name + " must be >= ZERO");
        }
        else if (op > DECR_WRAP)
        {
            throw new IllegalArgumentException(name + " must be <= DECR_WRAP");
        }
    }
    
    public int getStencilWriteMask(int face)
    {
        requireValidFace(face);
        
        if (face == FRONT)
        {
            return writeMaskFront;
        }
        else
        {
            return writeMaskBack;
        }
    }
    
    public void setStencilWriteMask(int face, int mask)
    {
        requireValidFaceBitmask(face);
        
        if ((face & FRONT) != 0)
        {
            writeMaskFront = mask;
        }
        if ((face & BACK) != 0)
        {
            writeMaskBack = mask;
        }
    }
    
    public void getStencil(int face, int[] funcRefMaskOps)
    {
        requireValidFace(face);
        
        if (funcRefMaskOps == null)
        {
            throw new IllegalArgumentException("funcRefMaskOps is null");
        }
        if (funcRefMaskOps.length < 6)
        {
            throw new IndexOutOfBoundsException("funcRefMaskOps.length < 6");
        }
        
        final int[] src = (face == FRONT) ? funcRefMaskOpsFront : funcRefMaskOpsBack;
        System.arraycopy(src, 0, funcRefMaskOps, 0, 6);
    }
    
    public void setStencilFunc(int face,
                               int func,
                               int ref,
                               int mask)
    {
        requireValidFaceBitmask(face);

        if ((face & FRONT) != 0)
        {
            funcRefMaskOpsFront[0] = func;
            funcRefMaskOpsFront[1] = ref;
            funcRefMaskOpsFront[2] = mask;
        }
        if ((face & BACK) != 0)
        {
            funcRefMaskOpsBack[0] = func;
            funcRefMaskOpsBack[1] = ref;
            funcRefMaskOpsBack[2] = mask;
        }
    }
    
    public void setStencilOps(int face,
                              int stencilFailOp,
                              int stencilPassDepthFailOp,
                              int stencilPassDepthPassOp) 
    {
        requireValidFaceBitmask(face);
        requireValidOp(stencilFailOp, "stencilFailOp");
        requireValidOp(stencilPassDepthFailOp, "stencilPassDepthFailOp");
        requireValidOp(stencilPassDepthPassOp, "stencilPassDepthPassOp");
        
        if ((face & FRONT) != 0)
        {
            funcRefMaskOpsFront[3] = stencilFailOp;
            funcRefMaskOpsFront[4] = stencilPassDepthFailOp;
            funcRefMaskOpsFront[5] = stencilPassDepthPassOp;
        }
        if ((face & BACK) != 0)
        {
            funcRefMaskOpsBack[3] = stencilFailOp;
            funcRefMaskOpsBack[4] = stencilPassDepthFailOp;
            funcRefMaskOpsBack[5] = stencilPassDepthPassOp;
        }
    }
}
