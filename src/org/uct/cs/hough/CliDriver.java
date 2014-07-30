package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.stages.*;
import org.uct.cs.hough.writer.ImageWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class CliDriver
{
    private static final int MIN_RADIUS = 5;
    private static final int MAX_RADIUS = 50;
    private static final int EDGE_THRESHOLD = 210;

    public static void main(String[] args)
    {
        try
        {
            BufferedImage original = ImageLoader.Load("samples/testseq100000.gif");

            PopUp.Show(original);

            ShortImageBuffer edges = new ThresholdStage(EDGE_THRESHOLD).flow(
                new NormalizeStage().flow(
                    new SobelEdgeDetectStage().flow(
                        new NormalizeStage().flow(
                            new GreyscaleSource(GreyscaleSource.Formula.AVERAGE).source(original)
                        )
                    )
                )
            );

            ShortImageBuffer houghed = new HoughFilterStage(MIN_RADIUS, MAX_RADIUS, true).flow(edges);

            PopUp.Show(houghed.toImage());

            PopUp.Show(new ThresholdStage(EDGE_THRESHOLD).flow(houghed).toImage());

            ImageWriter.Save(houghed.toImage(), "samples/out.png", ImageWriter.ImageFormat.PNG);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
