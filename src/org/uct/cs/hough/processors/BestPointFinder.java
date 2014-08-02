package org.uct.cs.hough.processors;

import org.uct.cs.hough.reader.ShortImageBuffer;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class BestPointFinder
{
    public static List<Circle> find(ShortImageBuffer input, HoughFilter.HoughSpace houghSpace, int minThreshold, int maxThreshold)
    {

        List<Circle> circles = new ArrayList<>();
        for(int y=0;y<input.getHeight();y++)
        {
            for(int x=0;x<input.getWidth();x++)
            {
                int v = input.get(y, x) & Constants.BYTE;
                if (v >= minThreshold && v <= maxThreshold) circles.add(new Circle(x,y,houghSpace.getMaxRadii(y,x)));
            }
        }
        return circles;
    }
}
