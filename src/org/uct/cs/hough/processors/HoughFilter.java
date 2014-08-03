package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.Circumpherence;
import org.uct.cs.hough.util.IntIntPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HoughFilter
{
    public static List<Circle> identify(ShortImageBuffer edges, Collection<Circumpherence> circumpherences, float centerThreshold)
    {
        int height = edges.getHeight();
        int width = edges.getWidth();
        int depth = circumpherences.size();

        // create hough space
        int[][][] space = new int[height][width][depth];

        for(int y=0;y<height;y++)
        {
            for(int x=0;x<width;x++)
            {
                if (edges.get(y,x) != 0)
                {
                    int i = 0;
                    for (Circumpherence c : circumpherences)
                    {
                        for (IntIntPair p : c.points)
                        {
                            int nx = x + p.x;
                            int ny = y + p.y;
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height ) space[ny][nx][i]++;
                        }
                        i++;
                    }
                }
            }
        }

        List<Circle> output = new ArrayList<>();
        for(int y=0;y<height;y++)
        {
            for(int x=0;x<width;x++)
            {
                int r = 0;
                for (Circumpherence c : circumpherences)
                {
                    float score = ((float)(space[y][x][r])) / c.getNumPoints();
                    if (score > centerThreshold)
                    {
                        output.add(new Circle(x,y,c));
                    }
                    r++;
                }
            }
        }
        return output;
    }
}
