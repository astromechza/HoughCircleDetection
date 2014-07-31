package org.uct.cs.hough.writer;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageWriter
{
    public static void Save(RenderedImage image, File file, ImageFormat format) throws IOException
    {
        ImageIO.write(image, format.getFormat(), file);
    }

    public static void Save(RenderedImage image, String s, ImageFormat format) throws IOException
    {
        Save(image, new File(s), format);
    }

    public static void Save(RenderedImage image, File file) throws IOException
    {
        Save(image, file, ImageFormat.JPG);
    }

    public static void Save(RenderedImage image, String s) throws IOException
    {
        Save(image, new File(s), ImageFormat.JPG);
    }

    public enum ImageFormat
    {
        PNG("png"), JPG("jpg");

        private final String format;

        ImageFormat(String f)
        {
            this.format = f;
        }

        public String getFormat()
        {
            return this.format;
        }
    }
}
