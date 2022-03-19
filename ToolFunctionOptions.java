import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PaintOptions extends JFrame {
    PaintBrush tool;

    JPanel brushSize;

    {
        setLayout(new BorderLayout());
    }
    PaintOptions(PaintBrush tool) {
        this.tool = tool;
        brushSize = new BrushSize();

        add(brushSize, BorderLayout.NORTH);

        add(new OkButton(), BorderLayout.SOUTH);
        pack();
    }

    class OkButton extends JButton implements ActionListener {

        OkButton() {
            super("ok");

            addActionListener(this);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tool.setBrushSize(((BrushSize) brushSize).brushSizeSlider.getValue());
        }
    }

    class BrushSize extends JPanel {
        JSlider brushSizeSlider = new BrushSizeSlider(0, 255);
        JLabel brushLabel = new JLabel("Brush Size : " + tool.brushSize);
        //might add stuff
        {
            add(brushLabel);
            add(brushSizeSlider);
        }
        class BrushSizeSlider extends JSlider implements ChangeListener {

            BrushSizeSlider(int min, int max) {
                super(min, max, tool.brushSize);
            }

            @Override
            public void stateChanged(ChangeEvent e) {
                brushLabel.setText("Brush Size : " + getValue());
            }
        }
    }
}
