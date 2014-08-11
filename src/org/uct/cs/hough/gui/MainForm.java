package org.uct.cs.hough.gui;

import org.uct.cs.hough.CircleDetection;
import org.uct.cs.hough.processors.HoughFilter;
import org.uct.cs.hough.util.Circle;
import org.uct.cs.hough.util.CircleAdder;
import org.uct.cs.hough.util.ImageFileFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class MainForm
{
    private final int BORDER_GAP = 5;

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JButton loadImageButton;
    private JProgressBar progressBar1;
    private JFileChooser imageChooser;

    private ScalingImagePanel tabPanel1, tabPanel2, tabPanel3;

    public MainForm()
    {
        createGui();

        imageChooser = new JFileChooser();
        imageChooser.addChoosableFileFilter(new ImageFileFilter());
        imageChooser.setAcceptAllFileFilterUsed(false);

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
                            e1.printStackTrace();
                        }
                    }
                }
            }
        );
    }

    private void createGui()
    {
        panel1 = new JPanel(new BorderLayout(BORDER_GAP, BORDER_GAP));
        panel1.setBorder(BorderFactory.createEmptyBorder(BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP));
        final JPanel panel2 = new JPanel(new BorderLayout(BORDER_GAP, BORDER_GAP));

        loadImageButton = new JButton();
        loadImageButton.setText("Load Image");
        loadImageButton.setMnemonic('L');
        loadImageButton.setDisplayedMnemonicIndex(0);
        loadImageButton.setPreferredSize(new Dimension(120, 60));
        panel2.add(loadImageButton, BorderLayout.WEST);

        progressBar1 = new JProgressBar();
        panel2.add(progressBar1, BorderLayout.CENTER);

        panel1.add(panel2, BorderLayout.NORTH);

        tabbedPane1 = new JTabbedPane();
        tabPanel1 = new ScalingImagePanel();
        tabbedPane1.addTab("Original", tabPanel1);
        tabPanel2 = new ScalingImagePanel();
        tabbedPane1.addTab("Hough Space", tabPanel2);
        tabPanel3 = new ScalingImagePanel();
        tabbedPane1.addTab("Detected Circles", tabPanel3);
        tabbedPane1.setEnabled(false);

        panel1.add(tabbedPane1, BorderLayout.CENTER);
    }

    private void runCircleDetection(File f) throws IOException
    {
        BufferedImage image = ImageIO.read(f);

        tabbedPane1.setEnabled(true);
        tabPanel1.setImage(image);

        HoughFilter.setCreateHoughAccumImage(true);
        Collection<Circle> circles = CircleDetection.detect(image);

        BufferedImage output = CircleAdder.combine(image, circles);

        tabPanel2.setImage(HoughFilter.getLastHoughAccumImage().toImage());
        tabPanel3.setImage(output);
        tabbedPane1.setSelectedIndex(2);

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

        JFrame frame = new JFrame("Circle Detector");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

