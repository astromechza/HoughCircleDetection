package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.stages.*;

import java.io.IOException;

public class CliDriver
{
    public static void main(String[] args)
    {
        try
        {
            PopUp.Show(
                new ThresholdStage(210).flow(
                    new NormalizeStage().flow(
                        new SobelEdgeDetectStage().flow(
                            new NormalizeStage().flow(
                                new InvertStage().flow(
                                    Greyscale.Convert(
                                        ImageLoader.Load("samples/testseq100000.gif"),
                                        new Greyscale.FormulaAverage()
                                    )
                                )
                            )
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
