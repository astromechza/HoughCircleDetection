package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.pipes.Greyscale;
import org.uct.cs.hough.pipes.InvertPipe;
import org.uct.cs.hough.pipes.NormalizePipe;
import org.uct.cs.hough.reader.ImageLoader;

import java.io.IOException;

public class CliDriver
{
    public static void main(String[] args)
    {
        try
        {
            PopUp.Show(
                new NormalizePipe().flow(
                    new InvertPipe().flow(
                        Greyscale.Convert(
                            ImageLoader.Load("samples/testseq100000.gif"),
                            new Greyscale.FormulaAverage()
                        )
                    )
                ).toImage()
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
