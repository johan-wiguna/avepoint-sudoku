package sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void newBoardIsEmpty() {
        Board board = new Board();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                assertTrue(board.isEmpty(r, c));
                assertFalse(board.isPreFilled(r, c));
            }
        }
        assertFalse(board.isFull());
    }

    @Test
    void setAndClearChangeCellValue() {
        Board board = new Board();

        board.set(0, 0, 5);
        assertEquals(5, board.get(0, 0));

        board.clear(0, 0);
        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void lockPreFilledMarksCell() {
        Board board = new Board();

        board.lockPreFilled(4, 4, 9);

        assertEquals(9, board.get(4, 4));
        assertTrue(board.isPreFilled(4, 4));
    }

    @Test
    void emptyBoardHasNoViolations() {
        assertNull(new Board().findFirstViolation());
    }

    @Test
    void detectsRowDuplicate() {
        Board board = new Board();
        board.set(0, 1, 3);
        board.set(0, 2, 3);

        assertEquals("Number 3 already exists in Row A.", board.findFirstViolation());
    }

    @Test
    void detectsColumnDuplicate() {
        Board board = new Board();
        board.set(0, 0, 5);
        board.set(2, 0, 5);

        assertEquals("Number 5 already exists in Column 1.", board.findFirstViolation());
    }

    @Test
    void detectsSubgridDuplicate() {
        Board board = new Board();
        board.set(0, 0, 8);
        board.set(1, 2, 8);

        assertEquals("Number 8 already exists in the same 3×3 subgrid.", board.findFirstViolation());
    }

    @Test
    void toStringRendersHeaderAndAllRows() {
        Board board = new Board();
        board.set(0, 0, 5);

        String[] lines = board.toString().split("\\R");

        assertEquals(10, lines.length);
        assertEquals("    1 2 3 4 5 6 7 8 9", lines[0]);
        assertEquals("  A 5 _ _ _ _ _ _ _ _", lines[1]);
        assertEquals("  I _ _ _ _ _ _ _ _ _", lines[9]);
    }
}
