import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class OptionsWindow extends JFrame {
    //I felt like using classes but it felt more right to use a lambda
    Runnable okAction;

    JButton okButton = new JButton("ok");
    DisplayableOptions displayableOptions = new DisplayableOptions();

    OptionsWindow() {
        setLayout(new BorderLayout());

        add(displayableOptions, BorderLayout.NORTH);

        add(okButton, BorderLayout.SOUTH);

        okButton.addActionListener(new ChangeOptions());

        pack();
    }

    class ChangeOptions implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            okAction.run();
            setVisible(false);
        }
    }

    class DisplayableOptions extends JPanel {
        public OptionContent[] CurrentOptions;

        DisplayableOptions() {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        }

        public void switchOptions(OptionContent[] newOptions, Runnable newOkAction) {
            CurrentOptions = newOptions;
            okAction = newOkAction;
            update();
        }

        public void update() {
            removeAll();

            for(OptionContent option : CurrentOptions)
                this.add(option.optionPanel);
            pack();
        }
    }
}



class OptionContent {
    JLabel optionName;
    Component optionValue;
    String name;
    JPanel optionPanel = new JPanel();

    OptionContent(String name, Component optionValue) {
        this.name = name;
        this.optionValue = optionValue;

        optionName = new JLabel(name);

        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
        //might add counter
        optionPanel.add(optionName);
        optionPanel.add(optionValue);
    }

    OptionContent(String name, JSlider optionValue) {
        this.name = name;
        this.optionValue = optionValue;

        //I got lazy to make a new constructor that has one more variable for an initial value to display
        optionName = new JLabel(name + optionValue.getValue());

        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
        //might add counter
        optionPanel.add(optionName);
        optionPanel.add(optionValue);
    }

    //ignor this being in this class

    abstract public static class SliderOption extends JSlider implements ChangeListener {
        SliderOption(int min, int max, int set) {
            super(min, max, set);
            addChangeListener(this);
        }

        abstract public void stateChanged(ChangeEvent e);

    }
}