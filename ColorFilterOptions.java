import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class ColorFilterOptions extends Menu {

    PhotoshapeCanvas canvas;


    public ColorFilterOptions(String title, PhotoshapeCanvas canvas) {
        super(title);
        this.canvas = canvas;

        /*I'm leaving the variables here in case I need to add anything
        MenuItem grayScale = add(new GrayScaler("Gray Scale"));
        MenuItem invertedColor = add(new ColorInverter("Inverted Color"));
         */
        add(new GrayScaler("Gray Scale"));
        add(new ColorInverter("Inverted Color"));
        add(new CanvasBlurrerMenu());
    }

    class GrayScaler extends MenuItem implements ActionListener {
        ColorChanger colorChanger = new Grayify();
        PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

        {
            addActionListener(this);
        }

        GrayScaler(String title) {
            super(title);

            canvasGraphics = canvas.photoshapeGraphics;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedImage image = canvas.image;
            long startingTime = System.currentTimeMillis();
            for(int i = 0; i < image.getWidth(); i++)
                for(int j = 0; j < image.getHeight(); j++) {
                    image.setRGB(i, j, colorChanger.filter(image.getRGB(i, j)));
                }
            System.out.println(System.currentTimeMillis() - startingTime);
            canvasGraphics.update(canvasGraphics.getPen());
            canvasGraphics.draw();
        }
    }

    class ColorInverter extends MenuItem implements ActionListener {
        ColorChanger colorChanger = new ColorInvert();
        PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

        {
            addActionListener(this);
        }

        ColorInverter(String title) {
            super(title);

            canvasGraphics = canvas.photoshapeGraphics;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /**
             * Previous Inverter
             */
            /*
            BufferedImage image = canvas.image;
            Graphics pen = image.getGraphics();

            pen.setXORMode(new Color(0, 0, 0, 0));
            pen.fillRect(0, 0, image.getWidth(), image.getHeight());
            canvasGraphics.update(image.getGraphics());
            canvasGraphics.draw();
             */

            BufferedImage image = canvas.image;

            for(int i = 0; i < image.getWidth(); i++)
                for(int j = 0; j < image.getHeight(); j++) {
                    image.setRGB(i, j, colorChanger.filter(image.getRGB(i, j)));
                }
            canvasGraphics.update(canvasGraphics.getPen());
            canvasGraphics.draw();
        }
    }

    class CanvasBlurrerMenu extends Menu {

        CanvasBlurrer CanvasBlurrer = new CanvasBlurrer("Blur");

        CanvasBlurrerMenu() {
            super("Blur");

            add(CanvasBlurrer);
            add(new CanvasBlurrerOptions());
        }

        class CanvasBlurrer extends MenuItem implements ActionListener {

            /*
            4/6/2022 : significantly cut down blur computing time. Might add debug, inverted averaging
             */

            short[][] stripAverageSection;

            PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
            int blurrIntensity = 50;
            int blurIntensityDimensions;

            boolean invertedAveraging = false;

            CanvasBlurrer(String title) {
                super(title);

                addActionListener(this);

                blurIntensityDimensions = 2 * blurrIntensity;

                canvasGraphics = canvas.photoshapeGraphics;
            }

            int getBlurAverage(int startX, int startY, int endX, int endY) {

                BufferedImage image = canvas.image;

                int nonCalcCount = 0;

                int rAvg = 0;
                int gAvg = 0;
                int bAvg = 0;
                int aAvg = 0;

                for(int cx = startX; cx < startX + endX; cx++) {
                    if (stripAverageSection[nonCalcCount] == null) {
                        stripAverageSection[nonCalcCount] = new short[4];
                        int[] rgbPixelStrip = image.getRGB(cx, startY, 1, endY, null, 0, 1);
                        int[][] rgbPixelColorStrip = new int[4][endY];
                        for (int i = 0; i < endY; i++) {
                            rgbPixelColorStrip[0][i] = (rgbPixelStrip[i] >> 16) & 0xff;
                            rgbPixelColorStrip[1][i] = (rgbPixelStrip[i] >> 8) & 0xff;
                            rgbPixelColorStrip[2][i] = rgbPixelStrip[i] & 0xff;
                            rgbPixelColorStrip[3][i] = (rgbPixelStrip[i] >> 24) & 0xff;
                        }
                        stripAverageSection[nonCalcCount][0] = (short) Arrays.stream(rgbPixelColorStrip[0]).average().getAsDouble();
                        stripAverageSection[nonCalcCount][1] = (short) Arrays.stream(rgbPixelColorStrip[1]).average().getAsDouble();
                        stripAverageSection[nonCalcCount][2] = (short) Arrays.stream(rgbPixelColorStrip[2]).average().getAsDouble();
                        stripAverageSection[nonCalcCount][3] = (short) Arrays.stream(rgbPixelColorStrip[3]).average().getAsDouble();
                    }
                    rAvg += stripAverageSection[nonCalcCount][0];
                    gAvg += stripAverageSection[nonCalcCount][1];
                    bAvg += stripAverageSection[nonCalcCount][2];
                    aAvg += stripAverageSection[nonCalcCount++][3];
                }

                if(invertedAveraging)
                    return (aAvg / endY) << 24 | (rAvg / endY) << 16 | (gAvg / endY) << 8 | bAvg / endY;
                return (aAvg / nonCalcCount) << 24 | (rAvg / nonCalcCount) << 16 | (gAvg / nonCalcCount) << 8 | bAvg / nonCalcCount;

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage image = canvas.image;

                BufferedImage blurrImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null);


                short[][] emptyList = new short[blurIntensityDimensions][4];
                for(int i = 0; i < emptyList.length; i++)
                    emptyList[i] = null;
                int startY;
                int endY;
                int startX;
                int endX;
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                for (int i = 0; i < image.getHeight(); i++) {
                    startY = i-blurrIntensity;
                    endY = blurIntensityDimensions;
                    stripAverageSection = emptyList.clone();
                    if(startY < 0) {
                        endY += startY;
                        startY = 0;
                    }
                    if(endY + startY >= imageHeight)
                        endY = imageHeight - startY - 1;
//                    int oldestStrip = 0;
                    for (int j = 0; j < image.getWidth(); j++) {

                        startX = j-blurrIntensity;
                        endX = blurIntensityDimensions;

                        if(startX < 0) {
                            endX += startX;
                            startX = 0;
                        }

                        if(endX + startX >= imageWidth)
                            endX = imageWidth - startX - 1;

                        blurrImage.setRGB(j, i, getBlurAverage(startX, startY, endX, endY));

                        if (stripAverageSection[stripAverageSection.length - 1] != null) {
                            for (int k = 0; k < stripAverageSection.length - 1; )
                                stripAverageSection[k++] = stripAverageSection[k];
                            stripAverageSection[stripAverageSection.length - 1] = null;
                        }




                        stripAverageSection[0] = null;
//                        stripAverageSection[oldestStrip++] = null;
//                        if(oldestStrip == blurIntensityDimensions)
//                            oldestStrip = 0;
                    }
//                    canvasGraphics.getPen().drawImage(blurrImage, 0, 0, canvasGraphics.getWidth(), canvasGraphics.getHeight(), null);
//                    canvasGraphics.display();
                }
                canvas.image = blurrImage;
                canvasGraphics.update(canvasGraphics.getPen());
                canvasGraphics.draw();
            }
        }

        class CanvasBlurrerOptions extends MenuItem implements ActionListener {

            JFrame BlurOptionsFrame = new BlurOptions();

            CanvasBlurrerOptions() {
                super("Blur Options");

                addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                BlurOptionsFrame.setVisible(true);
            }

            class BlurOptions extends JFrame{

                JPanel SliderContainer = new JPanel();
                JSlider SliderIntensity = new SliderIntensity();
                JLabel SliderLabel = new JLabel("Blur Intensity " + CanvasBlurrer.blurrIntensity);
                JPanel InvertedAvgContainer = new JPanel();
                JCheckBox InvertedAveragingCheck = new JCheckBox();

                BlurOptions() {
                    super("Blur Options");

                    setLayout(new BorderLayout());
                    SliderContainer.setLayout(new BoxLayout(SliderContainer, BoxLayout.X_AXIS));
                    InvertedAvgContainer.setLayout(new BoxLayout(InvertedAvgContainer, BoxLayout.X_AXIS));

                    InvertedAvgContainer.add(new JLabel("Inverted Averaging"));
                    InvertedAvgContainer.add(InvertedAveragingCheck);
                    SliderContainer.add(SliderLabel);
                    SliderContainer.add(SliderIntensity);

                    add(SliderContainer, BorderLayout.NORTH);
                    add(InvertedAvgContainer, BorderLayout.CENTER);

                    add(new OkButton(), BorderLayout.SOUTH);

                    pack();
                }

                class OkButton extends JButton implements ActionListener{
                    OkButton() {
                        super("OK");

                        addActionListener(this);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        CanvasBlurrer.blurrIntensity = SliderIntensity.getValue();
                        CanvasBlurrer.blurIntensityDimensions = 2 * CanvasBlurrer.blurrIntensity + 1;
                        CanvasBlurrer.invertedAveraging = InvertedAveragingCheck.isSelected();
                        BlurOptionsFrame.setVisible(false);
                    }
                }
                class SliderIntensity extends JSlider implements ChangeListener {

                    SliderIntensity() {
                        super(0, 255, CanvasBlurrer.blurrIntensity);
                        addChangeListener(this);
                    }

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        SliderLabel.setText("Blur Intensity " + SliderIntensity.getValue());
                    }
                }
            }
        }

    }
}
