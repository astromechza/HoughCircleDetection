package org.uct.cs.hough.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Circumpherence implements Iterable<IntIntPair>
{
    public final List<IntIntPair> points = new ArrayList<>();
    public final int radius;

    public Circumpherence(int r)
    {
        this.radius = r;
    }

    public static Circumpherence build(int radius)
    {
        Circumpherence c = new Circumpherence(radius);

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


    @Override
    public Iterator<IntIntPair> iterator()
    {
        return new Iterator<IntIntPair>()
        {
            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < getNumPoints();
            }

            @Override
            public IntIntPair next()
            {
                return points.get(index++);
            }
        };
    }
}
