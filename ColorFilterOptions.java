import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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
        add(new CanvasBlurrer("Blur"));
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
    class CanvasBlurrer extends MenuItem implements ActionListener {
        PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
        int blurrIntensity = 1;
        int blurIntensityDimensions;

        {
            addActionListener(this);

            blurIntensityDimensions = 2* blurrIntensity + 1;
        }

        CanvasBlurrer(String title) {
            super(title);

            canvasGraphics = canvas.photoshapeGraphics;
        }

        int getBlurAverage(int x, int y) {

            BufferedImage image = canvas.image;
            int width = image.getWidth();
            int height = image.getHeight();

            int startX = x - blurrIntensity;
            int startY = y - blurrIntensity;

            int endX = blurIntensityDimensions;
            int endY = blurIntensityDimensions;

            int totalAverageValue = 0;

            int totalRed = 0;
            int totalBlue = 0;
            int totalGreen = 0;

            if(endX + startX >= width)
                endX = width - 1 - startX;
            if(endY + startY >= height)
                endY = height - 1 - startY;

            if (startX < 0)
                startX = 0;
            if(startY < 0)
                startY = 0;

            int[] rgbVals = image.getRGB(startX, startY, endX, endY, null, 0, endX);

            for (int m : rgbVals) {
                    totalAverageValue++;
                    totalBlue += m & 0xff;
                    m >>= 8;
                    totalGreen += m & 0xff;
                    m >>= 8;
                    totalRed += m & 0xff;
            }

            int rgb = image.getRGB(x, y);
            totalRed /= totalAverageValue;
            totalGreen /= totalAverageValue;
            totalBlue /= totalAverageValue;

            return (rgb & 0xff000000) | totalRed<<16 | totalGreen<<8 | totalBlue;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedImage image = canvas.image;

            BufferedImage blurrImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null);

            for(int i = 0; i < image.getWidth(); i++)
                for(int j = 0; j < image.getHeight(); j++) {
                    blurrImage.setRGB(i, j, getBlurAverage(i, j));
                }
            canvas.image = blurrImage;
            canvasGraphics.update(canvasGraphics.getPen());
            canvasGraphics.draw();
        }
    }
    
}
