package site.paulden.sudoku.view.unit;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class HintPanel extends JPanel {
    private final JSpinner spinner;

    public HintPanel(int initialValue, int minValue, int maxValue) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Hint: ");
        spinner = new JSpinner(new SpinnerNumberModel(initialValue, minValue, maxValue, 1));

        add(label, BorderLayout.WEST);
        add(spinner, BorderLayout.CENTER);
    }

    public int getValue() {
        return (int) spinner.getValue();
    }

    public void setValue(int value) {
        spinner.setValue(value);
    }
}
