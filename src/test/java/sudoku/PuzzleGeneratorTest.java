package sudoku;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PuzzleGeneratorTest {

    @Test
    void generatedBoardHasExactly30PreFilledCells() {
        Game game = new PuzzleGenerator(new Random(42)).generate();

        int filled = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (game.getBoard().isPreFilled(r, c)) {
                    filled++;
                }
            }
        }

        assertEquals(30, filled);
    }

    @Test
    void generatedBoardHasNoRuleViolations() {
        Game game = new PuzzleGenerator(new Random(42)).generate();

        assertNull(game.getBoard().findFirstViolation());
    }

    @Test
    void hintAlwaysProducesAValueThatMatchesTheSolution() {
        Game game = new PuzzleGenerator(new Random(7)).generate();

        for (int i = 0; i < 20; i++) {
            String response = game.hint();
            assertTrue(response.matches("Hint: Cell [A-I][1-9] = [1-9]"), response);
        }
    }
}
