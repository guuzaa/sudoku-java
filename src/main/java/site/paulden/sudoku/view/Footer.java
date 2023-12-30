package site.paulden.sudoku.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Footer extends JPanel {
    public Footer(double difficulty, JButton submitButton) {
        setLayout(new GridLayout(1, 2));

        TextField difficultyField = new TextField();
        difficultyField.setText("Difficulty: " + difficulty);
        difficultyField.setEditable(false);
        add(difficultyField);

        add(submitButton);
    }
}
