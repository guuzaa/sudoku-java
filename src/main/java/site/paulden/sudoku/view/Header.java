package site.paulden.sudoku.view;

import site.paulden.sudoku.view.unit.HintPanel;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel {
    private final HintPanel left;

    public Header(int hint, JButton generateButton) {
        setLayout(new GridLayout(1, 2));

        left = new HintPanel(hint, 30, 81);
        add(left);

        add(generateButton);
    }

    public void setHintValue(int value) {
        left.setValue(value);
    }

    public int getHintValue() {
        return left.getValue();
    }
}
