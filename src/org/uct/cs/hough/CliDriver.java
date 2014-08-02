package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.processors.*;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.util.Constants;
import org.uct.cs.hough.util.Timer;
import org.uct.cs.hough.writer.ImageWriter;

import java.io.IOException;
import java.util.List;

class CliDriver
{
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 100;
    private static final int CENTER_THRESHOLD = 240;
    private static final int EDGE_THRESHOLD = 220;

    public static void main(String[] args)
    {
        try
        {
            try(Timer ignored = new Timer("total"))
            {
                ShortImageBuffer original = ImageLoader.load("samples/testseq100007.gif");
                ignored.print("read");

                ShortImageBuffer edges = Thresholder.threshold(
                    Normalizer.norm(
                        SobelEdgeDetector.apply(
                            Normalizer.norm(original)
                        )
                    ), EDGE_THRESHOLD
                );

                ignored.print("edge detector");

                HoughFilter houghFilter = new HoughFilter(MIN_RADIUS, MAX_RADIUS, true);
                ShortImageBuffer houghed = houghFilter.run(edges);
                HoughFilter.HoughSpace space = houghFilter.getLastHoughSpace();

                ignored.print("hough filter");

                List<Circle> circles = BestPointFinder.find(houghed, space, CENTER_THRESHOLD, Constants.BYTE);

                ignored.print("circle collect");

                PopUp.Show(CircleAdder.Draw(edges.toImage(), circles), "Detected Circles");

                ignored.print("circle draw");

                ImageWriter.Save(houghed.toImage(), "samples/out.png", ImageWriter.ImageFormat.PNG);

                ignored.print("save");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
