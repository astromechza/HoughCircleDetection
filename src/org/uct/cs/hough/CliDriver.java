package org.uct.cs.hough;

import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.processors.*;
import org.uct.cs.hough.reader.ImageLoader;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.*;
import org.uct.cs.hough.writer.ImageWriter;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CliDriver
{
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 100;
    private static final float CENTER_THRESHOLD = 0.4f;
    private static final int EDGE_THRESHOLD = 220;

    public static void main(String[] args)
    {
        String[] images = new String[]{
            "samples/testseq100000.gif",
            "samples/testseq100007.gif",
            "samples/testseq100136.gif",
            "samples/testseq100192.gif",
        };

        try
        {

            for (String image : images)
            {
                // start the timer so we can print sub times
                try (Timer timer = new Timer("total"))
                {
                    // load the image in greyscale form
                    ShortImageBuffer original = ImageLoader.load(image);
                    timer.print("read");

                    // perform edge detect
                    ShortImageBuffer edges = Thresholder.threshold(
                        Normalizer.norm(
                            SobelEdgeDetector.apply(
                                Normalizer.norm(original)
                            )
                        ), EDGE_THRESHOLD
                    );
                    timer.print("edge detector");

                    // build a list of the shapes to test for, in this case, circles.
                    List<Circumpherence> circumpherences = new ArrayList<>();
                    for (int r = MIN_RADIUS; r < MAX_RADIUS; r++) circumpherences.add(Circumpherence.build(r));

                    // setup hough filter
                    List<Circle> candidateCircles = HoughFilter.identify(edges, circumpherences, CENTER_THRESHOLD);

                    // double check
                    List<Circle> goodCircles = new ArrayList<>();
                    for (Circle c : candidateCircles)
                    {
                        float score = getCircleScore(edges, c);
                        float score2 = getCircleScore(edges, new Circle(c.x, c.y, Circumpherence.build(c.circumpherence.radius-1)));
                        float finalScore = (score + score2) / 2;
                        if (finalScore > 0.5f)
                        {
                            goodCircles.add(
                                new Circle(c.x, c.y, finalScore, c.circumpherence)
                            );
                        }
                    }

                    // remove overlaps
                    List<Circle> finalCircles = new ArrayList<>();
                    for(Circle c1 : goodCircles)
                    {
                        boolean hasOverlaps = false;
                        for(Circle c2 : goodCircles)
                        {
                            if (c2 != c1)
                            {
                                double d = Math.sqrt(Math.pow(c2.x - c1.x, 2) + Math.pow(c2.y - c1.y, 2));
                                if (d < 20)
                                {
                                    if (c2.score > c1.score) hasOverlaps = true;
                                }
                            }
                        }
                        if (!hasOverlaps)finalCircles.add(c1);
                    }

                    timer.print("circle detect");

                    PopUp.Show(CircleAdder.Draw(edges.toImage(), finalCircles), "Detected Circles");

                    timer.print("save");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static float getCircleScore(ShortImageBuffer edges, Circle circle)
    {
        int total = 0;
        int pcount = 0;
        for (IntIntPair p : circle.circumpherence.points)
        {
            int nx = circle.x + p.x;
            int ny = circle.y + p.y;

            if (nx >= 0 && nx < edges.getWidth() && ny >= 0 && ny < edges.getHeight())
            {
                pcount++;
                if (edges.get(ny, nx) != 0) total++;
            }
        }
        return ((float) total) / pcount;
    }
}
