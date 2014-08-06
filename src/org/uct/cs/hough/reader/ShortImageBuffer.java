package org.uct.cs.hough.reader;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class ShortImageBuffer
{
    private final int width;
    private final int height;
    private final short[] data;

    public ShortImageBuffer(int height, int width)
    {
        this.width = width;
        this.height = height;
        this.data = new short[height*width];
    }

    public short get(int y, int x)
    {
        return this.data[y*width + x];
    }

    public void set(int y, int x, short s)
    {
        this.data[y*width + x] = s;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public BufferedImage toImage()
    {
        BufferedImage output = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);

        WritableRaster raster = output.getRaster();
        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
        byte[] pixels = buffer.getData();
        int index = 0;
        for(int y=0;y<this.height;y++)
        {
            for(int x=0;x<this.width;x++)
            {
                byte v = (byte)this.get(y, x);
                pixels[index] = v;
                pixels[index+1] = v;
                pixels[index+2] = v;
                index += 3;
            }
        }

        return output;
    }

    public ShortImageBuffer copyShape()
    {
        return new ShortImageBuffer(this.height, this.width);
    }
}
