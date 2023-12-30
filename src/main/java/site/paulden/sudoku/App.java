package site.paulden.sudoku;

import site.paulden.sudoku.view.Board;

public class App {
    public App() {
        new Board("Sudoku Puzzle");
    }

    public static void main(String[] args) {
        new App();
    }
}
