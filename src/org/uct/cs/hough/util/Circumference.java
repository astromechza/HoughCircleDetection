package org.uct.cs.hough.util;

import java.util.ArrayList;
import java.util.List;

public class Circumference
{
    public final List<IntIntPair> points = new ArrayList<>();
    public final int radius;

    public Circumference(int r)
    {
        this.radius = r;
    }

    public static Circumference build(int radius)
    {
        Circumference c = new Circumference(radius);

        int x = radius, y = 0;
        int radiusError = 1-x;

        while(x >= y)
        {
            c.points.add(new IntIntPair(+x, +y));
            c.points.add(new IntIntPair(+y, +x));
            c.points.add(new IntIntPair(-x, +y));
            c.points.add(new IntIntPair(-y, +x));
            c.points.add(new IntIntPair(-x, -y));
            c.points.add(new IntIntPair(-y, -x));
            c.points.add(new IntIntPair(+x, -y));
            c.points.add(new IntIntPair(+y, -x));

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

        return c;
    }

    public int getNumPoints()
    {
        return this.points.size();
    }
}
