package org.uct.cs.hough.reader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader
{

    public static BufferedImage Load(File f) throws IOException
    {
        BufferedImage before = ImageIO.read(f);

        BufferedImage after = new BufferedImage(before.getWidth(), before.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        after.getGraphics().drawImage(before, 0, 0, null);
        return after;
    }

    public static BufferedImage Load(String s) throws IOException
    {
        return Load(new File(s));
    }


}
