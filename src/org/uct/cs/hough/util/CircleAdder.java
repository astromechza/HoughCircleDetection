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
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
        for(Circle c : circles)
        {
            g.setColor(Color.blue);
            for(int[] p : CircumferenceProvider.get(c.radius))
            {
                int nx = c.x + p[0];
                int ny = c.y + p[1];
                g.drawLine(nx,ny,nx,ny);
            }
            g.drawLine(c.x-1, c.y-1, c.x+1, c.y+1);
            g.drawLine(c.x-1, c.y+1, c.x+1, c.y-1);
            g.setColor(Color.yellow);
            g.drawString(String.format("%.3f", c.score), c.x+3, c.y+3);
        }

        return image;
    }
}
