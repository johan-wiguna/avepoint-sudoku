package sudoku;

/**
 * The 9x9 Sudoku grid. Stores cell values and which cells are pre-filled
 * (locked from player edits). Knows how to display itself and how to find
 * the first row/column/sub-grid violation.
 */
public class Board {

    public static final int SIZE = 9;
    public static final int BOX_SIZE = 3;

    private final int[][] values = new int[SIZE][SIZE];
    private final boolean[][] preFilled = new boolean[SIZE][SIZE];

    public int get(int row, int col) {
        return values[row][col];
    }

    public void set(int row, int col, int value) {
        values[row][col] = value;
    }

    public void clear(int row, int col) {
        values[row][col] = 0;
    }

    public boolean isEmpty(int row, int col) {
        return values[row][col] == 0;
    }

    public boolean isPreFilled(int row, int col) {
        return preFilled[row][col];
    }

    public void lockPreFilled(int row, int col, int value) {
        values[row][col] = value;
        preFilled[row][col] = true;
    }

    public boolean isFull() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (values[r][c] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the first rule violation found, in row -> column -> sub-grid
     * order, or {@code null} if the board has none.
     */
    public String findFirstViolation() {
        for (int row = 0; row < SIZE; row++) {
            int duplicate = findDuplicateInRow(row);
            if (duplicate != 0) {
                return "Number " + duplicate + " already exists in Row " + (char) ('A' + row) + ".";
            }
        }
        for (int col = 0; col < SIZE; col++) {
            int duplicate = findDuplicateInColumn(col);
            if (duplicate != 0) {
                return "Number " + duplicate + " already exists in Column " + (col + 1) + ".";
            }
        }
        for (int br = 0; br < SIZE; br += BOX_SIZE) {
            for (int bc = 0; bc < SIZE; bc += BOX_SIZE) {
                int duplicate = findDuplicateInBox(br, bc);
                if (duplicate != 0) {
                    return "Number " + duplicate + " already exists in the same 3×3 subgrid.";
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int c = 1; c <= SIZE; c++) {
            sb.append(c);
            if (c < SIZE) {
                sb.append(' ');
            }
        }
        sb.append(System.lineSeparator());
        for (int r = 0; r < SIZE; r++) {
            sb.append("  ").append((char) ('A' + r)).append(' ');
            for (int c = 0; c < SIZE; c++) {
                sb.append(values[r][c] == 0 ? "_" : String.valueOf(values[r][c]));
                if (c < SIZE - 1) {
                    sb.append(' ');
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private int findDuplicateInRow(int row) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int c = 0; c < SIZE; c++) {
            int v = values[row][c];
            if (v == 0) {
                continue;
            }
            if (seen[v]) {
                return v;
            }
            seen[v] = true;
        }
        return 0;
    }

    private int findDuplicateInColumn(int col) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int r = 0; r < SIZE; r++) {
            int v = values[r][col];
            if (v == 0) {
                continue;
            }
            if (seen[v]) {
                return v;
            }
            seen[v] = true;
        }
        return 0;
    }

    private int findDuplicateInBox(int startRow, int startCol) {
        boolean[] seen = new boolean[SIZE + 1];
        for (int r = startRow; r < startRow + BOX_SIZE; r++) {
            for (int c = startCol; c < startCol + BOX_SIZE; c++) {
                int v = values[r][c];
                if (v == 0) {
                    continue;
                }
                if (seen[v]) {
                    return v;
                }
                seen[v] = true;
            }
        }
        return 0;
    }
}
