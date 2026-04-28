package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Builds a fresh playable {@link Game}. The strategy is:
 * <ol>
 *   <li>Fill the three diagonal 3x3 boxes with random digits — they share
 *       no rows or columns, so any arrangement is legal.</li>
 *   <li>Complete the grid with a backtracking solver to obtain a full
 *       solution.</li>
 *   <li>Pick {@code preFilled} random cells from the solution and lock
 *       them on a fresh board; the rest stay empty for the player.</li>
 * </ol>
 */
public class PuzzleGenerator {

    private static final int DEFAULT_PRE_FILLED = 30;

    private final Random random;
    private final int preFilled;

    public PuzzleGenerator() {
        this(new Random(), DEFAULT_PRE_FILLED);
    }

    public PuzzleGenerator(Random random) {
        this(random, DEFAULT_PRE_FILLED);
    }

    public PuzzleGenerator(Random random, int preFilled) {
        if (preFilled < 0 || preFilled > Board.SIZE * Board.SIZE) {
            throw new IllegalArgumentException(
                    "preFilled must be between 0 and " + (Board.SIZE * Board.SIZE));
        }
        this.random = random;
        this.preFilled = preFilled;
    }

    public Game generate() {
        int[][] solution = generateSolution();
        Board board = buildPlayableBoard(solution);
        return new Game(board, solution, random);
    }

    private int[][] generateSolution() {
        int[][] grid = new int[Board.SIZE][Board.SIZE];
        for (int i = 0; i < Board.SIZE; i += Board.BOX_SIZE) {
            fillBoxWithRandomDigits(grid, i, i);
        }
        solve(grid, 0, 0);
        return grid;
    }

    private void fillBoxWithRandomDigits(int[][] grid, int startRow, int startCol) {
        List<Integer> digits = new ArrayList<>();
        for (int i = 1; i <= Board.SIZE; i++) {
            digits.add(i);
        }
        Collections.shuffle(digits, random);
        int idx = 0;
        for (int r = 0; r < Board.BOX_SIZE; r++) {
            for (int c = 0; c < Board.BOX_SIZE; c++) {
                grid[startRow + r][startCol + c] = digits.get(idx++);
            }
        }
    }

    private boolean solve(int[][] grid, int row, int col) {
        if (row == Board.SIZE) {
            return true;
        }
        int nextRow = (col == Board.SIZE - 1) ? row + 1 : row;
        int nextCol = (col == Board.SIZE - 1) ? 0 : col + 1;
        if (grid[row][col] != 0) {
            return solve(grid, nextRow, nextCol);
        }
        for (int num = 1; num <= Board.SIZE; num++) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                if (solve(grid, nextRow, nextCol)) {
                    return true;
                }
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < Board.SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        int boxRow = (row / Board.BOX_SIZE) * Board.BOX_SIZE;
        int boxCol = (col / Board.BOX_SIZE) * Board.BOX_SIZE;
        for (int r = boxRow; r < boxRow + Board.BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + Board.BOX_SIZE; c++) {
                if (grid[r][c] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private Board buildPlayableBoard(int[][] solution) {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                positions.add(new int[]{r, c});
            }
        }
        Collections.shuffle(positions, random);

        Board board = new Board();
        for (int i = 0; i < preFilled; i++) {
            int[] pos = positions.get(i);
            board.lockPreFilled(pos[0], pos[1], solution[pos[0]][pos[1]]);
        }
        return board;
    }
}
