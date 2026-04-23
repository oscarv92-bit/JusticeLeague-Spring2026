public class AnswerPuzzle extends Puzzle {

    // For text-answer puzzles (e.g. riddles, vault codes)
    private final String correctAnswer;   // null if this is a number-range puzzle
    private final String riddle;          // optional riddle text shown to player; may be null

    // For number-range puzzles (e.g. motel key guessing)
    private final Integer rangeMin;       // null if this is a text-answer puzzle
    private final Integer rangeMax;
    private final Integer correctNumber;

    // -------------------------------------------------------
    // CONSTRUCTOR — text answer (riddle / vault code)
    // -------------------------------------------------------

    /**
     * @param correctAnswer  The exact string the player must type (case-insensitive).
     * @param riddle         Optional riddle/prompt shown when player examines the puzzle.
     *                       Pass null to use the normal description instead.
     */
    public AnswerPuzzle(String id, String objectName, String description, String searchText,
                        String failMessage, String successMessage, int maxAttempts,
                        Item reward, String correctAnswer, String riddle) {
        super(id, objectName, description, searchText, failMessage, successMessage, maxAttempts, reward);
        this.correctAnswer  = correctAnswer;
        this.riddle         = riddle;
        this.rangeMin       = null;
        this.rangeMax       = null;
        this.correctNumber  = null;
    }

    // -------------------------------------------------------
    // CONSTRUCTOR — number-range guess (e.g. motel key 1-10)
    // -------------------------------------------------------

    /**
     * @param correctNumber  The correct integer within [rangeMin, rangeMax].
     * @param rangeMin       Lowest valid guess.
     * @param rangeMax       Highest valid guess.
     */
    public AnswerPuzzle(String id, String objectName, String description, String searchText,
                        String failMessage, String successMessage, int maxAttempts,
                        Item reward, int correctNumber, int rangeMin, int rangeMax) {
        super(id, objectName, description, searchText, failMessage, successMessage, maxAttempts, reward);
        this.correctAnswer  = null;
        this.riddle         = null;
        this.correctNumber  = correctNumber;
        this.rangeMin       = rangeMin;
        this.rangeMax       = rangeMax;
    }

    // -------------------------------------------------------
    // EXAMINE — show riddle if present
    // -------------------------------------------------------

    @Override
    public String examine(String target) {
        if (!target.equalsIgnoreCase(objectName)) return "There is nothing like that here.";
        if (solved) return "You already solved this puzzle.";
        if (failed) return "This puzzle can no longer be completed.";

        if (riddle != null && !riddle.isEmpty()) {
            return description + "\n" + riddle;
        }
        if (isNumberRangePuzzle()) {
            return description + "\nGuess a number between " + rangeMin + " and " + rangeMax + ".";
        }
        return description;
    }

    // -------------------------------------------------------
    // SOLVE — text answer
    // -------------------------------------------------------

    @Override
    public String solve(String input, Player player) {
        if (solved) return "You already solved this puzzle.";
        if (failed) return "This puzzle can no longer be completed.";
        if (isNumberRangePuzzle()) return "This puzzle requires a number, not a text answer.";

        if (input != null && input.trim().equalsIgnoreCase(correctAnswer)) {
            return finishPuzzle(player);
        }
        return handleFailedAttempt(player);
    }

    // -------------------------------------------------------
    // ATTEMPT — number-range guess
    // -------------------------------------------------------

    /**
     * Called with the player's integer guess as a string.
     * Returns feedback without revealing the answer.
     */
    public String guess(String guessStr, Player player) {
        if (solved) return "You already solved this puzzle.";
        if (failed) return "This puzzle can no longer be completed.";
        if (!isNumberRangePuzzle()) return "This puzzle requires a text answer, not a number.";

        int guess;
        try {
            guess = Integer.parseInt(guessStr.trim());
        } catch (NumberFormatException e) {
            return "Please enter a number between " + rangeMin + " and " + rangeMax + ".";
        }

        if (guess < rangeMin || guess > rangeMax) {
            return "That number is out of range. Pick between " + rangeMin + " and " + rangeMax + ".";
        }

        if (guess == correctNumber) {
            return finishPuzzle(player);
        }

        // Give higher/lower hint
        String hint = (guess < correctNumber) ? "Too low." : "Too high.";
        return handleFailedAttempt(player) + " " + hint;
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    public boolean isNumberRangePuzzle() { return correctNumber != null; }
    public boolean isTextAnswerPuzzle()  { return correctAnswer != null; }
}