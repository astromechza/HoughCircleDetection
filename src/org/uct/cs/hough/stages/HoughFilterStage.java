package org.uct.cs.hough.stages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HoughFilterStage implements IStage
{
    private final int minCircleRadius;
    private final int maxCircleRadius;
    private final ArrayList<IntPoint>[] circlePoints;
    private final boolean normaliseWithRadii;
    private final int[] circumpherenceLengths;

    public HoughFilterStage(int minCircleRadius, int maxCircleRadius, boolean normaliseWithRadii)
    {
        this.minCircleRadius = minCircleRadius;
        this.maxCircleRadius = maxCircleRadius;
        this.normaliseWithRadii = normaliseWithRadii;
        int numsizes = maxCircleRadius - minCircleRadius;
        this.circlePoints = new ArrayList[(maxCircleRadius-minCircleRadius)];
        this.circumpherenceLengths = new int[(maxCircleRadius-minCircleRadius)];
        for(int r=0;r<numsizes;r++)
        {
            this.circlePoints[r] = new ArrayList<>();
            int x = minCircleRadius + r, y = 0;
            int radiusError = 1-x;

            while(x >= y)
            {
                this.circlePoints[r].add(new IntPoint(x, y));
                y++;
                if (radiusError<0)
                {
                    radiusError += 2 * y + 1;
                }
                else
                {
                    x--;
                    radiusError += 2 * (y - x + 1);
                }
            }

            this.circumpherenceLengths[r] = this.circlePoints[r].size();
        }
    }

    @Override
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        HoughSpace space = new HoughSpace(before.getHeight(), before.getWidth(),this.minCircleRadius, this.maxCircleRadius, this.normaliseWithRadii, this.circumpherenceLengths);

        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & 0xFF;
                if (v > 0)
                {
                    int radii = this.circlePoints.length;
                    for(int r=0;r<radii;r++)
                    {
                        for(IntPoint p : this.circlePoints[r])
                        {
                            space.inc(y+p.y,x+p.x,r);
                            space.inc(y+p.x,x+p.y,r);
                            space.inc(y+p.y,x-p.x,r);
                            space.inc(y+p.x,x-p.y,r);
                            space.inc(y-p.y,x-p.x,r);
                            space.inc(y-p.x,x-p.y,r);
                            space.inc(y-p.y,x+p.x,r);
                            space.inc(y-p.x,x+p.y,r);
                        }
                    }
                }
            }
        }

        ShortImageBuffer after = before.copyShape();
        for(int y=0;y<before.getHeight();y++)
        {
            for (int x = 0; x < before.getWidth(); x++)
            {
                int v = (int)((space.getMax(y,x) / space.getBestMax()) * 255);
                after.set(y,x,(short)v);
            }
        }

        return after;
    }

    private class HoughSpace
    {
        private int[][][] data;
        private float[][] maximums;
        private int[][] maxradii;
        private int width, height, minRadii;
        private float highestMaximum;
        private boolean normaliseWithRadii;
        private int[] circumpherenceLengths;

        public HoughSpace(int height, int width, int minRadii, int maxRadii, boolean normaliseWithRadii, int[] circumpherenceLengths)
        {
            this.height = height;
            this.width = width;
            this.minRadii = minRadii;
            this.data = new int[height][width][maxRadii - minRadii];
            this.maximums = new float[height][width];
            this.maxradii = new int[height][width];
            this.normaliseWithRadii = normaliseWithRadii;
            this.circumpherenceLengths = circumpherenceLengths;
        }

        public void inc(int y, int x, int r)
        {
            if (y < 0 || y >= this.height) return;
            if (x < 0 || x >= this.width) return;

            float nv = ++data[y][x][r];
            if (normaliseWithRadii) nv /= circumpherenceLengths[r];
            if(nv > maximums[y][x])
            {
                maximums[y][x] = nv;
                maxradii[y][x] = this.minRadii + r;

                if(nv > highestMaximum) highestMaximum = nv;
            }
        }

        public float getMax(int y, int x)
        {
            return this.maximums[y][x];
        }

        public int getMaxRadii(int y, int x)
        {
            return this.maxradii[y][x];
        }

        public float getBestMax()
        {
            return this.highestMaximum;
        }
    }

    private class IntPoint
    {
        public final int x;
        public final int y;

        public IntPoint(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
