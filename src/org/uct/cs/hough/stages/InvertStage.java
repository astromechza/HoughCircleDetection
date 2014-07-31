package org.uct.cs.hough.stages;

import org.uct.cs.hough.util.Constants;

public class InvertStage implements IStage
{
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & Constants.BYTE;
                after.set(y, x, (short)(Constants.BYTE-v));
            }
        }
        return after;
    }
}
