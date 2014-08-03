package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Constants;

/** HighPassFilter
 * Custom filter for filtering out any values below the given threshold
 */
public class HighPassFilter
{
    public static ShortImageBuffer threshold(ShortImageBuffer input, int threshold)
    {
        ShortImageBuffer after = input.copyShape();
        for(int y=0;y<input.getHeight();y++)
        {
            for(int x=0;x<input.getWidth();x++)
            {
                int v = input.get(y, x) & Constants.BYTE;
                // if value is big enough, pass through
                if (v > threshold) after.set(y, x, (short)(v));
            }
        }
        return after;
    }
}
