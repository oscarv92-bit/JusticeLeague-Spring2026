import java.util.ArrayList;

public class Player {

    // current room number -- used to look up Room in the HashMap in Main
    private int currentRoomNumber;

    // health starts at 100, game over when it hits 0 (FR#02PL)
    private int health;
    private static final int MAX_HEALTH = 100;

    // inventory holds usable items the player has picked up (FR#03PL)
    // uses Item directly -- matches your teammates Item class
    private ArrayList<Item> inventory;

    // equipment holds equippable gear like armor and weapons (FR#04PL)
    // uses Item directly -- matches your teammates Item class
    private ArrayList<Item> equipment;

    // Billy companion state (FR#03ZB)
    // false = not yet tamed, true = Billy is following the player
    private boolean billyIsCompanion;

    // Billy's health -- only matters when billyIsCompanion is true
    private int billyHealth;
    private static final int BILLY_MAX_HEALTH = 60;

    // counts how many times STATUS is typed in a row with nothing else between
    // used for the easter egg message after 3 in a row (FR#02PL)
    private int statusSpamCount;

    // CONSTRUCTOR
    // Main creates the player with the starting room number, same as Willy Wonka
    public Player(int startingRoomNumber) {
        this.currentRoomNumber = startingRoomNumber;
        this.health            = MAX_HEALTH;
        this.inventory         = new ArrayList<>();
        this.equipment         = new ArrayList<>();
        this.billyIsCompanion  = false;
        this.billyHealth       = BILLY_MAX_HEALTH;
        this.statusSpamCount   = 0;
    }

    // -------------------------------------------------------
    // MOVEMENT -- same as Willy Wonka, Main uses these unchanged
    // -------------------------------------------------------

    public int getCurrentRoomNumber() {
        return currentRoomNumber;
    }

    public void setCurrentRoomNumber(int newRoomNumber) {
        currentRoomNumber = newRoomNumber;
        statusSpamCount = 0; // reset spam counter on any move
    }

    // -------------------------------------------------------
    // HEALTH (FR#02PL)
    // -------------------------------------------------------

    public int getHealth() {
        return health;
    }

    // called by Controller/Monster when player takes damage in combat
    public void takeDamage(int amount) {
        health = health - amount;
        if (health < 0) {
            health = 0;
        }
        statusSpamCount = 0;
    }

    // called by Controller when player uses a consumable item
    public void heal(int amount) {
        health = health + amount;
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        }
        statusSpamCount = 0;
    }

    // called by Controller to set health to an exact value
    // used for puzzle fail penalties that deal percentage damage
    public void setHealth(int value) {
        if (value < 0) value = 0;
        if (value > MAX_HEALTH) value = MAX_HEALTH;
        health = value;
    }

    // Controller checks this after every combat round or damage event
    public boolean isDead() {
        return health <= 0;
    }

    // -------------------------------------------------------
    // STATUS COMMAND (FR#02PL)
    // Controller calls getStatusMessage() and passes result to View to print
    // Returns the easter egg message after 3 consecutive STATUS inputs
    // -------------------------------------------------------

    public String getStatusMessage() {
        statusSpamCount++;
        if (statusSpamCount >= 3) {
            statusSpamCount = 0;
            return "My status is gonna be dead if I just sit here checking myself out all day.";
        }
        return "Considering the circumstances, I feel fine.";
    }

    // -------------------------------------------------------
    // INVENTORY (FR#03PL) -- same as Willy Wonka so Main works unchanged
    // -------------------------------------------------------

    public void addItem(Item item) {
        inventory.add(item);
        statusSpamCount = 0;
    }

    // removes and returns item by name, null if not found
    public Item removeItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(itemName)) {
                statusSpamCount = 0;
                return inventory.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------
    // EQUIPMENT (FR#04PL)
    // equipment is separate from inventory -- these are wearable/wieldable items
    // your teammate (Item class owner) calls these when player equips gear
    // -------------------------------------------------------

    public void addEquipment(Item item) {
        equipment.add(item);
        statusSpamCount = 0;
    }

    // removes and returns equipment by name, null if not found
    public Item removeEquipment(String itemName) {
        for (int i = 0; i < equipment.size(); i++) {
            if (equipment.get(i).getName().equalsIgnoreCase(itemName)) {
                statusSpamCount = 0;
                return equipment.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getEquipment() {
        return equipment;
    }

    public boolean hasEquipment(String itemName) {
        for (Item item : equipment) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------
    // BILLY COMPANION (FR#03ZB, FR#04ZB)
    // Controller calls tameBilly() after successful feed billy command
    // with rotten flesh in inventory
    // -------------------------------------------------------

    // called by Controller after player feeds Billy rotten flesh in Room 7
    public void tameBilly() {
        billyIsCompanion = true;
        billyHealth = BILLY_MAX_HEALTH;
        statusSpamCount = 0;
    }

    public boolean isBillyCompanion() {
        return billyIsCompanion;
    }

    // called by Controller during combat when monster targets Billy
    public void dealDamageToBilly(int amount) {
        billyHealth = billyHealth - amount;
        if (billyHealth < 0) {
            billyHealth = 0;
        }
        if (billyHealth == 0) {
            billyIsCompanion = false;
        }
    }

    public void healBilly(int amount) {
        billyHealth = billyHealth + amount;
        if (billyHealth > BILLY_MAX_HEALTH) {
            billyHealth = BILLY_MAX_HEALTH;
        }
    }

    public int getBillyHealth() {
        return billyHealth;
    }

    public boolean isBillyAlive() {
        return billyHealth > 0;
    }
}


//remove later!!!

class Item {
    private String name;
    private String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

class Monster {
    private String name;

    public Monster() {
        this.name = "Monster";
    }

    public Monster(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Puzzle {
    private String description;
    private String answer;
    private int remainingAttempts;
    private boolean solved;

    public Puzzle(String description, String answer, int remainingAttempts) {
        this.description = description;
        this.answer = answer;
        this.remainingAttempts = remainingAttempts;
        this.solved = false;
    }

    public void resetAttempts() {
        remainingAttempts = 3;
    }

    public String getDescription() {
        return description;
    }

    public boolean attemptAnswer(String userAnswer) {
        if (userAnswer != null && userAnswer.equalsIgnoreCase(answer)) {
            solved = true;
            return true;
        }
        remainingAttempts--;
        return false;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isSolved() {
        return solved;
    }
}