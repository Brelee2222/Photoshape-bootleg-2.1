import javax.swing.*;
import java.awt.*;

public class SectionToolSelectionPanel extends JPanel {

    SectionToolSelectionPanel(PhotoshapeCanvas canvas) {
        setLayout(new BorderLayout());
        JMenuBar section = new SectionToolSelection(canvas);
        add(section, BorderLayout.NORTH);
    }
}
