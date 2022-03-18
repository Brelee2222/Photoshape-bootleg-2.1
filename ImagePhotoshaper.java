import java.awt.*;
import java.awt.image.*;

public class ImagePhotoshaper extends Canvas {
    private BufferStrategy rendering;

    public ImagePhotoshaper(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setSize(getPreferredSize());
        setIgnoreRepaint(true);
    }

    public void addNotify() {
        super.addNotify();
        createBufferStrategy(2);
        rendering = getBufferStrategy();
    }

    public Graphics2D getPen() {
        return (Graphics2D) rendering.getDrawGraphics();
    }

    public void clear() {
        repaint();
        addNotify();
    }

    public void display() {
        rendering.show();
    }

}
