import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


//I have a tendency to capitalize things


public class PhotoshapeCanvas extends JPanel {
    //fix: put inside another JPanel so that is doesn't update canvas on resize.

    BufferedImage image;
    ActionHistory actionHistory;
    PhotoshapeGraphics photoshapeGraphics = new PhotoshapeGraphics(800, 600);
    OptionsWindow optionsDialogue = new OptionsWindow();

    PhotoshapeCanvas() {
        setLayout(new BorderLayout());
        add(photoshapeGraphics, BorderLayout.CENTER);
        addComponentListener(new Resizing());
    }

    class PhotoshapeGraphics extends ImagePhotoshaper {
        AspectRatio aspectRatio = new AspectRatio();
        TranslationMultiplier translationMultiplier;
        Color penColor;
        //I'm not sure if this is legal java code
//        {
//            setColor(Color.RED);
//            setCursor(new Cursor(1));
//        }

        public PhotoshapeGraphics(int width, int height) {
            super(width, height);
            setColor(Color.RED);
            loadImage(new File("./cat.jpg"));
            translationMultiplier = new TranslationMultiplier(new Dimension(image.getWidth(), image.getHeight()));
            actionHistory = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
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
            resize();
            actionHistory = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
        }

        public void setColor(Color penColor) {
            this.penColor = penColor;
        }

        public void sizeImage(Dimension dimension) {
            setSize(dimension);
            setPreferredSize(dimension);
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

        public void resize() {
            Dimension dimension;

            int width = getWidth();
            int height = getHeight();

            int heightTranslation = (int) (width * aspectRatio.multiplierHeight);

            if (heightTranslation < height)
                dimension = new Dimension(width, heightTranslation);
            else
                dimension = new Dimension((int) (height * aspectRatio.multiplierWidth), height);
            sizeImage(dimension);
            translationMultiplier.setMultiplier(getSize());
        }

        public void resized() {
            resize();
            update(photoshapeGraphics.getPen());
            photoshapeGraphics.draw();
        }

        public void newActionHistory() {
            actionHistory.next = new ActionHistory(new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null));
            actionHistory.next.prev = actionHistory;
            actionHistory = actionHistory.next;
            image = actionHistory.image;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            draw();
        }
    }
    //I made it resize the panel so that it doesn't blink
    public class Resizing extends ComponentAdapter {

        public void componentResized(ComponentEvent event)
        {
            photoshapeGraphics.resized();
        }

    }
}
