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
            for(int[] p : CircumferenceProvider.get(c.radius))
            {
                int nx = c.x + p[0];
                int ny = c.y + p[1];
                g.drawLine(nx,ny,nx,ny);
            }

            g.drawLine(c.x-2, c.y, c.x+2, c.y);
            g.drawLine(c.x, c.y-2, c.x, c.y+2);
        }

        return image;
    }
}
