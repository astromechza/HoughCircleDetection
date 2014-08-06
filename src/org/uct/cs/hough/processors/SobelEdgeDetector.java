package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;

public class SobelEdgeDetector
{
    public static ShortImageBuffer apply(ShortImageBuffer input)
    {
        ShortImageBuffer after = input.copyShape();
        for(int y=1;y<input.getHeight()-1;y++)
        {
            for(int x=1;x<input.getWidth()-1;x++)
            {
                int g00 = input.get(y-1, x-1) & 0xFF;
                int g01 = input.get(y-1, x) & 0xFF;
                int g02 = input.get(y-1, x+1) & 0xFF;
                int g10 = input.get(y, x-1) & 0xFF;
                int g12 = input.get(y, x+1) & 0xFF;
                int g20 = input.get(y+1, x-1) & 0xFF;
                int g21 = input.get(y+1, x) & 0xFF;
                int g22 = input.get(y+1, x+1) & 0xFF;

                int Gx = - g00 - 2*g01 - g02 + g20 + 2*g21 + g22;
                int Gy = - g00 - 2*g10 - g20 + g02 + 2*g12 + g22;

                double v = Math.sqrt(Gy*Gy + Gx*Gx);
                if (v > 0xFFFF) v = 0xFFFF;
                after.set(y, x, (short) v);
            }
        }
        return after;
    }
}
