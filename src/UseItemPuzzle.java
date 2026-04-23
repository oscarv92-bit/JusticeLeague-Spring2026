public class UseItemPuzzle extends Puzzle {
    private String requiredItem;
    private boolean consumeRequiredItem;

    public UseItemPuzzle(String id, String objectName, String description, String searchText,
                         String failMessage, String successMessage, int maxAttempts,
                         Item reward, String requiredItem, boolean consumeRequiredItem) {
        super(id, objectName, description, searchText, failMessage, successMessage, maxAttempts, reward);
        this.requiredItem = requiredItem.toLowerCase();
        this.consumeRequiredItem = consumeRequiredItem;
    }

    public String getRequiredItem() {
        return requiredItem;
    }

    @Override
    public String useItem(String itemName, String targetName, Player player) {
        if (solved) {
            return "You already solved this puzzle.";
        }

        if (failed) {
            return "This puzzle can no longer be completed.";
        }

        if (!targetName.equalsIgnoreCase(objectName)) {
            return "That item cannot be used on that object.";
        }

        if (!player.hasItem(itemName)) {
            return handleFailedAttempt(player);
        }

        if (!itemName.equalsIgnoreCase(requiredItem)) {
            return handleFailedAttempt(player);
        }

        if (consumeRequiredItem) {
            player.removeItem(requiredItem);
        }

        return finishPuzzle(player);
    }
}