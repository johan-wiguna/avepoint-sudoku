# Sudoku

A console Sudoku game in Java. Generates a puzzle, takes moves at the prompt,
supports `hint`, `check`, `clear`, and `quit`.

## Requirements

- JDK 17 or newer (project is set up with JDK 25).
- Works on Windows, macOS, and Linux.

## Run it

### From IntelliJ

Open the folder, then run `main` in `src/main/java/sudoku/Sudoku.java`.

### From the command line

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out sudoku.Sudoku
```

On Windows PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp out sudoku.Sudoku
```

## How to play

Rows are labeled `A–I`, columns `1–9`. At the prompt:

- `A3 4` — put `4` in cell A3
- `C5 clear` — clear cell C5 (pre-filled cells can't be cleared)
- `hint` — reveal the answer for one random empty cell
- `check` — report the first rule violation on the board, if any
- `quit` — exit

## Design notes

Four classes, one job each:

- `Board` — the 9×9 grid, which cells are pre-filled, and rule checks.
- `Game` — one session. Holds the board and the solution, validates moves,
  produces messages.
- `PuzzleGenerator` — builds a new game.
- `Sudoku` — the input/output loop.

Keeping I/O out of `Game` and `Board` makes them easy to unit test. `Game`
and `PuzzleGenerator` accept a `Random`, and `Sudoku` accepts a `Scanner`,
so tests can pin behavior.

### How puzzles are generated

1. Fill the three diagonal 3×3 boxes with random digits — they don't share
   rows or columns, so any arrangement is legal.
2. Backtrack to fill the rest, producing a full valid solution.
3. Pick 30 random cells from the solution and lock them on a fresh board.
   The rest stay empty.

### Assumptions

- Coordinates are `<row letter><column number>`, case-insensitive.
- Difficulty is fixed: 30 pre-filled cells.
- `place` lets you enter a value that breaks a rule. The `check` command is
  what tells you about violations — that way you can experiment without the
  game blocking every move.
- A puzzle is won when the board is full *and* has no violations.

### Known limitation

Generated puzzles aren't guaranteed to have a unique solution. Cells are
removed randomly from the full solution, so another valid completion may
exist. `hint` always points to the original solution.

## Tests

Run from IntelliJ: right-click `src/test/java/sudoku` → *Run Tests*. Uses
JUnit 5.
