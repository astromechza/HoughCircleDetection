package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Constants;

public class SobelEdgeDetector
{
    public static ShortImageBuffer apply(ShortImageBuffer input)
    {
        ShortImageBuffer after = input.copyShape();
        for(int y=1;y<input.getHeight()-1;y++)
        {
            for(int x=1;x<input.getWidth()-1;x++)
            {
                int g00 = input.get(y-1, x-1) & Constants.BYTE;
                int g01 = input.get(y-1, x) & Constants.BYTE;
                int g02 = input.get(y-1, x+1) & Constants.BYTE;
                int g10 = input.get(y, x-1) & Constants.BYTE;
                int g12 = input.get(y, x+1) & Constants.BYTE;
                int g20 = input.get(y+1, x-1) & Constants.BYTE;
                int g21 = input.get(y+1, x) & Constants.BYTE;
                int g22 = input.get(y+1, x+1) & Constants.BYTE;

                int Gx = -g00 -2*g01 -g02 +g20 +2*g21 +g22;
                int Gy = -g00 -2*g10 -g20 +g02 +2*g12 +g22;

                double v = Math.sqrt(Gy*Gy + Gx*Gx)/4;
                if (v > Constants.BYTE) v = Constants.BYTE;
                after.set(y, x, (short) v);
            }
        }
        return after;
    }
}
