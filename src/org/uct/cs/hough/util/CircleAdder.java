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
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        for(Circle c : circles)
        {
            g.setColor(Color.blue);
            int x = c.radius, y = 0;
            int radiusError = 1-x;
            while(x >= y)
            {
                g.drawLine(c.x+x, c.y+y, c.x+x, c.y+y);
                g.drawLine(c.x+y, c.y+x, c.x+y, c.y+x);
                g.drawLine(c.x-x, c.y+y, c.x-x, c.y+y);
                g.drawLine(c.x-y, c.y+x, c.x-y, c.y+x);
                g.drawLine(c.x-x, c.y-y, c.x-x, c.y-y);
                g.drawLine(c.x-y, c.y-x, c.x-y, c.y-x);
                g.drawLine(c.x+x, c.y-y, c.x+x, c.y-y);
                g.drawLine(c.x+y, c.y-x, c.x+y, c.y-x);
                y++;
                if (radiusError >= 0) radiusError -= 2 * (x--);
                radiusError += 2 * y + 1;
            }

            g.drawLine(c.x-2, c.y-2, c.x+2, c.y+2);
            g.drawLine(c.x-2, c.y+2, c.x+2, c.y-2);
            g.setColor(Color.yellow);
            g.drawString(String.format("%.3f", c.score), c.x-12, c.y+12);
        }

        return image;
    }
}
