package org.uct.cs.hough.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** PopUP
 * A simple class for displaying Buffered Images in a gui format as pop ups.
 *
 * Calling PopUp.show(..) will display a small box on screen containing the image. *
 */
public class PopUp
{
    // defaults/constants
    private static final int TITLE_LENGTH = 20;
    private static final int BORDER = 50;

    public PopUp(BufferedImage image, String title)
    {
        // format title if too long
        if(title.length() > (TITLE_LENGTH+3)) title = "..." + title.substring(title.length()-TITLE_LENGTH);

        // create frame
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // create centering panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
        panel.setBackground(Color.darkGray);
        panel.add(new ImagePanel(image), BorderLayout.CENTER);
        panel.setSize(image.getWidth(), image.getHeight());

        // pack and show
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public static void show(BufferedImage bi, String title)
    {
        new PopUp(bi, title);
    }

    /** ImagePanel
     * Custom ImagePanel for showing a bufferedImage
     */
    private class ImagePanel extends JPanel
    {
        private final BufferedImage image;

        public ImagePanel(BufferedImage image)
        {
            this.image = image;
            this.setSize(image.getWidth(), image.getHeight());
        }

        // draw!
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
