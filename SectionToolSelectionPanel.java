import javax.swing.*;
import java.awt.*;

public class SectionToolSelectionPanel extends JPanel {
    {
        setLayout(new BorderLayout());
    }

    SectionToolSelectionPanel(PhotoshapeCanvas canvas) {
        JMenuBar section = new SectionToolSelection(canvas);
        add(section, BorderLayout.NORTH);
    }
}
