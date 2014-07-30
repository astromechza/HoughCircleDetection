package org.uct.cs.hough.pipes;

public class NormalizePipe
{
    public ShortImageBuffer flow(ShortImageBuffer before)
    {
        ShortImageBuffer after = before.copyShape();
        int min = 0xFF;
        int max = 0;
        for(int y=0;y<before.getHeight();y++)
        {
            for(int x=0;x<before.getWidth();x++)
            {
                int v = before.get(y, x) & 0xFF;
                min = (v < min) ? v : min;
                max = (v > max) ? v : max;
            }
        }

        int diff = (max-min);
        if (diff > 0)
        {
            float scale = 255.0f / diff;

            for (int y = 0; y < before.getHeight(); y++)
            {
                for (int x = 0; x < before.getWidth(); x++)
                {
                    int v = before.get(y, x) & 0xFF;
                    after.set(y, x, (short)((int)((v - min) * scale) & 0xFF));
                }
            }
        }

        return after;
    }
}
