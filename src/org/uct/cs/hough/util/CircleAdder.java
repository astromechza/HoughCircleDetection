package org.uct.cs.hough.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CircleAdder
{
    public static BufferedImage Draw(BufferedImage before, Iterable<Circle> circles)
    {
        Graphics g = before.getGraphics();
        g.setColor(Color.red);
        for(Circle c : circles)
        {
            g.drawOval(c.x-c.r, c.y-c.r, c.r*2, c.r*2);
            g.drawString("" + c.r, c.x-6, c.y+6);
        }
        return before;
    }
}