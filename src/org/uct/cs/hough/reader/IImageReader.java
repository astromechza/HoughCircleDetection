package org.uct.cs.hough.reader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public abstract class IImageReader
{
    public static BufferedImage load(File f) throws IOException
    {
        return null;
    }

    public static BufferedImage load(String s) throws IOException
    {
        return null;
    }
}
