package org.uct.cs.hough.reader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class ImageLoader
{

    public static ShortImageBuffer load(File f) throws IOException
    {
        BufferedImage before = ImageIO.read(f);

        BufferedImage midway = new BufferedImage(before.getWidth(), before.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        midway.getGraphics().drawImage(before, 0, 0, null);

        int w = midway.getWidth();
        int h = midway.getHeight();
        ShortImageBuffer output = new ShortImageBuffer(h, w);
        byte[] pixels = ((DataBufferByte) midway.getRaster().getDataBuffer()).getData();

        int index = 0;
        for(int y=0;y<h;y++)
        {
            for(int x=0;x<w;x++)
            {
                output.set(y, x, (short)(
                    (
                        (pixels[index+2] & 0xFF) +
                        (pixels[index+1] & 0xFF) +
                        (pixels[index] & 0xFF)
                    )/3)
                );
                index += 3;
            }
        }

        return output;
    }

    public static ShortImageBuffer load(String s) throws IOException
    {
        return load(new File(s));
    }


}
