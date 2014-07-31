package org.uct.cs.hough.stages;

import org.uct.cs.hough.util.Constants;

public class NormalizeStage implements IStage
{
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        int min = Constants.BYTE;
        int max = 0;
        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & Constants.BYTE;
                min = (v < min) ? v : min;
                max = (v > max) ? v : max;
            }
        }

        int diff = (max-min);
        if (diff > 0)
        {
            float scale = ((float)Constants.BYTE) / diff;

            for (int y = 0; y < before.getHeight(); y++)
            {
                for (int x = 0; x < before.getWidth(); x++)
                {
                    int v = before.get(y, x) & Constants.BYTE;
                    after.set(y, x, (short)((int)((v - min) * scale) & Constants.BYTE));
                }
            }
        }

        return after;
    }
}
