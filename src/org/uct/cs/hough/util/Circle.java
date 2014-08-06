package org.uct.cs.hough.util;

public class Circle
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
}
