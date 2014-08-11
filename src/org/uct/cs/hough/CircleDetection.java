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
    private static final int DEFAULT_MIN_RADIUS = 10;
    private static final int DEFAULT_MAX_RADIUS = 100;
    private static final float FINAL_SCORE_THRESHOLD = 0.85f;
    private static final float CENTER_THRESHOLD = 0.4f;
    private static final int EDGE_THRESHOLD = 550;

    private static boolean storeHoughAccumImage = false;
    private static BufferedImage storedHoughImage;
    private static boolean storeEdgeImage = false;
    private static BufferedImage storedEdgeImage;

    public static Collection<Circle> detect(BufferedImage input)
    {
        int width = input.getWidth();
        int height = input.getHeight();

        // make sure the image is in BGR
        if (input.getType() != BufferedImage.TYPE_3BYTE_BGR)
        {
            BufferedImage midway = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            midway.getGraphics().drawImage(input, 0, 0, null);
            input = midway;
        }

        // convert to greyscale
        ShortImageBuffer greyscale = fromBufferedImage(input);

        ShortImageBuffer edges = SobelEdgeDetector.apply(
            Normalizer.norm(greyscale)
        );

        edges = HighPassFilter.threshold(edges, EDGE_THRESHOLD );

        if (storeEdgeImage) storedEdgeImage = edges.toImage();

        int final_min_radius = DEFAULT_MIN_RADIUS;
        int final_max_radius = Math.min(DEFAULT_MAX_RADIUS, Math.min(width, height));

        int[] oneDHoughSpace = HoughFilter.run(edges, final_min_radius, final_max_radius);

        int numRadii = final_max_radius - final_min_radius;
        // cache circumference lengths for the next step
        // avoid expensive divide operation by caching a pre multiplied threshold version
        float[] circLength = new float[numRadii];
        float[] circLengthThreshold = new float[numRadii];
        for(int r=0;r<numRadii;r++)
        {
            int py = final_min_radius + r;
            int px = 0;
            int d = (5-py*4)/4;
            do
            {
                circLength[r] += 8;
                if (d >= 0) d += - 2 * py--;
                d += 2 * ++px;
            }
            while(px <= py);
            // premultiply
            circLengthThreshold[r] = circLength[r] * CENTER_THRESHOLD;
        }

        // output list, we assume there will be around 10 circles
        List<Circle> circleCandidates = new ArrayList<>(10);
        int memBOffset = numRadii * (width + 2*final_max_radius);
        for(int y=0;y<height;y++)
        {
            int ay = (y + final_max_radius) * memBOffset;
            for(int x=0;x<width;x++)
            {
                int ax = (x + final_max_radius) * numRadii;
                for(int r=0;r<numRadii;r++)
                {
                    float score = oneDHoughSpace[ay + ax + r];
                    if (score > circLengthThreshold[r])
                    {
                        circleCandidates.add(new Circle(x, y, score / circLength[r], final_min_radius + r));
                    }
                }
            }
        }

        // if we need to create an image of the accumulation landscape, enter this block
        if (storeHoughAccumImage) createHoughAccumulatorImage((height + 2*final_max_radius), (width + 2*final_max_radius), numRadii, oneDHoughSpace, circLength);

        Collection<Circle> circles = filterOverlaps(circleCandidates);
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

    private static void createHoughAccumulatorImage(int heightWithBorder, int widthWithBorder, int depth, int[] space, float[] circLength)
    {
        // output container
        ShortImageBuffer image = new ShortImageBuffer(heightWithBorder, widthWithBorder);

        // now we loop through the image to create the pixels
        int index = 0;
        for(int y=0;y<heightWithBorder;y++)
        {
            for(int x=0;x<widthWithBorder;x++)
            {
                // we are using a 2d image to represent a 3d array, so we choose the brightest pixel
                float bestScore = 0;
                for(int r=0;r<depth;r++)
                {
                    // choose the correct normaliser
                    float divisor = circLength[r];
                    float score = space[index] / divisor;
                    if (score > bestScore) bestScore = score;
                    index++;
                }

                // cap the value between 0 and 1
                bestScore = Math.min(1, Math.max(0, bestScore));
                // create final pixel
                image.set(y, x, (short)(bestScore * 0xFF));
            }
        }

        // store
        storedHoughImage = image.toImage();
    }

    public static void storeEdgeImage()
    {
        storeEdgeImage = true;
    }

    public static void storeHoughAccumImage()
    {
        storeHoughAccumImage = true;
    }

    public static BufferedImage getStoredHoughAccumImage()
    {
        return storedHoughImage;
    }

    public static BufferedImage getStoredEdgeImage()
    {
        return storedEdgeImage;
    }
}
