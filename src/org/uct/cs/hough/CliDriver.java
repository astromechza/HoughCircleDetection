package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.stages.*;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.writer.ImageWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CliDriver
{
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 100;
    private static final int CENTER_THRESHOLD = 180;
    private static final int EDGE_THRESHOLD = 220;

    public static void main(String[] args)
    {
        try
        {
            BufferedImage original = ImageLoader.Load("samples/testseq100007.gif");

            PopUp.Show(original);

            // setup pipe stages
            IStage edgeThresholder = new ThresholdStage(EDGE_THRESHOLD);
            IStage centerThresholder = new ThresholdStage(CENTER_THRESHOLD);
            IStage normalizer = new NormalizeStage();
            IStage sobelEdgeDetector = new SobelEdgeDetectStage();

            ShortImageBuffer edges = edgeThresholder.flow(
                normalizer.flow(
                    sobelEdgeDetector.flow(
                        normalizer.flow(
                            new GreyscaleSource(GreyscaleSource.Formula.AVERAGE).source(original)
                        )
                    )
                )
            );

            PopUp.Show(edges.toImage());

            HoughFilterStage houghFilter = new HoughFilterStage(MIN_RADIUS, MAX_RADIUS, true);
            ShortImageBuffer houghed = houghFilter.flow(edges);
            HoughFilterStage.HoughSpace space = houghFilter.getLastHoughSpace();

            ShortImageBuffer circleCenters = centerThresholder.flow(houghed);

            List<Circle> circles = new ArrayList<>();
            for(int y=0;y<circleCenters.getHeight();y++)
            {
                for(int x=0;x<circleCenters.getWidth();x++)
                {
                    if (circleCenters.get(y,x) > 0)
                    {
                        circles.add(new Circle(x,y,space.getMaxRadii(y,x)));
                    }
                }
            }

            PopUp.Show(CircleAdder.Draw(edges.toImage(), circles));

            PopUp.Show(houghed.toImage());

            PopUp.Show(circleCenters.toImage());

            ImageWriter.Save(houghed.toImage(), "samples/out.png", ImageWriter.ImageFormat.PNG);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
