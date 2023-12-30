package site.paulden.sudoku.view;

import site.paulden.sudoku.solver.Digits;
import site.paulden.sudoku.solver.Sudoku;
import site.paulden.sudoku.view.unit.NumericTextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Body extends JPanel {
    private final List<JTextField> fields;
    private final Sudoku sudoku = Sudoku.getInstance();

    public Body(int hint) {
        fields = new LinkedList<>();
        setLayout(new GridLayout(9, 9, 1, 1));
        displayAsBoard(hint);
    }

    private void displayAsBoard(int hint) {
        List<Digits> board = sudoku.generate(hint);
        for (Digits d : board) {
            JTextField textField = new NumericTextField();
            textField.setPreferredSize(new Dimension(50, 50));
            if (d.size() == 1) {
                textField.setText(d.toString());
                textField.setEditable(false);
            } else {
                textField.setText("");
                textField.setEditable(true);
            }
            add(textField);
            fields.add(textField);
        }
    }

    public List<Digits> getFields() {
        ArrayList<Digits> digits = new ArrayList<>(81);
        for (JTextField field : fields) {
            digits.add(Digits.valueOf(field.getText()));
        }
        return digits;
    }

    public void refresh(int hint) {
        List<Digits> board = sudoku.generate(hint);
        for (int i = 0; i < fields.size(); i++) {
            JTextField textField = fields.get(i);
            Digits d = board.get(i);
            if (d.size() == 1) {
                textField.setText(d.toString());
                textField.setEditable(false);
            } else {
                textField.setText("");
                textField.setEditable(true);
            }
        }
    }
}
