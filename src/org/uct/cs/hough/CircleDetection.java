package org.uct.cs.hough;

import org.uct.cs.hough.processors.HighPassFilter;
import org.uct.cs.hough.processors.HoughFilter;
import org.uct.cs.hough.processors.Normalizer;
import org.uct.cs.hough.processors.SobelEdgeDetector;
import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CircleDetection
{
    private static final int OVERLAP_DISTANCE_SQ = 400;
    public static final int MIN_RADIUS = 10;
    public static final int MAX_RADIUS = 100;
    private static final float FINAL_SCORE_THRESHOLD = 0.72f;
    private static final float CENTER_THRESHOLD = 0.4f;
    private static final int EDGE_THRESHOLD = 210;

    public static Collection<Circle> detect(BufferedImage input)
    {
        // make sure the image is in BGR
        if (input.getType() != BufferedImage.TYPE_3BYTE_BGR)
        {
            int h = input.getHeight();
            int w = input.getWidth();
            BufferedImage midway = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            midway.getGraphics().drawImage(input, 0, 0, null);
            input = midway;
        }

        // convert to greyscale
        ShortImageBuffer greyscale = fromBufferedImage(input);

        ShortImageBuffer edges = HighPassFilter.threshold(
            Normalizer.norm(
                SobelEdgeDetector.apply(
                    Normalizer.norm(greyscale)
                )
            ), EDGE_THRESHOLD
        );

        Collection<Circle> circles = HoughFilter.identify(edges, MIN_RADIUS, MAX_RADIUS, CENTER_THRESHOLD);
        circles = filterOverlaps(circles);
        circles = filterFinalScoreCheck(edges, circles);

        return circles;
    }

    private static ShortImageBuffer fromBufferedImage(BufferedImage input)
    {
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
        return greyscale;
    }

    private static Collection<Circle> filterOverlaps(Collection<Circle> input)
    {
        Collection<Circle> output = new ArrayList<>(input.size());

        // for each circle
        for(Circle c1 : input)
        {
            // at first it doesn't have overlaps
            boolean hasOverlaps = false;

            // loop through all the circles
            for(Circle c2 : input)
            {
                // can't compare to itself
                if (c2 != c1)
                {
                    // if the square distance is smaller than the min overlap distance
                    if ((Math.pow(c2.x - c1.x, 2) + Math.pow(c2.y - c1.y, 2)) < OVERLAP_DISTANCE_SQ)
                    {
                        if (c2.score > c1.score) hasOverlaps = true;
                        else if(c2.score == c1.score && c2.hashCode() > c1.hashCode()) hasOverlaps = true;
                    }
                }
            }
            if (!hasOverlaps) output.add(c1);
        }
        return output;
    }

    private static Collection<Circle> filterFinalScoreCheck(ShortImageBuffer edgeImage, Collection<Circle> input)
    {
        Collection<Circle> output = new ArrayList<>(input.size());
        for (Circle original : input)
        {
            List<Circle> permutations = new ArrayList<>(input.size());
            for(int i=0;i<3*3*3;i++)
            {
                int dr = (i%3)-1;
                int dx = ((i/9)%3)-1;
                int dy = ((i/3)%3)-1;
                permutations.add(original.shift(dx, dy).grow(dr).score(edgeImage));
            }

            Circle best = Collections.max(permutations);

            if (best.score >= FINAL_SCORE_THRESHOLD)
            {
                output.add(best);
            }
        }
        return output;
    }
}
