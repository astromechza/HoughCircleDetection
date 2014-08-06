package org.uct.cs.hough.processors;

import org.uct.cs.hough.CircleDetection;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircumferenceProvider;

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
        int depth = CircumferenceProvider.getPointlists().size();

        // create hough space
        int[][][] space = new int[heightWithBorder][widthWithBorder][depth];
        int nx,ny,ay,ax,cx,cy,radiusError;
        for(int y=0;y<height;y++)
        {
            ay = y + border;
            for(int x=0;x<width;x++)
            {
                ax = x + border;
                if (edges.get(y,x) != 0)
                {
                    for(int r=0;r<depth;r++)
                    {
                        cx = CircleDetection.MIN_RADIUS + r;
                        cy = 0;
                        radiusError = 1-cx;
                        while(cx >= cy)
                        {
                            nx = ax + cx;
                            ny = ay + cy;
                            space[ny][nx][r]++;
                            nx = ax + cy;
                            ny = ay + cx;
                            space[ny][nx][r]++;
                            nx = ax - cx;
                            ny = ay + cy;
                            space[ny][nx][r]++;
                            nx = ax - cy;
                            ny = ay + cx;
                            space[ny][nx][r]++;
                            nx = ax - cx;
                            ny = ay - cy;
                            space[ny][nx][r]++;
                            nx = ax - cy;
                            ny = ay - cx;
                            space[ny][nx][r]++;
                            nx = ax + cx;
                            ny = ay - cy;
                            space[ny][nx][r]++;
                            nx = ax + cy;
                            ny = ay - cx;
                            space[ny][nx][r]++;

                            cy++;
                            if (radiusError<0)
                            {
                                radiusError += 2 * cy + 1;
                            }
                            else
                            {
                                cx--;
                                radiusError += 2 * (cy - cx + 1);
                            }
                        }
                    }
                }
            }
        }

        // cache circumference lenths
        float[] cl = new float[depth];
        for(int r=0;r<depth;r++) cl[r] = CircumferenceProvider.get(CircleDetection.MIN_RADIUS + r).length;

        List<Circle> output = new ArrayList<>();
        for(int y=0;y<height;y++)
        {
            ay = y + border;
            for(int x=0;x<width;x++)
            {
                ax = x + border;
                for(int r=0;r<depth;r++)
                {
                    if ((space[ay][ax][r] / cl[r]) > centerThreshold)
                    {
                        output.add(new Circle(x,y,CircleDetection.MIN_RADIUS + r));
                    }
                }
            }
        }
        return output;
    }
}
