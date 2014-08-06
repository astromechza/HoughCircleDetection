package org.uct.cs.hough.processors;

import org.uct.cs.hough.CircleDetection;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;

import java.util.ArrayList;
import java.util.List;

public class HoughFilter
{
    public static List<Circle> identify(ShortImageBuffer edges, float centerThreshold)
    {
        int border = CircleDetection.MAX_RADIUS;
        int height = edges.getHeight();
        int heightWithBorder = height + 2*border;
        int width = edges.getWidth();
        int widthWithBorder = width + 2*border;
        int depth = CircleDetection.MAX_RADIUS - CircleDetection.MIN_RADIUS;

        // create hough space
        int[] space = new int[heightWithBorder*widthWithBorder*depth];
        int nx,ny,ay,ax,cx,cy,radiusError,sx,sy;
        int memBOffset = widthWithBorder*depth;
        for(int y=0;y<height;y++)
        {
            ay = (y + border) * memBOffset;
            for(int x=0;x<width;x++)
            {
                if (edges.get(y,x) != 0)
                {
                    ax = (x + border) * depth;
                    for(int r=0;r<depth;r++)
                    {
                        cx = CircleDetection.MIN_RADIUS + r;
                        cy = 0;
                        radiusError = 1-cx;
                        while(cx >= cy)
                        {
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

        // cache circumference lengths
        float[] cl = new float[depth];
        for(int r=0;r<depth;r++)
        {
            int py = CircleDetection.MIN_RADIUS + r;
            int px = 0;
            int d = (5-py*4)/4;
            do
            {
                cl[r] += 8;
                if (d >= 0) d += - 2 * py--;
                d += 2 * ++px;
            }
            while(px <= py);
            cl[r] *= centerThreshold;
        }

        List<Circle> output = new ArrayList<>();
           
        for(int y=0;y<height;y++)
        {
            ay = (y + border) * memBOffset;
            for(int x=0;x<width;x++)
            {
                ax = (x + border) * depth;
                for(int r=0;r<depth;r++)
                {
                    float score = space[ay + ax + r];
                    if (score > cl[r])
                    {
                        output.add(new Circle(x, y, score / (cl[r] / centerThreshold),CircleDetection.MIN_RADIUS + r));
                    }
                }
            }
        }
        return output;
    }
}
