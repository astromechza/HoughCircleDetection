package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;

/**
 * HoughFilter Class
 */
public class HoughFilter
{

    public static int[] run(ShortImageBuffer edges, final int minRadius, final int maxRadius)
    {
        // sanity checks
        if (minRadius >= maxRadius || minRadius < 0 || maxRadius < 0)
            throw new IllegalArgumentException("min radius and max radius are invalid");

        // dimensions
        int height = edges.getHeight();
        int heightWithBorder = height + 2* maxRadius;
        int width = edges.getWidth();
        int widthWithBorder = width + 2* maxRadius;
        int depth = maxRadius - minRadius;

        // create hough space
        int[] space = new int[heightWithBorder*widthWithBorder*depth];
        int nx,ny,ay,ax,cx,cy,radiusError,sx,sy;
        int memBOffset = widthWithBorder*depth;
        for(int y=0;y<height;y++)
        {
            ay = (y + maxRadius) * memBOffset;
            for(int x=0;x<width;x++)
            {
                if (edges.get(y,x) != 0)
                {
                    ax = (x + maxRadius) * depth;
                    for(int r=0;r<depth;r++)
                    {
                        cx = minRadius + r;
                        cy = 0;
                        radiusError = 1-cx;
                        while(cx >= cy)
                        {

                            // highly optimised index calculation and increment
                            // this may look strange but it has been carefully worked out

                            sx = cx * depth;
                            sy = cy * memBOffset;

                            nx = ax + sx;
                            ny = ay + sy;
                            space[ny + nx + r]++;
                            nx = ax - sx;
                            space[ny + nx + r]++;
                            ny = ay - sy;
                            space[ny + nx + r]++;
                            nx = ax + sx;
                            space[ny + nx + r]++;

                            sx = cy * depth;
                            sy = cx * memBOffset;

                            nx = ax + sx;
                            ny = ay + sy;
                            space[ny + nx + r]++;
                            ny = ay - sy;
                            space[ny + nx + r]++;
                            nx = ax - sx;
                            space[ny + nx + r]++;
                            ny = ay + sy;
                            space[ny + nx + r]++;

                            cy++;

                            if (radiusError >= 0) radiusError -= 2 * (cx--);

                            radiusError += 2 * cy + 1;
                        }
                    }
                }
            }
        }
        return space;
    }
}
