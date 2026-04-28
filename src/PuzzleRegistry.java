import java.util.ArrayList;
import java.util.List;

public class PuzzleRegistry {

    public static List<Puzzle> createAllPuzzles() {
        List<Puzzle> puzzles = new ArrayList<>();

        puzzles.add(new UseItemPuzzle(
                "FR#01PZ",
                "scooter",
                "A scooter could help you flee monsters. It looks like it needs power.",
                "You find a scooter here.",
                "Looks like it still needs a battery.",
                "Looks like you've got a ride! Now you can flee from zombies.",
                3,
                new Item(32, "Electric Scooter", "A furious motor vehicle that can improve your speed.", -1, "misc", 0, 0, 0, 0, 0, 0),
                "Car Battery",
                true
        ));

        puzzles.add(new UseItemPuzzle(
                "FR#02PZ",
                "backroom",
                "The backroom door looks jammed shut.",
                "There is a locked backroom here.",
                "You need to find a crowbar first.",
                "Congratulations, you found a rare bandage.",
                3,
                new Item(42, "Bandage", "Bandage from your favorite local hospital.", -1, "consumable", 0, 0, 0, 40, 0, 0),
                "Crowbar",
                false
        ));

        puzzles.add(new UseItemPuzzle(
                "FR#03PZ",
                "vending machine",
                "An old vending machine stands in the room.",
                "There is a vending machine here.",
                "Nothing useful in your pockets. Find a coin first.",
                "Something more useful than a snack. A bandage.",
                3,
                new Item(42, "Bandage", "Bandage from your favorite local hospital.", -1, "consumable", 0, 0, 0, 40, 0, 0),
                "Coin",
                true
        ));

        puzzles.add(new UseItemPuzzle(
                "FR#04PZ",
                "locker",
                "A police locker is locked tight.",
                "There is a locked police locker here.",
                "Access denied. Cops love eating pizza.",
                "Congratulations! An armored plate vest will protect you against zombie attacks.",
                3,
                new Item(14, "Armor plate", "Armor plate that absorbs impact from damages.", -1, "armor", 0, 0, 3, 0, 0, 0),
                "Police Badge",
                false
        ));

        puzzles.add(new UseItemPuzzle(
                "FR#05PZ",
                "medical cabinet",
                "A medical cabinet has a keycard slot.",
                "You find a medical cabinet with a slot for a keycard.",
                "You need a hospital keycard. Maybe check the parking garage outside.",
                "Med kit secured. This should keep you alive longer.",
                3,
                new Item(42, "Bandage", "Bandage from your favorite local hospital.", -1, "consumable", 0, 0, 0, 40, 0, 0),
                "Keycard",
                false
        ));

        puzzles.add(new UseItemPuzzle(
                "FR#06PZ",
                "monkey cage",
                "The monkey cage is locked tight.",
                "There is a locked monkey cage here.",
                "The cage is locked tight. You will need some bolt cutters.",
                "The monkeys are thankful for helping them escape! They tossed you a Rare Banana.",
                3,
                new Item(39, "Rare Banana", "An extremely rare banana given to you by a monkey.", -1, "consumable", 0, 0, 0, 25, 0, 0),
                "Bolt Cutters",
                false
        ));

        puzzles.add(new AnswerPuzzle(
                "FR#07PZ",
                "bank vault",
                "A heavy vault door blocks the reward inside.",
                "There is a locked bank vault here.",
                "The vault won't budge. You need the vault code. Look around the city park.",
                "The vault clicks open. Jackpot! Coins can be used throughout the entire map.",
                3,
                new Item(30, "Coin", "A unique coin that can trick the vending machine.", -1, "misc", 0, 0, 0, 0, 0, 0),
                "999",
                null
        ));

        puzzles.add(new AnswerPuzzle(
                "FR#08PZ",
                "motel",
                "There are multiple motel keys here. One of them should work.",
                "You find several motel keys.",
                "Zombie gets closer. You chose the wrong key.",
                "The door unlocks. You found runner joggers.",
                3,
                new Item(17, "Runner Joggers", "A pair of joggers stolen from a runner.", -1, "armor", 0, 0, 2, 0, 2, 0),
                6,
                1,
                10
        ));

        puzzles.add(new AnswerPuzzle(
                "FR#09PZ",
                "toy chest",
                "A toy chest is locked with a riddle.",
                "You find a toy chest here.",
                "Try again. I have hands but cannot clap. I have numbers but cannot count. I tell you when to run from monsters. What am I?",
                "The toy chest pops open. Good thinking! You deserve a thinking cap.",
                3,
                new Item(22, "BaseBall Helmet", "A New York Yankee baseball helmet made of hardened plastic.", -1, "armor", 0, 0, 2, 0, 0, 0),
                "clock",
                "I have hands but cannot clap.\nI have numbers but cannot count.\nI tell you when to run from monsters.\nWhat am I?"
        ));

        return puzzles;
    }

    public static Puzzle getPuzzleById(String id) {
        for (Puzzle puzzle : createAllPuzzles()) {
            if (puzzle.getId().equalsIgnoreCase(id)) {
                return puzzle;
            }
        }
        return null;
    }
}