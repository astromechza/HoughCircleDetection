package org.uct.cs.hough.stages;

import java.awt.image.BufferedImage;

public interface ISource
{
    public ShortImageBuffer source(BufferedImage image);
}
