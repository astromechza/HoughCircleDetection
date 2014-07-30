package org.uct.cs.hough.util;

public class MathHelper
{
    public static int clamp(int v, int min, int max)
    {
        v = (v > min) ? v : min;
        return (v < max) ? v : max;
    }
}
