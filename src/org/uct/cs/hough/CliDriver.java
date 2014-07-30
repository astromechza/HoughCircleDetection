package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.stages.*;
import org.uct.cs.hough.writer.ImageWriter;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;

public class CliDriver
{
    public static void main(String[] args)
    {
        try
        {

            BufferedImage original = ImageLoader.Load("samples/testseq100000.gif");

            PopUp.Show(original);

            ShortImageBuffer edges = new ThresholdStage(210).flow(
                    new NormalizeStage().flow(
                            new SobelEdgeDetectStage().flow(
                                    new NormalizeStage().flow(
                                            Greyscale.Convert(
                                                    original,
                                                    new Greyscale.FormulaAverage()
                                            )
                                    )
                            )
                    )
            );

            ShortImageBuffer houghed = new HoughFilterStage(7, 50, true).flow(edges);

            PopUp.Show(houghed.toImage());

            ImageWriter.Save(houghed.toImage(), "samples/out.png", ImageWriter.ImageFormat.PNG);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
