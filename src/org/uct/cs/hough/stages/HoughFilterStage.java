package org.uct.cs.hough.stages;

public class HoughFilterStage implements IStage
{
    private final int minCircleRadius;
    private final int maxCircleRadius;

    public HoughFilterStage(int minCircleRadius, int maxCircleRadius)
    {
        this.minCircleRadius = minCircleRadius;
        this.maxCircleRadius = maxCircleRadius;
    }

    // TODO aggressive caching of sqrt values

    @Override
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        int[][][] houghSpace = new int[before.getHeight()][before.getWidth()][this.maxCircleRadius - this.minCircleRadius];

        return null;
    }
}
