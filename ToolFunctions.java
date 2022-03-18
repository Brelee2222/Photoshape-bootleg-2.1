/*
Has the functions to paint on the image
 */

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class PaintBrush implements MouseInputListener {

    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
    int brushSize = 15;

    int prevMouseLocX;
    int prevMouseLocY;

    public PaintBrush(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;
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
        pen.setStroke(new BasicStroke(brushSize));
        pen.drawLine(x, y, prevMouseLocX, prevMouseLocY);

        int brushDiameter = brushSize/2;
        pen.fillOval(x - brushDiameter, y - brushDiameter, brushSize, brushSize);
        setMouseLoc(x, y);
        canvasGraphics.update(canvasGraphics.getPen());
        canvasGraphics.draw();
    }

    void setMouseLoc(int X, int Y) {
        prevMouseLocX = X;
        prevMouseLocY = Y;
    }
}

class LineTool implements MouseInputListener {

    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;
    int lineThickness = 15;

    int initialX;
    int initialY;

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
        pen.setStroke(new BasicStroke(15));
        canvasGraphics.smoothDraw();
        pen.drawLine(x, y, initialX, initialY);
        canvasGraphics.display();
        canvasGraphics.update(pen);
    }
}

class Erase implements MouseInputListener {
    public int brushSize = 15;
    public int[] brushSizeList;
    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

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
}

class PaintBucket implements MouseInputListener {
    //I'm adapting a pathfinding algorithm because I'm using one to make a bot in node.js, and applying the concept to here would be fun.
    PhotoshapeCanvas canvas;
    PhotoshapeCanvas.PhotoshapeGraphics canvasGraphics;

    int tolerance = 25;

    public PaintBucket(PhotoshapeCanvas canvas) {
        this.canvas = canvas;
        this.canvasGraphics = canvas.photoshapeGraphics;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        TranslationMultiplier translationMultiplier = canvasGraphics.translationMultiplier;



        int x = (int) (e.getX() / translationMultiplier.multiplierX);
        int y = (int) (e.getY() / translationMultiplier.multiplierY);

        new BucketPath(x, y).findPath(x, y);
        System.out.println("done");
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
        }

        @Override
        public void move() {
            super.move();
            image.setRGB(currentTile.position.x, currentTile.position.y, color);
            canvasGraphics.draw();
        }

        @Override
        public void indicateTailSkip() {
            image.setRGB(currentTile.position.x, currentTile.position.y, 0xff0000ff);
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
    }
}
