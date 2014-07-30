package org.uct.cs.hough.stages;

public class ThresholdStage implements IStage
{
    private final int threshold;

    public ThresholdStage(int threshold)
    {
        this.threshold = threshold;
    }

    @Override
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & 0xFF;
                if (v > this.threshold) after.set(y, x, (short)(v));
            }
        }
        return after;
    }
}
