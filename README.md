# Sudoku

A console-based 9×9 Sudoku game written in Java. Generates a fresh puzzle each
round, accepts moves at the prompt, and supports `hint`, `check`, `clear`, and
`quit`.

## Environment

- **Operating system:** Windows, macOS, or Linux. The application is plain
  Java with no native dependencies, so any OS with a JDK works.
- **JDK:** 25 (project SDK as configured in IntelliJ). Java 17 or newer
  should also compile and run the source as written.
- **Tests:** JUnit 5 (Jupiter). Easiest path is to run them from IntelliJ,
  which bundles JUnit.
- **No build tool.** The project has no `pom.xml` or `build.gradle` — it is
  configured as a plain IntelliJ module under `.idea/`.

## Project layout

```
src/
  main/java/sudoku/
    Sudoku.java           # Console front-end: input loop, command parsing
    Game.java             # One playing session: place / clear / hint / check
    Board.java            # 9x9 grid state, pre-filled cells, rule violations
    PuzzleGenerator.java  # Generates a full solution and a playable board
  test/java/sudoku/
    GameTest.java
    BoardTest.java
    PuzzleGeneratorTest.java
```

## How to run

### Option A — IntelliJ IDEA (recommended)

1. Open the project folder in IntelliJ.
2. If `Sudoku.java` is not detected as runnable, mark `src/main/java` as
   *Sources Root* and `src/test/java` as *Test Sources Root* (right-click →
   *Mark Directory as*). The included `.idea/avepoint-sudoku.iml` already
   declares both.
3. Open `src/main/java/sudoku/Sudoku.java` and click the green run arrow next
   to `public static void main`.

### Option B — Command line

From the project root:

```bash
# Compile
javac -d out $(find src/main/java -name "*.java")

# Run
java -cp out sudoku.Sudoku
```

On Windows PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp out sudoku.Sudoku
```

## How to play

When the game starts, a partially filled grid is printed with rows labeled
`A–I` and columns labeled `1–9`:

```
    1 2 3 4 5 6 7 8 9
  A 5 _ _ _ 7 _ _ _ _
  B _ 6 _ 1 9 5 _ _ _
  ...
```

At the prompt, enter one of:

| Command       | Effect                                              |
|---------------|-----------------------------------------------------|
| `A3 4`        | Place the number `4` in cell A3                     |
| `C5 clear`    | Clear cell C5 (only if you placed it; pre-filled cells are locked) |
| `hint`        | Reveal the correct value for one random empty cell  |
| `check`       | Report the first row/column/sub-grid rule violation, or confirm none |
| `quit` / `exit` | Leave the game                                    |

After the puzzle is solved, you are offered a new round.

## Design and assumptions

### Separation of concerns

The code is split into four classes so that each has one reason to change:

- **`Board`** — pure state plus rule lookups. It knows the grid size, which
  cells are locked (pre-filled by the generator), and how to find the first
  duplicate in any row, column, or 3×3 sub-grid. It has no notion of a
  "player" or a "session."
- **`Game`** — one playing session. Owns a `Board` *and* the matching
  solution. Routes player actions (`place`, `clear`, `hint`, `check`) through
  validation and produces user-facing messages. Hints are powered by the
  stored solution rather than by re-solving on demand.
- **`PuzzleGenerator`** — builds a fresh `Game`. It does not mutate any
  existing state; it produces a new solution and a new playable board on
  each call.
- **`Sudoku`** — the I/O layer. Parses input strings, prints the board,
  and decides when to ask whether to play again. It is the only class that
  touches `System.in`/`System.out`, which keeps `Game` and `Board` trivially
  unit-testable.

### Puzzle generation strategy

`PuzzleGenerator.generate()` works in three steps:

1. Fill the three diagonal 3×3 boxes with random digits 1–9. Because the
   diagonal boxes share no rows or columns with each other, any random
   arrangement is automatically legal — this seeds randomness cheaply.
2. Run a standard backtracking solver across the rest of the grid to
   obtain a complete, valid solution.
3. Pick `preFilled` random cells (default **30**) and lock those values on
   a fresh empty board. Everything else is left blank for the player.

This guarantees every generated puzzle has *at least one* valid solution
(the one we built it from). It does **not** guarantee a *unique* solution;
see "Known limitations" below.

### Hints

Hints are intentionally simple: pick a random empty cell and reveal what
that cell must be in the stored solution. They are not "the only legal
value here" hints — they are "here is one cell answer" hints. This is
deterministic in tests via the injectable `Random`.

### Validation

`Game.place` enforces:

- The number must be in the range 1–9.
- The cell must not be pre-filled (locked).

It deliberately allows the player to enter a value that violates Sudoku
rules. Surfacing rule violations is the job of the `check` command, which
calls `Board.findFirstViolation()`. This separation lets a player explore
moves without the program second-guessing every entry.

### Completion

A puzzle is "complete" when the board is full *and* has no rule
violations. A full board with a duplicate is treated as in-progress, not
won.

### Testability

- `Game` and `PuzzleGenerator` accept an injectable `Random`, so tests can
  pin RNG behavior and assert exact hint cells / generated layouts.
- `Sudoku` accepts an injectable `Scanner`, so the input loop can be
  exercised without real stdin.
- `Board.toString()` produces the exact rendering shown to the player,
  which means snapshot-style assertions stay aligned with what users see.

### Assumptions

- Coordinates are entered as `<row letter><column number>`, e.g. `A3`. The
  letter is case-insensitive.
- The grid is always 9×9 with 3×3 sub-grids; the constants live on
  `Board` (`SIZE`, `BOX_SIZE`) but the algorithms assume the standard
  Sudoku shape.
- "Difficulty" is represented only by the number of pre-filled cells
  (default 30). There is no easy/medium/hard selector.
- A single Sudoku solution is generated per puzzle and used to power
  hints; the player's board is independent state.

### Known limitations

- **Uniqueness is not enforced.** Because pre-filled cells are picked at
  random from the solution, a generated puzzle may technically admit more
  than one valid completion. The "correct" answer used by `hint` is
  always the originally generated solution, but `check` only flags rule
  violations, so an alternative legal completion will be accepted as a
  win. Adding a uniqueness pass (solve and require exactly one solution
  while removing cells) would be the natural extension.
- **No save/load.** Quitting discards the current grid.
- **No timer / scoring.**

## Running the tests

From IntelliJ: right-click `src/test/java/sudoku` → *Run 'Tests in
sudoku'*. JUnit 5 is bundled with the IDE.

There is no Maven/Gradle wrapper, so command-line test execution requires
adding the JUnit Platform Console Launcher jar to the classpath manually.
Running tests via the IDE is the supported path.
