package site.paulden.sudoku.solver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import site.paulden.sudoku.exceptions.SudokuException;

import java.util.*;

public class Sudoku {
    private static final Sudoku instance = new Sudoku();
    private static final Logger logger = LogManager.getLogger(DefaultConfiguration.class);
    private final List<List<Integer>> unitList = new ArrayList<>();
    private final List<List<Integer>> peers;
    private final List<List<List<Integer>>> units;

    private Sudoku() {
        for (int row = 0; row < 9; row++) {
            ArrayList<Integer> rowUnit = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
                rowUnit.add(getIndex(row, col));
            }
            unitList.add(rowUnit);
        }

        for (int col = 0; col < 9; col++) {
            ArrayList<Integer> colUnit = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
                colUnit.add(getIndex(row, col));
            }
            unitList.add(colUnit);
        }

        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockCol = 0; blockCol < 3; blockCol++) {
                ArrayList<Integer> blockUnit = new ArrayList<>();
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        blockUnit.add(getIndex(blockRow * 3 + row, blockCol * 3 + col));
                    }
                }
                unitList.add(blockUnit);
            }
        }

        units = new ArrayList<>(81);
        for (int i = 0; i < 81; i++) {
            ArrayList<List<Integer>> ul = new ArrayList<>();
            for (var unit : unitList) {
                if (unit.contains(i)) {
                    ul.add(new ArrayList<>(unit));
                }
            }
            units.add(ul);
        }

        peers = new ArrayList<>(81);
        for (int i = 0; i < 81; i++) {
            ArrayList<Integer> peer = new ArrayList<>();
            for (var unit : units.get(i)) {
                for (var candidate : unit) {
                    if (candidate != i && !peer.contains(candidate)) {
                        peer.add(candidate);
                    }
                }
            }
            peers.add(peer);
        }
    }

    private int getIndex(int row, int col) {
        return row * 9 + col;
    }

    public static Sudoku getInstance() {
        return instance;
    }

    public List<Digits> generate(int hintCount) {
        List<Digits> board = emptyBoard();
        board = solve(board);
        if (board == null) {
            logger.fatal("unable to generate solved board from empty");
        }

        int count = 81;
        ArrayList<Integer> removalOrder = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            removalOrder.add(i);
        }
        Collections.shuffle(removalOrder);

        for (int sq : removalOrder) {
            Digits savedDigit = board.get(sq);
            board.set(sq, Digits.fullDigitsSet());
            List<List<Digits>> solutions = solveAll(board, 2);
            switch (solutions.size()) {
                case 0 -> {
                    logger.fatal("got a board without solutions");
                }
                case 1 -> {
                    count--;
                    if (count <= hintCount) {
                        return board;
                    }
                }
                default -> {
                    board.set(sq, savedDigit);
                }
            }
        }
        return board;
    }

    public String toString(List<Digits> values) {
        int maxLen = values.stream().mapToInt(Digits::size).filter(d -> d >= 0).max().orElse(0);

        int width = maxLen + 1;
        String line = String.join("+", new String[]{"-".repeat(width * 3), "-".repeat(width * 3), "-".repeat(width * 3)});

        StringJoiner joiner = new StringJoiner("");
        for (int i = 0; i < values.size(); i++) {
            Digits d = values.get(i);

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb);
            formatter.format("%" + -width + "s", String.format("%" + (width + d.size()) / 2 + "s", d));
            joiner.add(formatter.toString());

            if (i % 9 == 2 || i % 9 == 5) {
                joiner.add("|");
            }

            if (i % 9 == 8) {
                joiner.add("\n");
            }

            if (i == 26 || i == 53) {
                joiner.add(line + "\n");
            }
        }
        return joiner.toString();
    }

    public List<Digits> parseBoard(String str, boolean runElimination) throws SudokuException {
        ArrayList<Integer> dgs = new ArrayList<>();
        for (var r : str.toCharArray()) {
            if (Character.isDigit(r)) {
                dgs.add(r - '0');
            } else if (r == '.') {
                dgs.add(0);
            }
        }
        if (dgs.size() != 81) {
            throw new SudokuException(String.format("got only %d digits in board, want 81", dgs.size()));
        }

        // Start with an empty board
        List<Digits> values = emptyBoard();

        // Assign square digits based on the parsed board.
        // Note that this runs constraint propagation and may discover contradictions.
        for (int sq = 0; sq < dgs.size(); sq++) {
            Integer d = dgs.get(sq);
            if (d != 0) {
                values.set(sq, Digits.singleDigitSet(d));
            }
        }

        if (runElimination && !eliminateAll(values)) {
            throw new SudokuException("contradictions when eliminating board");
        }
        return values;
    }

    /**
     * eliminateAll runs elimination on all assigned squares in values.
     * It applies first-order Sudoku heuristics on the entire board.
     * Returns true if the elimination is successful, and false if the board has a contradiction.
     *
     * @param values
     * @return
     */
    private boolean eliminateAll(List<Digits> values) {
        for (int sq = 0; sq < values.size(); sq++) {
            Digits d = values.get(sq);
            if (d.size() == 1) {
                // Because of how eliminate() works, we prepare for it by remembering which digit this square
                // has assigned, setting the square to the full set of digits and then calling eliminate on all
                // digits except the assigned one.
                int digit = d.singleMemberDigit();
                values.set(sq, Digits.fullDigitsSet());
                for (int dn = 1; dn <= 9; dn++) {
                    if (dn != digit) {
                        if (!eliminate(values, sq, dn)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * eliminate removes digit from the candidates in values[square], propagating constraints.
     * values is modified.
     * It returns false if this results in an invalid Sudoku board;
     * otherwise returns true.
     *
     * @param values
     * @param square
     * @param digit
     * @return
     */
    private boolean eliminate(List<Digits> values, int square, int digit) {
        if (!values.get(square).isMember(digit)) {
            return true;
        }
        // Remove digit from the candidates in square.
        values.set(square, values.get(square).remove(digit));

        switch (values.get(square).size()) {
            case 0 -> {
                return false;
            }

            // A single digit candidate remaining in the square -- this creates a new constraint.
            // Eliminate this digit from all peer squares.
            case 1 -> {
                int remaining = values.get(square).singleMemberDigit();
                for (var peer : peers.get(square)) {
                    if (!eliminate(values, peer, remaining)) {
                        return false;
                    }
                }
            }
        }

        unitLoop:
        for (var unit : units.get(square)) {
            int sqd = -1;
            for (var sq : unit) {
                if (values.get(sq).isMember(digit)) {
                    if (sqd == -1) {
                        sqd = sq;
                    } else {
                        continue unitLoop;
                    }
                }
            }
            if (sqd == -1) {
                return false;
            }

            if (!assign(values, sqd, digit)) {
                return false;
            }
        }
        return true;
    }

    /**
     * assign attempts to assign digit to values[square], propagating
     * constraints from the assignment.
     * values is modified.
     * It returns true if the assignment succeeded, and false if the assignment fails resulting in an invalid
     * Sudoku board.
     *
     * @param values
     * @param square
     * @param digit
     * @return
     */
    private boolean assign(List<Digits> values, int square, int digit) {
        for (int d = 1; d <= 9; d++) {
            if (values.get(square).isMember(d) && d != digit) {
                if (!eliminate(values, square, d)) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<Digits> emptyBoard() {
        ArrayList<Digits> vals = new ArrayList<>(81);
        for (int i = 0; i < 81; i++) {
            vals.add(Digits.fullDigitsSet());
        }
        return vals;
    }

    public boolean isSolved(List<Digits> values) {
        if (values == null) {
            return false;
        }

        for (List<Integer> unit : unitList) {
            Digits dset = new Digits();
            for (Integer sq : unit) {
                Digits d = values.get(sq);
                if (d.size() != 1) {
                    return false;
                }
                dset.add(d.singleMemberDigit());
            }

            if (!dset.equals(Digits.fullDigitsSet())) {
                return false;
            }
        }

        return true;
    }

    private int findSquareWithFewestCandidates(List<Digits> values) {
        int squareToTry = -1;
        int minSize = 10;

        for (int sq = 0; sq < values.size(); sq++) {
            Digits d = values.get(sq);
            if (d.size() > 1 && d.size() < minSize) {
                minSize = d.size();
                squareToTry = sq;
            }
        }

        return squareToTry;
    }

    public List<Digits> solve(List<Digits> values) {
        int squareToTry = findSquareWithFewestCandidates(values);
        if (squareToTry == -1) {
            return values;
        }

        List<Integer> candidates = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(candidates);
        for (var d : candidates) {
            if (values.get(squareToTry).isMember(d)) {
                ArrayList<Digits> vCopy = new ArrayList<>(values);

                if (assign(vCopy, squareToTry, d)) {
                    List<Digits> ret = solve(vCopy);
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    public List<List<Digits>> solveAll(List<Digits> values, int max) {
        int squareToTry = findSquareWithFewestCandidates(values);
        if (squareToTry == -1) {
            ArrayList<List<Digits>> lists = new ArrayList<>();
            lists.add(values);
            return lists;
        }

        ArrayList<List<Digits>> allSolved = new ArrayList<>();
        for (int d = 1; d <= 9; d++) {
            if (values.get(squareToTry).isMember(d)) {
                List<Digits> vCopy = new ArrayList<>(values);
                if (assign(vCopy, squareToTry, d)) {
                    List<List<Digits>> vSolved = solveAll(vCopy, max);
                    if (vSolved.isEmpty()) {
                        continue;
                    }

                    allSolved.addAll(vSolved);
                    if (max > 0 && allSolved.size() >= max) {
                        return allSolved;
                    }
                }

            }
        }
        return allSolved;
    }

}
