public abstract class Puzzle {

    protected String  id;
    protected String  objectName;
    protected String  description;
    protected String  searchText;
    protected String  failMessage;
    protected String  successMessage;
    protected int     attemptsUsed;
    protected int     maxAttempts;
    protected boolean solved;
    protected boolean failed;
    protected Item    reward;
    protected boolean rewardGiven;

    // -------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------

    public Puzzle(String id, String objectName, String description, String searchText,
                  String failMessage, String successMessage, int maxAttempts, Item reward) {
        this.id             = id;
        this.objectName     = objectName.toLowerCase();
        this.description    = description;
        this.searchText     = searchText;
        this.failMessage    = failMessage;
        this.successMessage = successMessage;
        this.maxAttempts    = maxAttempts;
        this.reward         = reward;
        this.attemptsUsed   = 0;
        this.solved         = false;
        this.failed         = false;
        this.rewardGiven    = false;
    }

    // -------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------

    public String  getId()           { return id; }
    public String  getObjectName()   { return objectName; }
    public String  getSearchText()   { return searchText; }
    public boolean isSolved()        { return solved; }
    public boolean isFailed()        { return failed; }
    public int     getAttemptsUsed() { return attemptsUsed; }
    public int     getAttemptsLeft() { return maxAttempts - attemptsUsed; }

    // -------------------------------------------------------
    // EXAMINE
    // -------------------------------------------------------

    public String examine(String target) {
        if (!target.equalsIgnoreCase(objectName)) return "There is nothing like that here.";
        if (solved) return "You already solved this puzzle.";
        if (failed) return "This puzzle can no longer be completed.";
        return description;
    }

    // -------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------

    protected String handleFailedAttempt(Player player) {
        attemptsUsed++;
        if (attemptsUsed >= maxAttempts) {
            failed = true;
            applyExplosionPenalty(player);
            return failMessage + "\nThe puzzle explodes and damages you badly.";
        }
        return failMessage + "\nAttempts left: " + getAttemptsLeft();
    }

    protected void applyExplosionPenalty(Player player) {
        int damage = (int) Math.ceil(player.getHealth() * 0.5);
        player.setHealth(player.getHealth() - damage);
    }

    protected String finishPuzzle(Player player) {
        solved = true;
        if (reward != null && !rewardGiven) {
            player.addItem(reward);
            rewardGiven = true;
            return successMessage + "\nReward received: " + reward.getName();
        }
        return successMessage;
    }

    // -------------------------------------------------------
    // OVERRIDABLE INTERACTION METHODS
    // -------------------------------------------------------

    /** Called when player types USE <item> ON <target>. */
    public String useItem(String itemName, String targetName, Player player) {
        return "That does not work here.";
    }

    /** Called when player types a text answer to this puzzle. */
    public String solve(String input, Player player) {
        return "That puzzle is not solved that way.";
    }

    /** Called when player types ATTEMPT on a number-range puzzle. */
    public String attempt(Player player) {
        return "That puzzle cannot be attempted that way.";
    }
}