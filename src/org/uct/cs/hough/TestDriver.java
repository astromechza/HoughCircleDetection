package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.util.Timer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class TestDriver
{
    public static void main(String [] args)
    {
        String[] paths = new String[]{
            "samples/testseq100000.gif",
            "samples/testseq100007.gif",
            "samples/testseq100136.gif",
            "samples/testseq100192.gif",
        };

        try
        {
            for (String path : paths)
            {
                System.out.println(path);

                // start the timer so we can print sub times
                Timer timer = new Timer();

                BufferedImage image = ImageIO.read(new File(path));
                timer.print("image load");

                Collection<Circle> circles = CircleDetection.detect(image);
                timer.print("circle detection");

                PopUp.Show(CircleAdder.combine(image, circles), path);
                timer.print("popup");

                System.out.printf("Detected: %d circles. %n%n", circles.size());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
