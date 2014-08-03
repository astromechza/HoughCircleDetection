package org.uct.cs.hough.writer;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageWriter
{
    public static void save(RenderedImage image, File file, ImageFormat format) throws IOException
    {
        if(file.exists()) System.out.println("File already exists, overwriting.");
        ImageIO.write(image, format.getFormat(), file);
    }

    public static void save(RenderedImage image, String s, ImageFormat format) throws IOException
    {
        save(image, new File(s), format);
    }

    public static void save(RenderedImage image, File file) throws IOException
    {
        if (file.getName().endsWith(".png"))
            save(image, file, ImageFormat.PNG);
        else
            save(image, file, ImageFormat.JPG);
    }

    public static void save(RenderedImage image, String s) throws IOException
    {
        if (s.endsWith(".png"))
            save(image, new File(s), ImageFormat.PNG);
        else
            save(image, new File(s), ImageFormat.JPG);
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
