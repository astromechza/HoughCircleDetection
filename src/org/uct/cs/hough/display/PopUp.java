package org.uct.cs.hough.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PopUp
{
    private static final String DEFAULT_TITLE = "Image";

    // components
    private final JFrame frame;

    public PopUp(BufferedImage image, String title)
    {
        this.frame = new JFrame(title);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.add(new ImagePanel(image), BorderLayout.CENTER);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
    }

    public PopUp(BufferedImage image)
    {
        this(image, DEFAULT_TITLE);
    }

    public void show()
    {
        this.frame.setVisible(true);
    }

    public static void Show(BufferedImage bi)
    {
        new PopUp(bi).show();
    }

    public static void Show(BufferedImage bi, String title)
    {
        new PopUp(bi, title).show();
    }

    private class ImagePanel extends JPanel
    {
        private final BufferedImage image;

        public ImagePanel(BufferedImage image)
        {
            this.image = image;
            this.setSize(image.getWidth(), image.getHeight());
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(this.image.getWidth(), this.image.getHeight());
        }
    }


}
