package org.uct.cs.hough.util;

public class Circle
{
    public final int x,y;
    public final Circumference circumference;
    public final float score;

    public Circle(int x, int y, float s, Circumference c)
    {
        this.x = x;
        this.y = y;
        this.score = s;
        this.circumference = c;
    }

    public Circle(int x, int y, Circumference c)
    {
        this(x,y,0,c);
    }
}
