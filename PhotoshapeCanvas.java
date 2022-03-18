import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoshapeCanvas extends JPanel {
    //fix: put inside another JPanel so that is doesn't update canvas on resize.

    BufferedImage image = new BufferedImage(800,800,BufferedImage.TYPE_INT_RGB);
    ActionHistory actionHistory = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
    PhotoshapeGraphics photoshapeGraphics = new PhotoshapeGraphics(800, 600);
    PhotoshapeCanvas() {
        setLayout(new BorderLayout());
        add(photoshapeGraphics, BorderLayout.CENTER);
        addComponentListener(new Resizing());

        MouseInputListener paint = new PaintBrush(this);
        photoshapeGraphics.addMouseListener(paint);
        photoshapeGraphics.addMouseMotionListener(paint);
    }

    class PhotoshapeGraphics extends ImagePhotoshaper {
        AspectRatio aspectRatio = new AspectRatio();
        TranslationMultiplier translationMultiplier = new TranslationMultiplier(new Dimension(image.getWidth(), image.getHeight()));
        Color penColor;

        {
            setColor(Color.RED);
            setCursor(new Cursor(1));
        }

        public PhotoshapeGraphics(int width, int height) {
            super(width, height);
        }

        public void loadImage(File filepath) {
            try {
                image = ImageIO.read(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Dimension dimension = new Dimension(image.getWidth(), image.getHeight());

            aspectRatio.setNewMultipliers(dimension);
            translationMultiplier = new TranslationMultiplier(dimension);
            //Fits image
            actionHistory = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
            resized();
        }

        public void setColor(Color penColor) {
            this.penColor = penColor;
        }

        public void resized() {
            Dimension dimension;

            int width = getWidth();
            int height = getHeight();

            int heightTranslation = (int) (width*aspectRatio.multiplierHeight);

            if(heightTranslation < height)
                dimension = new Dimension(width, heightTranslation);
            else
                dimension = new Dimension((int) (height*aspectRatio.multiplierWidth), height);
            sizeImage(dimension);
            translationMultiplier.setMultiplier(this.getSize());
            update(getPen());
            draw();
        }

        public void draw() {
            Graphics pen = getPen();

            pen.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);

            display();
        }

        public void smoothDraw() {
            Graphics pen = getPen();

            pen.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        }

        public void changeDrawFunction(MouseInputListener tool) {
            this.removeMouseListener(getMouseListeners()[0]);
            this.removeMouseMotionListener(getMouseMotionListeners()[0]);
            this.addMouseListener(tool);
            this.addMouseMotionListener(tool);
        }

        public void undo() {
            if(actionHistory.prev != null) {
                actionHistory = actionHistory.prev;
                image = actionHistory.image;
                update(getPen());
                draw();
            }
        }

        public void redo() {
            if(actionHistory.next != null) {
                actionHistory = actionHistory.next;
                image = actionHistory.image;
                update(getPen());
                draw();
            }
        }

        public void newActionHistory() {
            actionHistory.next = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
            actionHistory.next.prev = actionHistory;
            actionHistory = actionHistory.next;
            image = actionHistory.image;
        }

        public void sizeImage(Dimension dimension) {
            this.setSize(dimension);
            this.setPreferredSize(dimension);
        }
    }
    public class Resizing extends ComponentAdapter {

        public void componentResized(ComponentEvent event)
        {
            photoshapeGraphics.resized();
        }

    }
}
