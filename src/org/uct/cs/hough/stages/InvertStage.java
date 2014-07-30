package org.uct.cs.hough.stages;

public class InvertStage implements IStage
{
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & 0xFF;
                after.set(y, x, (short)(255-v));
            }
        }
        return after;
    }
}
