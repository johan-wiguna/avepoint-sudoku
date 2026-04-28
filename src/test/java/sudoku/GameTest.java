package sudoku;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    private Board board;
    private int[][] solution;
    private Game game;

    @BeforeEach
    void setUp() {
        // Solution row 0 starts 1..9, row 1 starts 4..9,1..3, etc. — a known
        // valid Sudoku solution that gives us predictable values for tests.
        solution = new int[][]{
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},
                {2, 3, 4, 5, 6, 7, 8, 9, 1},
                {5, 6, 7, 8, 9, 1, 2, 3, 4},
                {8, 9, 1, 2, 3, 4, 5, 6, 7},
                {3, 4, 5, 6, 7, 8, 9, 1, 2},
                {6, 7, 8, 9, 1, 2, 3, 4, 5},
                {9, 1, 2, 3, 4, 5, 6, 7, 8}
        };
        board = new Board();
        board.lockPreFilled(0, 0, 1);
        game = new Game(board, solution, new Random(0));
    }

    @Test
    void placeAcceptsValidMoveAndUpdatesBoard() {
        String response = game.place(1, 1, 5);

        assertEquals("Move accepted.", response);
        assertEquals(5, board.get(1, 1));
    }

    @Test
    void placeRejectsValueOutOfRange() {
        assertEquals("Invalid move. Number must be between 1 and 9.", game.place(1, 1, 0));
        assertEquals("Invalid move. Number must be between 1 and 9.", game.place(1, 1, 10));
    }

    @Test
    void placeRejectsPreFilledCell() {
        String response = game.place(0, 0, 7);

        assertEquals("Invalid move. A1 is pre-filled.", response);
        assertEquals(1, board.get(0, 0));
    }

    @Test
    void clearRejectsPreFilledCell() {
        String response = game.clear(0, 0);

        assertEquals("Invalid move. A1 is pre-filled.", response);
        assertEquals(1, board.get(0, 0));
    }

    @Test
    void clearEmptiesPreviouslyPlacedCell() {
        game.place(2, 2, 4);

        String response = game.clear(2, 2);

        assertEquals("Cell C3 cleared.", response);
        assertTrue(board.isEmpty(2, 2));
    }

    @Test
    void hintRevealsAValueFromTheSolution() {
        String response = game.hint();

        // The format is fixed; the cell is random but must come from the solution.
        assertTrue(response.startsWith("Hint: Cell "), response);
    }

    @Test
    void checkReturnsOkWhenNoViolations() {
        assertEquals("No rule violations detected.", game.check());
    }

    @Test
    void checkReturnsViolationMessageWhenDuplicateExists() {
        game.place(0, 1, 1); // duplicate of pre-filled A1

        assertEquals("Number 1 already exists in Row A.", game.check());
    }

    @Test
    void boardWithOnlyOnePreFilledCellIsNotComplete() {
        assertFalse(game.isComplete());
    }

    @Test
    void filledAndValidBoardIsComplete() {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isEmpty(r, c)) {
                    board.set(r, c, solution[r][c]);
                }
            }
        }

        assertTrue(game.isComplete());
    }
}
