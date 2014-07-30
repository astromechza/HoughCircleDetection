package org.uct.cs.hough.stages;

import org.uct.cs.hough.util.MathHelper;

public class SobelEdgeDetectStage implements IStage
{
    @Override
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        for(int y=1;y<before.getHeight()-1;y++)
        {
            for(int x=1;x<before.getWidth()-1;x++)
            {
                int g00 = before.get(y-1, x-1) & 0xFF;
                int g01 = before.get(y-1, x) & 0xFF;
                int g02 = before.get(y-1, x+1) & 0xFF;
                int g10 = before.get(y, x-1) & 0xFF;
                int g12 = before.get(y, x+1) & 0xFF;
                int g20 = before.get(y+1, x-1) & 0xFF;
                int g21 = before.get(y+1, x) & 0xFF;
                int g22 = before.get(y+1, x+1) & 0xFF;

                int Gx = -g00 -2*g01 -g02 +g20 +2*g21 +g22;
                int Gy = -g00 -2*g10 -g20 +g02 +2*g12 +g22;

                double v = Math.sqrt(Gy*Gy + Gx*Gx)/4;
                after.set(y, x, (short)MathHelper.clamp((int)v, 0, 0xFF));
            }
        }
        return after;
    }
}
