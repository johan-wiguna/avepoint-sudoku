package sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * One Sudoku session. Owns the playable board and the matching solution
 * (used to power hints), and routes player actions through validation.
 */
public class Game {

    private final Board board;
    private final int[][] solution;
    private final Random random;

    public Game(Board board, int[][] solution) {
        this(board, solution, new Random());
    }

    public Game(Board board, int[][] solution, Random random) {
        this.board = board;
        this.solution = solution;
        this.random = random;
    }

    public Board getBoard() {
        return board;
    }

    public String place(int row, int col, int value) {
        if (value < 1 || value > 9) {
            return "Invalid move. Number must be between 1 and 9.";
        }
        if (board.isPreFilled(row, col)) {
            return "Invalid move. " + label(row, col) + " is pre-filled.";
        }
        board.set(row, col, value);
        return "Move accepted.";
    }

    public String clear(int row, int col) {
        if (board.isPreFilled(row, col)) {
            return "Invalid move. " + label(row, col) + " is pre-filled.";
        }
        board.clear(row, col);
        return "Cell " + label(row, col) + " cleared.";
    }

    public String hint() {
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isEmpty(r, c)) {
                    empties.add(new int[]{r, c});
                }
            }
        }
        if (empties.isEmpty()) {
            return "No empty cells left to hint.";
        }
        int[] cell = empties.get(random.nextInt(empties.size()));
        int value = solution[cell[0]][cell[1]];
        return "Hint: Cell " + label(cell[0], cell[1]) + " = " + value;
    }

    public String check() {
        String violation = board.findFirstViolation();
        return violation == null ? "No rule violations detected." : violation;
    }

    public boolean isComplete() {
        return board.isFull() && board.findFirstViolation() == null;
    }

    private static String label(int row, int col) {
        return "" + (char) ('A' + row) + (col + 1);
    }
}
