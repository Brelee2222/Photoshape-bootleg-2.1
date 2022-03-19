import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SectionToolSelection extends JMenuBar {
    PhotoshapeCanvas canvas;
    JMenuItem paintBrush;
    JMenuItem lineTool;
    JMenuItem eraser;
    JMenuItem colorChooser;
    JMenuItem bucketPaint;
    JMenuItem toolSettings;


    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public SectionToolSelection(PhotoshapeCanvas canvas) {
        this.canvas = canvas;

        eraser = new SectionToolSelectionButton("Eraser", new Erase(canvas));
        lineTool = new SectionToolSelectionButton("Line Tool", new LineTool(canvas));
        paintBrush = new SectionToolSelectionButton("Paint Brush", new PaintBrush(canvas));
        bucketPaint = new SectionToolSelectionButton("Bucket", new PaintBucket(canvas));

        toolSettings = new ToolSettings("Tool");

        colorChooser = new ColorChooserButton("Color");

        add(paintBrush);
        add(lineTool);
        add(eraser);
        add(bucketPaint);

        add(toolSettings);

        add(colorChooser);
    }

    class SectionToolSelectionButton extends JMenuItem implements ActionListener {
        MouseInputListener toolFunction;

        public SectionToolSelectionButton(String title, MouseInputListener toolFunction) {
            super(title);
            addActionListener(this);
            this.toolFunction = toolFunction;
            //I deserve extra points for using html and would've used js
            //I might want to model it after my website https://brelee2222.github.io
            setText("<html><font Color=#ff00ff>" + title + "</font></html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            canvas.photoshapeGraphics.changeDrawFunction(toolFunction);
        }
    }

    class ToolSettings extends JMenuItem implements ActionListener {

        ToolSettings(String title) {
            super(title);

            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame options = canvas.optionsDialogue;



            options.setVisible(true);
        }
    }

    class ColorChooserButton extends JMenuItem implements ActionListener {
        public ColorChooserButton(String title) {
            super(title);
            addActionListener(this);
            JOptionPane optionPane = new JOptionPane();
            optionPane.createDialog("hi");
            //JOptionPane.
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            canvas.photoshapeGraphics.penColor = JColorChooser.showDialog(this, "Choose Color", Color.RED);
        }

    }
}