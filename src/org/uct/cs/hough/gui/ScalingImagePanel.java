package org.uct.cs.hough.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ScalingImagePanel extends JComponent
{
    private static final Color background = Color.darkGray;
    private static final int border = 5;

    private BufferedImage image;

    public ScalingImagePanel()
    {
        super();
        this.image = null;
    }

    public ScalingImagePanel(BufferedImage image)
    {
        super();
        this.image = image;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int h = this.getHeight();
        int w = this.getWidth();
        g.setColor(background);
        g.fillRect(0, 0, w, h);

        if (this.image != null)
        {
            int wb = (w - this.image.getWidth()) / 2;
            int hb = (h - this.image.getHeight()) / 2;
            g.drawImage(image, wb, hb, this);
        }
    }

    @Override
    public Dimension getMinimumSize()
    {
        if(this.image == null) return new Dimension(500, 100);
        return new Dimension(this.image.getWidth() + 2*border, 300);
    }

    @Override
    public Dimension getPreferredSize()
    {
        if(this.image == null) return new Dimension(600, 300);
        return new Dimension(this.image.getWidth() + 2*border, this.image.getHeight() + 2*border);
    }

    public void setImage(BufferedImage bi)
    {
        this.image = bi;
        this.repaint();
    }


}
