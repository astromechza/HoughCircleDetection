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
        if(this.x <= this.radius ||
            this.y <= this.radius ||
            this.x >= (edges.getWidth() - this.radius) ||
            this.y >= (edges.getHeight() - this.radius)) return this.scoreCheck(edges);
        return this.scoreNoCheck(edges);
    }

    public Circle scoreCheck(ShortImageBuffer edges)
    {
        float total = 0;
        int pcount = 0;
        {
            int py = this.radius;
            int px = 0;
            int d = (5-py*4)/4;
            int nx, ny;
            do
            {
                nx = this.x + px;
                ny =  this.y + py;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                nx = this.x - px;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                ny =  this.y - py;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                nx = this.x + px;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }

                nx = this.x + py;
                ny =  this.y + px;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                nx = this.x - py;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                ny =  this.y - px;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }
                nx = this.x + py;
                if (nx >= 1 && nx < edges.getWidth()-1 && ny >= 1 && ny < edges.getHeight()-1)
                {
                    pcount++;
                    if (edges.get(ny, nx) != 0) total += 1;
                }

                if (d >= 0) d += - 2 * py--;
                d += 2 * ++px;
            }
            while(px <= py);
        }

        return new Circle(this.x, this.y, total / pcount, this.radius);
    }

    public Circle scoreNoCheck(ShortImageBuffer edges)
    {
        float total2 = 0;
        int pcount2 = 0;
        {
            int py = this.radius;
            int px = 0;
            int d = (5-py*4)/4;
            int nx, ny;
            do
            {
                pcount2 += 8;
                nx = this.x + px;
                ny = this.y + py;
                if (edges.get(ny, nx) != 0) total2 += 1;
                nx = this.x - px;
                if (edges.get(ny, nx) != 0) total2 += 1;
                ny = this.y - py;
                if (edges.get(ny, nx) != 0) total2 += 1;
                nx = this.x + px;
                if (edges.get(ny, nx) != 0) total2 += 1;

                nx = this.x + py;
                ny = this.y + px;
                if (edges.get(ny, nx) != 0) total2 += 1;
                nx = this.x - py;
                if (edges.get(ny, nx) != 0) total2 += 1;
                ny = this.y - px;
                if (edges.get(ny, nx) != 0) total2 += 1;
                nx = this.x + py;
                if (edges.get(ny, nx) != 0) total2 += 1;

                if (d >= 0) d += - 2 * py--;
                d += 2 * ++px;
            }
            while(px <= py);
        }

        return new Circle(this.x, this.y, total2 / pcount2, this.radius);
    }
}
