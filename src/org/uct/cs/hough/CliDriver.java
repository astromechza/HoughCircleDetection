package org.uct.cs.hough;

import org.apache.commons.cli.*;
import org.uct.cs.hough.display.PopUp;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.writer.ImageWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

class CliDriver
{
    public static void main(String[] args) throws IOException
    {
        CommandLine cmd = getArgs(args);

        String path = cmd.getArgs()[0];

        System.out.printf("File: '%s'%n", path);

        BufferedImage image = ImageIO.read(new File(path));

        Collection<Circle> circles = CircleDetection.detect(image);

        BufferedImage output = CircleAdder.combine(image, circles);

        if (cmd.hasOption("preview"))
        {
            System.out.println("Rendering popup");
            PopUp.show(output, path);
        }
        if (cmd.hasOption("save"))
        {
            String outpath = cmd.getOptionValue("save");
            System.out.printf("Saving Image to '%s'%n", outpath);
            ImageWriter.save(output, outpath);
        }

        System.out.printf("Detected: %d circles: %n", circles.size());
        System.out.printf("%-10s | %-10s | %-10s%n", "X", "Y", "radius");
        for(Circle c : circles) System.out.printf("%10s | %10s | %10s%n", c.x, c.y, c.circumference.radius);

    }

    private static CommandLine getArgs(String[] args)
    {
        CommandLineParser clp = new BasicParser();

        Options options = new Options();
        options.addOption(new Option("P", "preview", false, "Show a pop up window of result"));
        options.addOption(new Option("S", "save", true, "Save image to file"));

        try
        {
            CommandLine cmd = clp.parse(options, args);

            if (cmd.getArgs().length == 0) throw new ParseException("No source image specified!");

            return cmd;
        }
        catch (ParseException e)
        {
            System.out.printf("%s%n%n", e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printUsage(new PrintWriter(System.out), HelpFormatter.DEFAULT_WIDTH, "Circle Detection", options);
            formatter.printHelp("[OPTIONS] <source image file>", options);
            System.exit(1);
            return null;
        }
    }
}
