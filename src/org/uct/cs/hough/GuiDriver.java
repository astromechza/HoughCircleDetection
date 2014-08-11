package org.uct.cs.hough;

import org.uct.cs.hough.gui.ScalingImagePanel;
import org.uct.cs.hough.processors.HoughFilter;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.util.ImageFileFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

public class GuiDriver
{
    private static final int BORDER_GAP = 5;

    private JFrame frame;
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton loadImageButton;
    private JFileChooser imageChooser;
    private JLabel statusBar;

    private ScalingImagePanel tabPanel1, tabPanel2, tabPanel3;

    public GuiDriver()
    {
        createGui();

        imageChooser = new JFileChooser();
        imageChooser.addChoosableFileFilter(new ImageFileFilter());
        imageChooser.setAcceptAllFileFilterUsed(false);
        imageChooser.setCurrentDirectory(Paths.get(".").toFile());

        loadImageButton.addMouseListener(
            new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    int result = imageChooser.showOpenDialog(panel1);
                    if (result == JFileChooser.APPROVE_OPTION)
                    {
                        try
                        {
                            runCircleDetection(imageChooser.getSelectedFile());
                        }
                        catch (IOException e1)
                        {
                            JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        );
    }

    private void createGui()
    {
        frame = new JFrame("Circle Detector");
        panel1 = new JPanel(new BorderLayout(BORDER_GAP, BORDER_GAP));
        panel1.setBorder(BorderFactory.createEmptyBorder(BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP));

        loadImageButton = new JButton();
        loadImageButton.setText("Load Image");
        loadImageButton.setMnemonic('L');
        loadImageButton.setDisplayedMnemonicIndex(0);
        loadImageButton.setPreferredSize(new Dimension(120, 40));

        panel1.add(loadImageButton, BorderLayout.NORTH);

        tabbedPane1 = new JTabbedPane();
        tabPanel1 = new ScalingImagePanel();
        tabbedPane1.addTab("Original", tabPanel1);
        tabPanel2 = new ScalingImagePanel();
        tabbedPane1.addTab("Hough Space", tabPanel2);
        tabPanel3 = new ScalingImagePanel();
        tabbedPane1.addTab("Detected Circles", tabPanel3);
        tabbedPane1.setEnabled(false);

        panel1.add(tabbedPane1, BorderLayout.CENTER);

        statusBar = new JLabel("Press 'Load Image' to perform Circle Detection.");
        panel1.add(statusBar, BorderLayout.SOUTH);

        frame.setContentPane(panel1);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void runCircleDetection(File f) throws IOException
    {
        BufferedImage image = ImageIO.read(f);

        tabbedPane1.setEnabled(true);
        tabPanel1.setImage(image);

        long startTime = System.nanoTime();

        HoughFilter.setCreateHoughAccumImage(true);
        Collection<Circle> circles = CircleDetection.detect(image);

        BufferedImage output = CircleAdder.combine(image, circles);

        tabPanel2.setImage(HoughFilter.getLastHoughAccumImage().toImage());
        tabPanel3.setImage(output);
        tabbedPane1.setSelectedIndex(2);


        long elapsed = System.nanoTime() - startTime;
        statusBar.setText(String.format("File: %s Elapsed: %s", f.getAbsolutePath(), org.uct.cs.hough.util.Timer.formatTime(elapsed)));

        frame.pack();
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        new GuiDriver();
    }
}

