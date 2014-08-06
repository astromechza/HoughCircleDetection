package org.uct.cs.hough.util;

import java.util.ArrayList;
import java.util.List;

public class CircumferenceProvider
{
    private static int minRadius;
    private static int maxRadius;
    private static List<int[][]> pointlists;

    public static void initialise(int minR, int maxR)
    {
        minRadius = minR;
        maxRadius = maxR;
        pointlists = new ArrayList<>();
        int numr = maxRadius - minRadius;

        for(int r=0;r<numr;r++)
        {
            // first count points
            int length = 0;
            {
                int x = minRadius + r, y = 0;
                int radiusError = 1-x;

                while(x >= y)
                {
                    length += 8;
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
            }

            // create actual array
            int[][] points = new int[length][2];
            {
                int x = minRadius + r, y = 0;
                int radiusError = 1-x;
                int index = 0;
                while(x >= y)
                {
                    points[index][0] = +x;
                    points[index][1] = +y;
                    points[index+1][0] = +y;
                    points[index+1][1] = +x;
                    points[index+2][0] = -x;
                    points[index+2][1] = +y;
                    points[index+3][0] = -y;
                    points[index+3][1] = +x;

                    points[index+4][0] = -x;
                    points[index+4][1] = -y;
                    points[index+5][0] = -y;
                    points[index+5][1] = -x;
                    points[index+6][0] = +x;
                    points[index+6][1] = -y;
                    points[index+7][0] = +y;
                    points[index+7][1] = -x;

                    index += 8;
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
            }
            pointlists.add(points);
        }
    }

    public static int[][] get(int r)
    {
        return pointlists.get(r-minRadius);
    }

    public static int getMinRadius()
    {
        return minRadius;
    }

    public static int getMaxRadius()
    {
        return maxRadius;
    }

    public static List<int[][]> getPointlists()
    {
        return pointlists;
    }
}
