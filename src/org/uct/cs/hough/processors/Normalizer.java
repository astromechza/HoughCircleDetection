package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Constants;

/** Normalizer
 * Simple class to normalize the colours in the image
 */
public class Normalizer
{
    public static ShortImageBuffer norm(ShortImageBuffer input)
    {
        ShortImageBuffer after = input.copyShape();

        // calculate min and max
        int min = Constants.BYTE;
        int max = 0;
        for(int y=0;y<input.getHeight();y++)
        {
            for(int x=0;x<input.getWidth();x++)
            {
                int v = input.get(y, x) & Constants.BYTE;
                min = (v < min) ? v : min;
                max = (v > max) ? v : max;
            }
        }

        // apply normalisation function to scale all values
        int diff = (max-min);
        if (diff > 0)
        {
            float scale = ((float)Constants.BYTE) / diff;

            for (int y = 0; y < input.getHeight(); y++)
            {
                for (int x = 0; x < input.getWidth(); x++)
                {
                    int v = input.get(y, x) & Constants.BYTE;
                    after.set(y, x, (short)((int)((v - min) * scale) & Constants.BYTE));
                }
            }
        }

        return after;
    }
}
