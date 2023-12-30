package site.paulden.sudoku.solver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DigitsTest {
    @Test
    public void testFullDigitsSet() {
        Digits digits = Digits.fullDigitsSet();
        for (int i = 1; i <= 9; i++) {
            assertTrue(digits.isMember(i));
        }
    }

    @Test
    public void testValueOf() {
        for (int i = 1; i <= 9; i++) {
            char ch = (char) ('0' + i);
            Digits dg = Digits.valueOf(ch);
            assertEquals(1, dg.size());
            assertTrue(dg.isMember(i));
        }

        for (var ch : new char[]{'t', ' ', '\n', 'a'}) {
            assertTrue(new Digits().equals(Digits.valueOf(ch)));
        }
    }

    @Test
    public void testSingleDigitSet() {
        int num = 1;
        for (int i = 1; i <= 9; i++) {
            assertEquals(new Digits(num << i), Digits.singleDigitSet(i));
        }
    }

    @Test
    public void testIsMember() {
        int target = 0b0011_1010_1010;
        Digits digits = new Digits(target);
        for (int i = 1; i <= 9; i++) {
            if (i == 2 || i == 4 || i == 6) {
                assertFalse(digits.isMember(i));
            } else {
                assertTrue(digits.isMember(i));
            }
        }
    }

    @Test
    public void testAdd() {
        int target = 0b0000_0000_1010;
        int target1 = 0b0000_1000_0010;
        int input = 0b0000_0000_0010;
        assertEquals(new Digits(input).add(3), new Digits(target));
        assertEquals(new Digits(input).add(7), new Digits(target1));
        Digits d = new Digits();
        for (int i = 1; i <= 9; i++) {
            d.add(i);
        }
        assertEquals(d, Digits.fullDigitsSet());
    }

    @Test
    public void testEquals() {
        Digits d1 = new Digits(0b0011_1111_1110);
        Digits d2 = new Digits(0b0011_1111_1110);
        assertEquals(d1, d2);
        assertTrue(d1.equals(d2));
    }

    @Test
    public void testRemove() {
        int input = 0b0000_0010_1010;
        int target = 0b0000_0000_1010;
        int target1 = 0b0000_0010_0010;
        int target2 = 0b0000_0010_1000;
        assertEquals(new Digits(input).remove(5), new Digits(target));
        assertEquals(new Digits(input).remove(3), new Digits(target1));
        assertEquals(new Digits(input).remove(1), new Digits(target2));
    }

    @Test
    public void testRemoveAll() {
        int input = 0b0000_0010_1010;
        int toRemove = 0b0000_0010_1010;
        Digits digits = new Digits(input);
        digits = digits.removeAll(toRemove);
        assertEquals(0, digits.size());
    }

    @Test
    public void testSize() {
        int input = 0b0000_0010_1010;
        assertEquals(3, new Digits(input).size());
        input = 0b0000_0011_1010;
        assertEquals(4, new Digits(input).size());
        input = 0b0000_0000_0000;
        assertEquals(0, new Digits(input).size());
    }

    @Test
    public void testSingleMemberDigit() {
        Digits d = new Digits(0);
        for (int dig = 1; dig <= 9; dig++) {
            d = d.add(dig);
            assertEquals(1, d.size());
            int off = d.singleMemberDigit();
            assertEquals(off, dig);
            d = d.remove(dig);
            assertEquals(0, d.size());
        }

    }

    @Test
    public void testToString() {
        int input = 0b0000_0010_1010;
        assertEquals("135", new Digits(input).toString());
        input = 0b0000_0011_1010;
        assertEquals("1345", new Digits(input).toString());
        input = 0b0000_0000_0000;
        assertEquals("", new Digits(input).toString());
    }
}
