package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircumferenceProvider;

import java.util.ArrayList;
import java.util.List;

public class HoughFilter
{
    public static List<Circle> identify(ShortImageBuffer edges, float centerThreshold)
    {
        int height = edges.getHeight();
        int width = edges.getWidth();
        int depth = CircumferenceProvider.getPointlists().size();

        // create hough space
        int[][][] space = new int[height][width][depth];

        for(int y=0;y<height;y++)
        {
            for(int x=0;x<width;x++)
            {
                if (edges.get(y,x) != 0)
                {
                    int r = 0;
                    for(int[][] pointlist : CircumferenceProvider.getPointlists())
                    {
                        for(int[] p : pointlist)
                        {
                            int nx = x + p[0];
                            int ny = y + p[1];
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height ) space[ny][nx][r]++;
                        }
                        r++;
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
                for (int[][] pointlist : CircumferenceProvider.getPointlists())
                {
                    float score = ((float)(space[y][x][r])) / pointlist.length;
                    if (score > centerThreshold)
                    {
                        output.add(new Circle(x,y,CircumferenceProvider.getMinRadius() + r));
                    }
                    r++;
                }
            }
        }
        return output;
    }
}
