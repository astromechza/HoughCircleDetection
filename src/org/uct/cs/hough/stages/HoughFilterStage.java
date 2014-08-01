package org.uct.cs.hough.stages;

import org.uct.cs.hough.util.Constants;
import org.uct.cs.hough.util.IntIntPair;

import java.util.ArrayList;
import java.util.List;

public class HoughFilterStage implements IStage
{
    private final int minCircleRadius;
    private final int maxCircleRadius;

    private final List<List<IntIntPair>> circlePoints;
    private final int[] circumpherenceLengths;

    private final boolean normaliseWithRadii;

    private HoughSpace currentHoughSpace;

    public HoughFilterStage(int minCircleRadius, int maxCircleRadius, boolean normaliseWithRadii)
    {
        this.minCircleRadius = minCircleRadius;
        this.maxCircleRadius = maxCircleRadius;
        this.normaliseWithRadii = normaliseWithRadii;

        int numsizes = maxCircleRadius - minCircleRadius;
        this.circlePoints = new ArrayList<>();
        this.circumpherenceLengths = new int[numsizes];
        for(int r=0;r<numsizes;r++)
        {
            List<IntIntPair> points = new ArrayList<>();
            int x = minCircleRadius + r, y = 0;
            int radiusError = 1-x;

            while(x >= y)
            {
                points.add(new IntIntPair(x, y));
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

            this.circlePoints.add(points);
            this.circumpherenceLengths[r] = points.size();
        }
    }

    @Override
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        this.currentHoughSpace = new HoughSpace(before.getHeight(), before.getWidth(),this.minCircleRadius, this.maxCircleRadius, this.normaliseWithRadii, this.circumpherenceLengths);

        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & Constants.BYTE;
                if (v > 0)
                {
                    int radii = this.circlePoints.size();
                    for(int r=0;r<radii;r++)
                    {
                        for(IntIntPair p : this.circlePoints.get(r))
                        {
                            this.currentHoughSpace.inc(y+p.y,x+p.x,r);
                            this.currentHoughSpace.inc(y+p.x,x+p.y,r);
                            this.currentHoughSpace.inc(y+p.y,x-p.x,r);
                            this.currentHoughSpace.inc(y+p.x,x-p.y,r);
                            this.currentHoughSpace.inc(y-p.y,x-p.x,r);
                            this.currentHoughSpace.inc(y-p.x,x-p.y,r);
                            this.currentHoughSpace.inc(y-p.y,x+p.x,r);
                            this.currentHoughSpace.inc(y-p.x,x+p.y,r);
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
                int v = (int)((this.currentHoughSpace.getMax(y,x) / this.currentHoughSpace.getBestMax()) * Constants.BYTE);
                after.set(y,x,(short)v);
            }
        }

        return after;
    }

    public HoughSpace getLastHoughSpace()
    {
        return this.currentHoughSpace;
    }

    public class HoughSpace
    {
        private final int[][][] data;
        private final float[][] maximums;
        private final int[][] maxradii;
        private final int width, height, minRadii;
        private float highestMaximum;
        private final boolean normaliseWithRadii;
        private final int[] circumpherenceLengths;

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
}
