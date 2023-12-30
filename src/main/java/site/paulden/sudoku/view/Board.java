package site.paulden.sudoku.view;

import site.paulden.sudoku.solver.Digits;
import site.paulden.sudoku.solver.Sudoku;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Board extends JFrame {
    private final Sudoku sudoku = Sudoku.getInstance();
    public Board(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout(30, 10));

        int defaultHint = 80;
        JButton generateButton = new JButton("Generate");
        Header headPanel = new Header(defaultHint, generateButton);
        add(headPanel, BorderLayout.NORTH);
        add(new JSeparator());

        Body bodyPanel = new Body(defaultHint);
        add(bodyPanel, BorderLayout.CENTER);
        generateButton.addActionListener(e -> bodyPanel.refresh(headPanel.getHintValue()));

        JButton submitButton = new JButton("Submit");
        JPanel bottom = new Footer(3.0, submitButton);
        add(bottom, BorderLayout.SOUTH);
        submitButton.addActionListener(e -> {
            List<Digits> digits = bodyPanel.getFields();
            if (sudoku.isSolved(digits)) {
                JOptionPane.showMessageDialog(this, "success");
            } else {
                JOptionPane.showMessageDialog(this, "failed, try again");
            }
        });

        pack();
        setVisible(true);
    }
}
