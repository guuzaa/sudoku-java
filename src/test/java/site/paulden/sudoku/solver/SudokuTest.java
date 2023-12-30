package site.paulden.sudoku.solver;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import site.paulden.sudoku.exceptions.SudokuException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuTest {
    @Test
    @DisplayName("Given_Sudoku_When_get_empty_board_Then_successfully")
    public void testEmptyBoard() {
        List<Digits> digits = Sudoku.getInstance().emptyBoard();
        assertEquals(81, digits.size());
        for (int i = 0; i < digits.size(); i++) {
            assertTrue("123456789".equals(digits.get(i).toString()));
        }
    }

    @Test
    @DisplayName("Given_Sudoku_String_representation_When_get_solve_them_Then_solve_successfully")
    public void testParseBoard() {
        List<String> puzzles = Arrays.asList(
                "003020600900305001001806400008102900700000008006708200002609500800203009005010300",
                "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......",
                "..53.....8......2..7..1.5..4....53...1..7...6..32...8..6.5....9..4....3......97..",
                ". . . |. . 6 |. . ." +
                        ". 5 9 |. . . |. . 8" +
                        "2 . . |. . 8 |. . ." +
                        "------+------+------" +
                        ". 4 5 |. . . |. . ." +
                        ". . 3 |. . . |. . ." +
                        ". . 6 |. . 3 |. 5 4" +
                        "------+------+------" +
                        ". . . |3 2 5 |. . 6" +
                        ". . . |. . . |. . ." +
                        ". . . |. . . |. . .");
        for (var sudoku : puzzles) {
            try {
                List<Digits> values = Sudoku.getInstance().parseBoard(sudoku, true);
                values = Sudoku.getInstance().solve(values);
                assertTrue(Sudoku.getInstance().isSolved(values));
            } catch (SudokuException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Disabled
    @DisplayName("Given_Impossible_Sudoku_String_representation_When_get_solve_them_Then_failed_to_solve")
    public void testImpossible() {
        String impossible = ". . . |. . 5 |. 8 ." +
                ". . . |6 . 1 |. 4 3" +
                ". . . |. . . |. . ." +
                "------+------+------" +
                ". 1 . |5 . . |. . ." +
                ". . . |1 . 6 |. . ." +
                "3 . . |. . . |. . 5" +
                "------+------+------" +
                "5 3 . |. . . |. 6 1" +
                ". . . |. . . |. . 4" +
                ". . . |. . . |. . .";
        try {
            List<Digits> values = Sudoku.getInstance().parseBoard(impossible, true);
            values = Sudoku.getInstance().solve(values);
            assertFalse(values != null || Sudoku.getInstance().isSolved(values));
        } catch (SudokuException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerate() {
        Random random = new Random();
        for (int run = 0; run < 10; run++) {
            Sudoku sudoku = Sudoku.getInstance();
            int hint = random.nextInt(50) + 30;
            List<Digits> board = sudoku.generate(hint);
            List<List<Digits>> solutions = sudoku.solveAll(board, -1);

            assertEquals(1, solutions.size());
            assertTrue(sudoku.isSolved(solutions.get(0)));
        }
    }
}
