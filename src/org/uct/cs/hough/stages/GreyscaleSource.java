package org.uct.cs.hough.stages;

import org.uct.cs.hough.util.Constants;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class GreyscaleSource implements ISource
{
    private final Formula formula;

    public GreyscaleSource(Formula formula)
    {
        this.formula = formula;
    }

    @Override
    public ShortImageBuffer source(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        ShortImageBuffer output = new ShortImageBuffer(h, w);
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        int index = 0;
        for(int y=0;y<h;y++)
        {
            for(int x=0;x<w;x++)
            {
                output.set(y, x, this.formula.combine(pixels[index+2], pixels[index+1], pixels[index]));
                index += 3;
            }
        }

        return output;
    }

    private static interface IFormula
    {
        public short combine(byte r, byte g, byte b);
    }

    public static class FormulaAverage implements IFormula
    {
        @Override
        public short combine(byte r, byte g, byte b)
        {
            return (short) (((r & Constants.BYTE) + (g & Constants.BYTE) + (b & Constants.BYTE)) / 3);
        }
    }

    public static class FormulaLightness implements IFormula
    {
        @Override
        public short combine(byte r, byte g, byte b)
        {
            return (short)((
                Math.max(Math.max(r & Constants.BYTE, g & Constants.BYTE), b & Constants.BYTE) +
                Math.min(Math.min(r & Constants.BYTE, g & Constants.BYTE), b & Constants.BYTE)
            )/2);
        }
    }

    private static final float REDLUM = 0.21f;
    private static final float GREENLUM = 0.72f;
    private static final float BLUELUM = 0.01f;
    public static class FormulaLuminosity implements IFormula
    {
        @Override
        public short combine(byte r, byte g, byte b)
        {
            return (short)(
                REDLUM * (r & Constants.BYTE) +
                GREENLUM * (g & Constants.BYTE) +
                BLUELUM * (b & Constants.BYTE)
            );
        }
    }

    public enum Formula
    {
        AVERAGE(new FormulaAverage()),
        LIGHTNESS(new FormulaLightness()),
        LUMINOSITY(new FormulaLuminosity());

        private final IFormula f;

        Formula(IFormula f)
        {
            this.f = f;
        }

        public short combine(byte r, byte g, byte b)
        {
            return this.f.combine(r,g,b);
        }
    }

}
