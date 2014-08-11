package org.uct.cs.hough.display;

import org.uct.cs.hough.gui.ScalingImagePanel;

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
        panel.setBackground(Color.darkGray);
        panel.add(new ScalingImagePanel(image), BorderLayout.CENTER);
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
}
