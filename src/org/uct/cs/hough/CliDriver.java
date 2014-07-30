package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.stages.*;

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

            PopUp.Show(
                new HoughFilterStage(5, 50).flow(
                    edges
                ).toImage()
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
