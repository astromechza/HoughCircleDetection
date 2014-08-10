package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * HoughFilter Class
 */
public class HoughFilter
{

    // static variables for accessory image creation
    private static boolean createHoughAccumImage = false;
    private static boolean normaliseImagerByCircLength = true;
    private static ShortImageBuffer lastHoughAccumImage;

    /**
     * Perform a Hough Circle Detection on the given edge image.
     *
     * @param edges Edge Detected Image result
     * @param minRadius minimum radius to search for
     * @param maxRadius maximum radius to search for
     * @param centerThreshold percentage of circumpherence needed to qualify
     * @return a list of circles that qualify (Note these may include circles that overlap)
     */
    public static List<Circle> identify(ShortImageBuffer edges, final int minRadius, final int maxRadius, final float centerThreshold)
    {
        // sanity checks
        if (minRadius >= maxRadius || minRadius < 0 || maxRadius < 0)
            throw new IllegalArgumentException("min radius and max radius are invalid");
        if (centerThreshold <= 0 || centerThreshold >= 1)
            throw new IllegalArgumentException("centerThreshold is invalid!");

        // dimensions
        int height = edges.getHeight();
        int heightWithBorder = height + 2* maxRadius;
        int width = edges.getWidth();
        int widthWithBorder = width + 2* maxRadius;
        int depth = maxRadius - minRadius;

        // create hough space
        int[] space = new int[heightWithBorder*widthWithBorder*depth];
        int nx,ny,ay,ax,cx,cy,radiusError,sx,sy;
        int memBOffset = widthWithBorder*depth;
        for(int y=0;y<height;y++)
        {
            ay = (y + maxRadius) * memBOffset;
            for(int x=0;x<width;x++)
            {
                if (edges.get(y,x) != 0)
                {
                    ax = (x + maxRadius) * depth;
                    for(int r=0;r<depth;r++)
                    {
                        cx = minRadius + r;
                        cy = 0;
                        radiusError = 1-cx;
                        while(cx >= cy)
                        {

                            // highly optimised index calculation and increment
                            // this may look strange but it has been carefully worked out

                            sx = cx * depth;
                            sy = cy * memBOffset;

                            nx = ax + sx;
                            ny = ay + sy;
                            space[ny + nx + r]++;
                            nx = ax - sx;
                            space[ny + nx + r]++;
                            ny = ay - sy;
                            space[ny + nx + r]++;
                            nx = ax + sx;
                            space[ny + nx + r]++;

                            sx = cy * depth;
                            sy = cx * memBOffset;

                            nx = ax + sx;
                            ny = ay + sy;
                            space[ny + nx + r]++;
                            ny = ay - sy;
                            space[ny + nx + r]++;
                            nx = ax - sx;
                            space[ny + nx + r]++;
                            ny = ay + sy;
                            space[ny + nx + r]++;

                            cy++;

                            if (radiusError >= 0) radiusError -= 2 * (cx--);

                            radiusError += 2 * cy + 1;
                        }
                    }
                }
            }
        }

        // cache circumference lengths for the next step
        // avoid expensive divide operation by caching a pre multiplied threshold version
        float[] circLength = new float[depth];
        float[] circLengthThreshold = new float[depth];
        for(int r=0;r<depth;r++)
        {
            int py = minRadius + r;
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
            circLengthThreshold[r] = circLength[r] * centerThreshold;
        }

        // output list, we assume there will be around 10 circles
        List<Circle> output = new ArrayList<>(10);
        for(int y=0;y<height;y++)
        {
            ay = (y + maxRadius) * memBOffset;
            for(int x=0;x<width;x++)
            {
                ax = (x + maxRadius) * depth;
                for(int r=0;r<depth;r++)
                {
                    float score = space[ay + ax + r];
                    if (score > circLengthThreshold[r])
                    {
                        output.add(new Circle(x, y, score / circLength[r], minRadius + r));
                    }
                }
            }
        }

        // if we need to create an image of the accumulation landscape, enter this block
        if (createHoughAccumImage) createHoughAccumulatorImage(heightWithBorder, widthWithBorder, depth, space, circLength);

        return output;
    }

    private static void createHoughAccumulatorImage(int heightWithBorder, int widthWithBorder, int depth, int[] space, float[] circLength)
    {
        // output container
        ShortImageBuffer image = new ShortImageBuffer(heightWithBorder, widthWithBorder);

        float maxValue = 1;
        // if we don't normalise by the circle circumpherence, we need to calculate the max value
        // across the image in order to normalise based on that.
        if (!normaliseImagerByCircLength)
        {
            maxValue = 0;
            int index = 0;
            for(int y=0;y<heightWithBorder;y++)
            {
                for(int x=0;x<widthWithBorder;x++)
                {
                    for(int r=0;r<depth;r++)
                    {
                        float value = space[index];
                        if (value > maxValue) maxValue = value;
                        index++;
                    }
                }
            }
        }

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
                    float divisor = (normaliseImagerByCircLength) ? circLength[r] : maxValue;
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
        lastHoughAccumImage = image;
    }

    public static void setCreateHoughAccumImage(boolean input)
    {
        createHoughAccumImage = input;
    }

    public static void setNormaliseImagerByCircLength(boolean input)
    {
        if (input && !createHoughAccumImage) throw new IllegalArgumentException("createHoughAccumImage must be True!");
        normaliseImagerByCircLength = input;
    }

    public static ShortImageBuffer getLastHoughAccumImage()
    {
        return lastHoughAccumImage;
    }
}
