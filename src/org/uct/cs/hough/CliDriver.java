package org.uct.cs.hough;

import org.uct.cs.hough.reader.ImageReaderGIF;
import org.uct.cs.hough.writer.ImageWriter;

import java.io.File;
import java.io.IOException;

public class CliDriver
{
    public static void main(String[] args)
    {
        try
        {
            ImageWriter.Save(
                ImageReaderGIF.Load(new File("samples/testseq100000.gif")),
                new File("samples/testseq100000.jpg"),
                ImageWriter.ImageFormat.JPG
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
