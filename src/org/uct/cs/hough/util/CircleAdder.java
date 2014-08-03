package org.uct.cs.hough.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CircleAdder
{
    public static BufferedImage combine(BufferedImage layer, Iterable<Circle> circles)
    {
        BufferedImage image = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D)image.getGraphics();

        g.drawImage(layer, 0, 0, null);
        g.setColor(Color.yellow);
        for(Circle c : circles)
        {
            for(IntIntPair p : c.circumpherence.points)
            {
                int nx = c.x + p.x;
                int ny = c.y + p.y;
                g.drawLine(nx,ny,nx,ny);
            }
        }

        return image;
    }
}
