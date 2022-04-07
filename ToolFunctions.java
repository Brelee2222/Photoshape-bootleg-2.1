/*
Has the functions to paint on the image
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.Arrays;

interface OptionsToolPanel {
    void changeToolOptions();
}

class PaintBrush implements MouseInputListener, OptionsToolPanel {

    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
    int brushSize = 15;

    int prevMouseLocX;
    int prevMouseLocY;

    OptionContent[] options = new OptionContent[]{
            new OptionContent("Brush Size ", new OptionContent.SliderOption(0, 255, brushSize) {
                @Override
                public void stateChanged(ChangeEvent e) {
                    options[0].optionName.setText(options[0].name + ((JSlider) options[0].optionValue).getValue());
                }
            })
    };

    public PaintBrush(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;
        canvasGraphics.addMouseListener(this);
        canvasGraphics.addMouseMotionListener(this);
        changeToolOptions();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        canvasGraphics.newActionHistory();
        TranslationMultiplier translationMultiplier = canvasGraphics.translationMultiplier;

        setMouseLoc((int) (e.getX() / translationMultiplier.multiplierX), (int) (e.getY() / translationMultiplier.multiplierY));
        draw(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        draw(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void draw(MouseEvent e) {
        BufferedImage image = canvas.image;
        Graphics2D pen = (Graphics2D) image.getGraphics();
        TranslationMultiplier translationMultiplier = canvasGraphics.translationMultiplier;

        pen.setColor(canvas.photoshapeGraphics.penColor);

        int x = (int) (e.getX() / translationMultiplier.multiplierX);
        int y = (int) (e.getY() / translationMultiplier.multiplierY);
        pen.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        pen.drawLine(x, y, prevMouseLocX, prevMouseLocY);

        setMouseLoc(x, y);
        canvasGraphics.update(canvasGraphics.getPen());
        canvasGraphics.draw();
    }

    void setMouseLoc(int X, int Y) {
        prevMouseLocX = X;
        prevMouseLocY = Y;
    }

    @Override
    public void changeToolOptions() {
        canvas.optionsDialogue.displayableOptions.switchOptions(options, () ->
            brushSize = ((JSlider) options[0].optionValue).getValue()
        );
    }
}

class LineTool implements MouseInputListener, OptionsToolPanel {

    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
    int lineThickness = 15;

    int initialX;
    int initialY;

    OptionContent[] options = new OptionContent[]{
            new OptionContent("Line Thickness ", new OptionContent.SliderOption(0, 255, lineThickness) {
                @Override
                public void stateChanged(ChangeEvent e) {
                    options[0].optionName.setText(options[0].name + ((JSlider) options[0].optionValue).getValue());
                }
            })
    };

    public LineTool(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        canvasGraphics.newActionHistory();
        initialX = e.getX();
        initialY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Graphics2D pen = (Graphics2D) canvas.image.getGraphics();
        pen.setColor(canvas.photoshapeGraphics.penColor);
        pen.setStroke(new BasicStroke(lineThickness));
        canvasGraphics.smoothDraw();
        pen.drawLine((int) (e.getX() / canvasGraphics.translationMultiplier.multiplierX), (int) (e.getY() / canvasGraphics.translationMultiplier.multiplierY), (int) (initialX / canvasGraphics.translationMultiplier.multiplierX), (int) (initialY / canvasGraphics.translationMultiplier.multiplierX));

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        draw(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void draw(MouseEvent e) {
        Graphics2D pen = canvasGraphics.getPen();
        int x = e.getX();
        int y = e.getY();

        pen.setColor(canvas.photoshapeGraphics.penColor);
        pen.setStroke(new BasicStroke(lineThickness));
        canvasGraphics.smoothDraw();
        pen.drawLine(x, y, initialX, initialY);
        canvasGraphics.display();
        canvasGraphics.update(pen);
    }

    @Override
    public void changeToolOptions() {
        canvas.optionsDialogue.displayableOptions.switchOptions(options, () ->
            lineThickness = ((JSlider) options[0].optionValue).getValue()
        );
    }
}

class Erase implements MouseInputListener, OptionsToolPanel {
    public int brushSize = 15;
    public int[] brushSizeList;
    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

    OptionContent[] options = new OptionContent[]{
      new OptionContent("Eraser Size ", new OptionContent.SliderOption(0, 255, brushSize * 2) {
          @Override
          public void stateChanged(ChangeEvent e) {
              options[0].optionName.setText(options[0].name + ((JSlider) options[0].optionValue).getValue());
          }
      })
    };

    public Erase(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //will be changed
        brushSizeList = new int[2*brushSize];
        Arrays.fill(brushSizeList, 0x00000000);
        draw(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvasGraphics.newActionHistory();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        draw(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void draw(MouseEvent e) {
        int brushDiameterX = 2*brushSize;
        int brushDiameterY = brushDiameterX;
        BufferedImage image = canvas.image;
        TranslationMultiplier translationMultiplier = canvasGraphics.translationMultiplier;
        int x = (int) (e.getX() / translationMultiplier.multiplierX) - brushSize;
        int y = (int) (e.getY() / translationMultiplier.multiplierY) - brushSize;
        if(x < 0)
            x = 0;
        if(y < 0)
            y = 0;

        if(brushDiameterX + x > image.getWidth())
            brushDiameterX = image.getWidth() - x;
        if(brushDiameterY + y > image.getHeight())
            brushDiameterY = image.getHeight() - y;
        image.setRGB(x, y, brushDiameterX, brushDiameterY, brushSizeList, 0, 0);
        canvasGraphics.update(canvasGraphics.getPen());
        canvasGraphics.draw();
    }

    @Override
    public void changeToolOptions() {
        canvas.optionsDialogue.displayableOptions.switchOptions(options, () ->
                brushSize = ((JSlider) options[0].optionValue).getValue()/2
        );
    }
}

class PaintBucket implements MouseInputListener, OptionsToolPanel {
    //I'm adapting a pathfinding algorithm because I'm using one to make a bot in node.js, and applying the concept to here would be fun.
    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

    boolean debugOn = false;
    int tolerance = 50;

    OptionContent[] options = new OptionContent[]{
            new OptionContent("Tolerance ", new OptionContent.SliderOption(0, 255, tolerance) {
                @Override
                public void stateChanged(ChangeEvent e) {
                    options[0].optionName.setText(options[0].name + ((JSlider) options[0].optionValue).getValue());
                }
            }),
            new OptionContent("Debug Mode ", new JCheckBox())
    };

    public PaintBucket(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        canvasGraphics.newActionHistory();

        TranslationMultiplier translationMultiplier = canvasGraphics.translationMultiplier;


        int x = (int) (e.getX() / translationMultiplier.multiplierX);
        int y = (int) (e.getY() / translationMultiplier.multiplierY);
        new BucketPath(x, y, debugOn).findPath(x, y);
        canvas.image.setRGB(x, y, canvasGraphics.penColor.getRGB());
        canvasGraphics.draw();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void changeToolOptions() {
        canvas.optionsDialogue.displayableOptions.switchOptions(options, () -> {
                    tolerance = ((JSlider) options[0].optionValue).getValue();
                    debugOn = ((JCheckBox) options[1].optionValue).isSelected();
                }
        );
    }

    class BucketPath extends Pathfinder {


        BufferedImage image = canvas.image;
        int color = canvasGraphics.penColor.getRGB();

        int selectedColor;
        int selectedColorR;
        int selectedColorG;
        int selectedColorB;

        BucketPath(int x, int y) {
            selectedColor = image.getRGB(x, y);
            selectedColorR = selectedColor >> 16 & 0xff;
            selectedColorG = selectedColor >> 8 & 0xff;
            selectedColorB = selectedColor & 0xff;

            tileMap = new TileMap();
        }

        BucketPath(int x, int y, boolean debugOn) {
            selectedColor = image.getRGB(x, y);
            selectedColorR = selectedColor >> 16 & 0xff;
            selectedColorG = selectedColor >> 8 & 0xff;
            selectedColorB = selectedColor & 0xff;

            tileMap = new TileMap();

            if(debugOn) {
                super.indicateTailSkip = () -> {
                    image.setRGB(currentTile.position.x, currentTile.position.y, color ^ 0xffffff);
                    canvasGraphics.draw();
                    currentTile = currentTile.tailingNode;
                };
            }

        }

        @Override
        public void move() {
            super.move();
            image.setRGB(currentTile.position.x, currentTile.position.y, color);
        }

        @Override
        public boolean tileIsWall(Position position) {
            if(position.x >= image.getWidth() || position.x < 0 || position.y >= image.getHeight() || position.y < 0)
                return true;

//            return image.getRGB(position.x, position.y) != selectedColor;
            return !isTolerable(image.getRGB(position.x, position.y));
        }

        public boolean isTolerable(int color) {
            int b = color & 0xff;
            color >>= 8;
            int g = color & 0xff;
            color >>= 8;
            int r = color & 0xff;

            int rD = selectedColorR - r;
            int gD = selectedColorG - g;
            int bD = selectedColorB - b;
            return (tolerance >= rD && rD >= -tolerance) && (tolerance >= gD && gD >= -tolerance) && (tolerance >= bD && bD >= -tolerance);
        }
        class TileMap extends SimpleTileMap{
            boolean[][] map = new boolean[image.getWidth()][image.getHeight()];


            public void put(Position key) {
                map[key.x][key.y] = true;
            }

            public boolean containsKey(Position key) {
                return map[key.x][key.y];
            }
        }
    }
}
