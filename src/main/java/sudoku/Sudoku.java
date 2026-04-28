package sudoku;

import java.util.Scanner;

/**
 * Console front-end. Generates a puzzle, then loops on user input until the
 * player either quits or finishes the grid.
 */
public class Sudoku {

    private static final String PROMPT =
            "Enter command (e.g., A3 4, C5 clear, hint, check, quit):";

    private final Scanner scanner;
    private final PuzzleGenerator generator;

    public Sudoku() {
        this(new Scanner(System.in), new PuzzleGenerator());
    }

    public Sudoku(Scanner scanner, PuzzleGenerator generator) {
        this.scanner = scanner;
        this.generator = generator;
    }

    public static void main(String[] args) {
        new Sudoku().run();
    }

    public void run() {
        System.out.println("Welcome to Sudoku!");
        boolean keepPlaying = true;
        while (keepPlaying) {
            keepPlaying = playOne();
        }
    }

    private boolean playOne() {
        Game game = generator.generate();
        System.out.println();
        System.out.println("Here is your puzzle:");
        System.out.print(game.getBoard());

        while (true) {
            System.out.println();
            System.out.println(PROMPT);
            if (!scanner.hasNextLine()) {
                return false;
            }
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println();
                System.out.println("Goodbye!");
                return false;
            }

            String response = handleInput(game, input);
            System.out.println();
            System.out.println(response);

            if (changesBoard(input)) {
                System.out.println();
                System.out.println("Current grid:");
                System.out.print(game.getBoard());
            }

            if (game.isComplete()) {
                System.out.println();
                System.out.println("You have successfully completed the Sudoku puzzle!");
                return askPlayAgain();
            }
        }
    }

    private String handleInput(Game game, String input) {
        if (input.isEmpty()) {
            return "Please enter a command.";
        }
        if (input.equalsIgnoreCase("hint")) {
            return game.hint();
        }
        if (input.equalsIgnoreCase("check")) {
            return game.check();
        }

        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            return "Unrecognised command: " + input;
        }

        int row = parseRow(parts[0]);
        int col = parseColumn(parts[0]);
        if (row < 0 || col < 0) {
            return "Invalid cell reference: " + parts[0];
        }

        if (parts[1].equalsIgnoreCase("clear")) {
            return game.clear(row, col);
        }

        try {
            int value = Integer.parseInt(parts[1]);
            return game.place(row, col, value);
        } catch (NumberFormatException e) {
            return "Expected a number 1-9 or 'clear', got: " + parts[1];
        }
    }

    private boolean changesBoard(String input) {
        String lower = input.toLowerCase();
        return !lower.equals("hint") && !lower.equals("check") && !lower.isEmpty();
    }

    private static int parseRow(String token) {
        if (token.isEmpty()) {
            return -1;
        }
        char c = Character.toUpperCase(token.charAt(0));
        if (c < 'A' || c > 'I') {
            return -1;
        }
        return c - 'A';
    }

    private static int parseColumn(String token) {
        if (token.length() < 2) {
            return -1;
        }
        try {
            int n = Integer.parseInt(token.substring(1));
            if (n < 1 || n > Board.SIZE) {
                return -1;
            }
            return n - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean askPlayAgain() {
        System.out.println("Press Enter to play again, or type 'quit' to exit:");
        if (!scanner.hasNextLine()) {
            return false;
        }
        String input = scanner.nextLine().trim();
        return !input.equalsIgnoreCase("quit");
    }
}
