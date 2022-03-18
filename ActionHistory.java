import java.awt.image.BufferedImage;

public class ActionHistory {
    BufferedImage image;
    ActionHistory prev;
    ActionHistory next;

    ActionHistory(BufferedImage image) {
        this.image = image;
    }
}
