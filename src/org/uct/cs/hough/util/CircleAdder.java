package org.uct.cs.hough.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CircleAdder
{
    public static BufferedImage Draw(BufferedImage before, Iterable<Circle> circles)
    {
        Graphics2D g = (Graphics2D)before.getGraphics();
        g.setColor(Color.blue);
        for(Circle c : circles)
        {
            int r = c.circumpherence.radius;
            g.drawString("" + r, c.x-6, c.y+6);

            for(IntIntPair p : c.circumpherence.points)
            {
                int nx = c.x + p.x;
                int ny = c.y + p.y;
                g.drawLine(nx,ny,nx,ny);
            }
        }
        return before;
    }
}
