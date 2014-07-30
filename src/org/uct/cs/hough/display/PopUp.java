package org.uct.cs.hough.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PopUp
{
    private BufferedImage image;

    // components
    private JFrame frame;
    private ImagePanel panel;

    public PopUp(BufferedImage bi)
    {
        this.image = bi;

        this.frame = new JFrame("Image");
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.setLayout(new BorderLayout());
        this.panel = new ImagePanel(this.image);
        this.frame.add(panel, BorderLayout.CENTER);

        this.frame.pack();

        this.frame.setLocationRelativeTo(null);
    }

    public void show()
    {
        this.frame.setVisible(true);
    }

    public static void Show(BufferedImage bi)
    {
        new PopUp(bi).show();
    }

    private class ImagePanel extends JPanel
    {
        private BufferedImage image;

        public ImagePanel(BufferedImage image)
        {
            this.image = image;
            this.setSize(image.getWidth(), image.getHeight());
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if(image != null) g.drawImage(image, 0, 0, this);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(this.image.getWidth(), this.image.getHeight());
        }
    }


}
