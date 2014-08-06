package org.uct.cs.hough.util;

import org.uct.cs.hough.reader.ShortImageBuffer;

public class Circle implements Comparable<Circle>
{
    public final int x,y;
    public final int radius;
    public final float score;

    public Circle(int x, int y, float s, int r)
    {
        this.x = x;
        this.y = y;
        this.score = s;
        this.radius = r;
    }

    public Circle(int x, int y, int r)
    {
        this(x,y,0,r);
    }

    public Circle shift(int dx, int dy)
    {
        return new Circle(this.x + dx, this.y + dy, 0, this.radius);
    }
    public Circle grow(int dr)
    {
        return new Circle(this.x, this.y, 0, this.radius + dr);
    }

    @Override
    public int compareTo(Circle o)
    {
        return Float.compare(this.score, o.score);
    }

    public Circle score(ShortImageBuffer edges)
    {
        float total = 0;
        int pcount = 0;
        for(int[] p : CircumferenceProvider.get(this.radius))
        {
            int nx = this.x + p[0];
            int ny = this.y + p[1];

            if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
            {
                pcount++;
                if (edges.get(ny, nx) != 0) total+=1;
            }
        }
        return new Circle(this.x, this.y, total / pcount, this.radius);
    }
}
