package org.uct.cs.hough;

import org.uct.cs.hough.processors.HighPassFilter;
import org.uct.cs.hough.processors.HoughFilter;
import org.uct.cs.hough.processors.Normalizer;
import org.uct.cs.hough.processors.SobelEdgeDetector;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircumferenceProvider;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CircleDetection
{
    private static final int OVERLAP_DISTANCE_SQ = 400;
    public static final int MIN_RADIUS = 10;
    public static final int MAX_RADIUS = 100;
    private static final float FINAL_SCORE_THRESHOLD = 0.5f;
    private static final float CENTER_THRESHOLD = 0.4f;
    private static final int EDGE_THRESHOLD = 220;

    public static Collection<Circle> detect(BufferedImage input)
    {
        CircumferenceProvider.initialise(MIN_RADIUS, MAX_RADIUS);

        // make sure the image is in BGR
        if (input.getType() != BufferedImage.TYPE_3BYTE_BGR)
        {
            BufferedImage midway = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            midway.getGraphics().drawImage(input, 0, 0, null);
            input = midway;
        }

        // convert to greyscale
        int w = input.getWidth();
        int h = input.getHeight();
        ShortImageBuffer greyscale = new ShortImageBuffer(h, w);
        byte[] pixels = ((DataBufferByte) input.getRaster().getDataBuffer()).getData();

        int index = 0;
        for(int y=0;y<h;y++)
        {
            for(int x=0;x<w;x++)
            {
                greyscale.set(y, x, (short)(
                    (
                        (pixels[index+2] & 0xFF) +
                        (pixels[index+1] & 0xFF) +
                        (pixels[index] & 0xFF)
                    )/3)
                );
                index += 3;
            }
        }

        ShortImageBuffer edges = HighPassFilter.threshold(
            Normalizer.norm(
                SobelEdgeDetector.apply(
                    Normalizer.norm(greyscale)
                )
            ), EDGE_THRESHOLD
        );


        // setup hough filter
        List<Circle> candidateCircles = HoughFilter.identify(edges, CENTER_THRESHOLD);

        // double check
        Collection<Circle> goodCircles = new ArrayList<>();
        for (Circle c : candidateCircles)
        {
            float score = getCircleScore(edges, c);
            if (c.radius % 2 == 1)
            {
                float score2 = getCircleScore(edges, new Circle(c.x, c.y, c.radius-1));
                score = (score + score2) / 2;
            }
            if (score > FINAL_SCORE_THRESHOLD)
            {
                goodCircles.add(
                    new Circle(c.x, c.y, score, c.radius)
                );
            }
        }

        // remove overlaps
        Collection<Circle> finalCircles = new ArrayList<>();
        for(Circle c1 : goodCircles)
        {
            boolean hasOverlaps = false;
            for(Circle c2 : goodCircles)
            {
                if (c2 != c1)
                {
                    double ds = Math.pow(c2.x - c1.x, 2) + Math.pow(c2.y - c1.y, 2);
                    if (ds < OVERLAP_DISTANCE_SQ)
                    {
                        if (c2.score > c1.score) hasOverlaps = true;
                    }
                }
            }
            if (!hasOverlaps)finalCircles.add(c1);
        }

        return finalCircles;
    }

    private static float getCircleScore(ShortImageBuffer edges, Circle circle)
    {
        float total = 0;
        int pcount = 0;
        for(int[] p : CircumferenceProvider.get(circle.radius))
        {
            int nx = circle.x + p[0];
            int ny = circle.y + p[1];

            if (nx >= 0 && nx < edges.getWidth() && ny >= 0 && ny < edges.getHeight())
            {
                pcount++;
                if (edges.get(ny, nx) != 0) total+=1;
            }
        }
        return total / pcount;
    }
}
