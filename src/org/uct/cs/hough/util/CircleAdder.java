package org.uct.cs.hough.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CircleAdder
{
    public static BufferedImage draw(BufferedImage before, Iterable<Circle> circles)
    {
        Graphics2D g = (Graphics2D)before.getGraphics();
        g.setColor(Color.yellow);
        for(Circle c : circles)
        {
            int r = c.circumpherence.radius;
            for(IntIntPair p : c.circumpherence.points)
            {
                int nx = c.x + p.x;
                int ny = c.y + p.y;
                g.drawLine(nx,ny,nx,ny);
            }
        }
        return before;
    }

    public static BufferedImage combine(BufferedImage layer, Iterable<Circle> circles)
    {
        BufferedImage bi = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        bi.getGraphics().drawImage(layer,0,0, null);
        return draw(bi, circles);
    }
}
