package site.paulden.sudoku.solver;

import java.util.Objects;
import java.util.StringJoiner;

public class Digits {
    private int digit;
    private static Digits fullDigits;

    public Digits() {
        this.digit = 0;
    }

    public Digits(int digit) {
        this.digit = digit;
    }

    public static Digits fullDigitsSet() {
        if (fullDigits == null) {
            int full = 0b0011_1111_1110;
            fullDigits = new Digits(full);
        }
        return fullDigits;
    }

    public static Digits valueOf(String str) {
        if (str.length() != 1) {
            return new Digits();
        }
        return valueOf(str.charAt(0));
    }

    public static Digits valueOf(char ch) {
        if (!Character.isDigit(ch)) {
            return new Digits();
        }

        int num = Character.getNumericValue(ch);
        return new Digits(0).add(num);
    }

    public static Digits singleDigitSet(int n) {
        return new Digits(1 << n);
    }

    public boolean isMember(int n) {
        return (digit & (1 << n)) != 0;
    }

    public Digits add(int n) {
        digit |= (1 << n);
        return this;
    }

    public Digits remove(int n) {
        return new Digits(digit & ~(1 << n));
    }

    public Digits removeAll(int n) {
        return new Digits(digit & ~n);
    }

    public int size() {
        return Integer.bitCount(digit);
    }

    public int singleMemberDigit() {
        return Integer.numberOfTrailingZeros(digit);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("");
        for (int i = 1; i <= 9; i++) {
            if (isMember(i)) {
                joiner.add(String.valueOf(i));
            }
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Digits digits = (Digits) o;
        return digit == digits.digit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digit);
    }
}
