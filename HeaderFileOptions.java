import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


class HeaderFileOptions extends Menu {

    JFileChooser fileChooser = new JFileChooser();

    /*
    MenuItem fop = new FileOptionOpen();
     */
    PhotoshapeCanvas canvas;

    {
        add(new FileOptionOpen());
        add(new FileOptionSave());
        add(new Undo());
        add(new Redo());
    }
    HeaderFileOptions(String title, PhotoshapeCanvas canvas) {
        super(title);

        this.canvas = canvas;
    }

    class FileOptionSave extends MenuItem implements ActionListener {

        {
            addActionListener(this);
        }

        FileOptionSave() {
            super("Save");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String fileExtension = fileChooser.getDescription(fileChooser.getSelectedFile());
            if(fileExtension != null) {
                String[] fileExtensionBreakdown = fileExtension.split("\\.");
                fileExtension = fileExtensionBreakdown[fileExtensionBreakdown.length - 1];
            } else
                fileExtension = "jpg";
            fileChooser.resetChoosableFileFilters();
            fileChooser.setFileFilter(new FileNameExtensionFilter(fileExtension, fileExtension));
            fileChooser.showSaveDialog(fileChooser);
            try {
                ImageIO.write(canvas.image, fileExtension, fileChooser.getSelectedFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    class FileOptionOpen extends MenuItem implements ActionListener {

        {
            addActionListener(this);
        }

        FileOptionOpen() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            fileChooser.resetChoosableFileFilters();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image File", "png", "jpg", "jpeg"));

            fileChooser.showOpenDialog(fileChooser);
            File selectedFile = fileChooser.getSelectedFile();
            if(selectedFile != null)
                canvas.photoshapeGraphics.loadImage(selectedFile);
        }

    }

    class Undo extends MenuItem implements ActionListener {

        {
            addActionListener(this);
        }

        Undo() {
            super("Undo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            canvas.photoshapeGraphics.undo();
        }
    }

    class Redo extends MenuItem implements ActionListener {

        {
            addActionListener(this);
        }

        Redo() {
            super("Redo");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            canvas.photoshapeGraphics.redo();
        }
    }
}

