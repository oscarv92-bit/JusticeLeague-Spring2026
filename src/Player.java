import java.util.ArrayList;

public class Player {

    private int currentRoomNumber;

    private int health;
    private static final int MAX_HEALTH = 100;

    private ArrayList<Item> inventory;
    private ArrayList<Item> equipment;

    private boolean billyIsCompanion;
    private int billyHealth;
    private static final int BILLY_MAX_HEALTH = 60;

    private int statusSpamCount;

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
    // MOVEMENT
    // -------------------------------------------------------

    public int getCurrentRoomNumber() { return currentRoomNumber; }

    public void setCurrentRoomNumber(int newRoomNumber) {
        currentRoomNumber = newRoomNumber;
        statusSpamCount   = 0;
    }

    // -------------------------------------------------------
    // HEALTH
    // -------------------------------------------------------

    public int getHealth() { return health; }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        statusSpamCount = 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > MAX_HEALTH) health = MAX_HEALTH;
        statusSpamCount = 0;
    }

    public void setHealth(int value) {
        if (value < 0)         value = 0;
        if (value > MAX_HEALTH) value = MAX_HEALTH;
        health = value;
    }

    public boolean isDead() { return health <= 0; }

    // -------------------------------------------------------
    // STATUS COMMAND
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
    // INVENTORY
    // -------------------------------------------------------

    public void addItem(Item item) {
        inventory.add(item);
        statusSpamCount = 0;
    }

    public Item removeItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equalsIgnoreCase(itemName)) {
                statusSpamCount = 0;
                return inventory.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getInventory() { return inventory; }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) return true;
        }
        return false;
    }

    // -------------------------------------------------------
    // EQUIPMENT
    // -------------------------------------------------------

    public void addEquipment(Item item) {
        equipment.add(item);
        statusSpamCount = 0;
    }

    public Item removeEquipment(String itemName) {
        for (int i = 0; i < equipment.size(); i++) {
            if (equipment.get(i).getName().equalsIgnoreCase(itemName)) {
                statusSpamCount = 0;
                return equipment.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getEquipment() { return equipment; }

    public boolean hasEquipment(String itemName) {
        for (Item item : equipment) {
            if (item.getName().equalsIgnoreCase(itemName)) return true;
        }
        return false;
    }

    // -------------------------------------------------------
    // BILLY COMPANION
    // -------------------------------------------------------

    public void tameBilly() {
        billyIsCompanion = true;
        billyHealth      = BILLY_MAX_HEALTH;
        statusSpamCount  = 0;
    }

    public boolean isBillyCompanion() { return billyIsCompanion; }

    public void dealDamageToBilly(int amount) {
        billyHealth -= amount;
        if (billyHealth < 0)  billyHealth = 0;
        if (billyHealth == 0) billyIsCompanion = false;
    }

    public void healBilly(int amount) {
        billyHealth += amount;
        if (billyHealth > BILLY_MAX_HEALTH) billyHealth = BILLY_MAX_HEALTH;
    }

    public int     getBillyHealth() { return billyHealth; }
    public boolean isBillyAlive()   { return billyHealth > 0; }
}