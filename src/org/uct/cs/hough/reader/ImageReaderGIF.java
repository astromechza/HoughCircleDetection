package org.uct.cs.hough.reader;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageReaderGIF extends IImageReader
{
    public static BufferedImage Load(File f) throws IOException
    {
        ImageReader r = new GIFImageReader(new GIFImageReaderSpi());
        r.setInput(ImageIO.createImageInputStream(f));

        return r.read(0);
    }

    public static BufferedImage Load(String s) throws IOException
    {
        return Load(new File(s));
    }
}
