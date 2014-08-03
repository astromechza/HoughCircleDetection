package org.uct.cs.hough.util;

public class Circle
{
    public final int x,y;
    public final Circumpherence circumpherence;
    public final float score;

    public Circle(int x, int y, float s, Circumpherence c)
    {
        this.x = x;
        this.y = y;
        this.score = s;
        this.circumpherence = c;
    }

    public Circle(int x, int y, Circumpherence c)
    {
        this(x,y,0,c);
    }
}
