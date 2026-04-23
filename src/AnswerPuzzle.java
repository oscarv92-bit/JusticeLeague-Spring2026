import java.util.Random;

public class AnswerPuzzle extends Puzzle {
    private String answer;
    private String riddleText;
    private boolean randomMode;
    private int minRoll;
    private int maxRoll;
    private int winningNumber;
    private Random random;

    public AnswerPuzzle(String id, String objectName, String description, String searchText,
                        String failMessage, String successMessage, int maxAttempts,
                        Item reward, String answer, String riddleText) {
        super(id, objectName, description, searchText, failMessage, successMessage, maxAttempts, reward);
        this.answer = answer.toLowerCase().trim();
        this.riddleText = riddleText;
        this.randomMode = false;
    }

    public AnswerPuzzle(String id, String objectName, String description, String searchText,
                        String failMessage, String successMessage, int maxAttempts,
                        Item reward, int minRoll, int maxRoll, int winningNumber) {
        super(id, objectName, description, searchText, failMessage, successMessage, maxAttempts, reward);
        this.randomMode = true;
        this.minRoll = minRoll;
        this.maxRoll = maxRoll;
        this.winningNumber = winningNumber;
        this.random = new Random();
    }

    @Override
    public String examine(String target) {
        String base = super.examine(target);

        if (!randomMode && riddleText != null && base.equals(description)) {
            return description + "\n" + riddleText;
        }

        return base;
    }

    @Override
    public String solve(String input, Player player) {
        if (randomMode) {
            return "This puzzle should be attempted, not solved with an answer.";
        }

        if (solved) {
            return "You already solved this puzzle.";
        }

        if (failed) {
            return "This puzzle can no longer be completed.";
        }

        if (input == null || input.trim().isEmpty()) {
            return handleFailedAttempt(player);
        }

        if (input.toLowerCase().trim().equals(answer)) {
            return finishPuzzle(player);
        }

        return handleFailedAttempt(player);
    }

    @Override
    public String attempt(Player player) {
        if (!randomMode) {
            return "That puzzle cannot be attempted that way.";
        }

        if (solved) {
            return "You already solved this puzzle.";
        }

        if (failed) {
            return "This puzzle can no longer be completed.";
        }

        int roll = random.nextInt(maxRoll - minRoll + 1) + minRoll;

        if (roll == winningNumber) {
            return finishPuzzle(player) + "\nYou rolled: " + roll;
        }

        attemptsUsed++;

        if (attemptsUsed >= maxAttempts) {
            failed = true;
            applyExplosionPenalty(player);
            return failMessage + "\nYou rolled: " + roll + "\nThe puzzle explodes and damages you badly.";
        }

        return failMessage + "\nYou rolled: " + roll + "\nAttempts left: " + getAttemptsLeft();
    }
}
