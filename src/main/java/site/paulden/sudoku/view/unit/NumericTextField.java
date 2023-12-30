package site.paulden.sudoku.view.unit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumericTextField extends JTextField {
    public NumericTextField() {
        setDocument(new NumericDocument());
    }

    private static class NumericDocument extends PlainDocument {
        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }

            String newStr = getText(0, offset) + str + getText(offset, getLength() - offset);
            if (isValidNumericInput(newStr)) {
                super.insertString(offset, str, attr);
            }
        }

        private boolean isValidNumericInput(String text) {
            return text.matches("[1-9]"); // only accept 1-9
        }
    }
}
