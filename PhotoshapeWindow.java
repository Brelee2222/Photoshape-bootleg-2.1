import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoshapeWindow extends JFrame {

    public PhotoshapeWindow(String title) {

        super(title);
        setLayout(new BorderLayout());

        JPanel photoshapeCanvas = new PhotoshapeCanvas();
        JPanel sectionToolSelection = new SectionToolSelectionPanel((PhotoshapeCanvas) photoshapeCanvas);
        MenuBar headerBar = new HeaderBar((PhotoshapeCanvas) photoshapeCanvas);

        add(photoshapeCanvas);
        add(sectionToolSelection, BorderLayout.WEST);
        setMenuBar(headerBar);

        pack();
        try {
            setIconImage(ImageIO.read(new File("./cat.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLocationRelativeTo(null);
        setVisible(true);

    }

}
