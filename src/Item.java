import java.util.ArrayList;

public class Item {

    // Item Variables
    private int itemID;
    private String itemName;
    private String itemDescription;
    private int location;  // Room ID or -1 for player inventory
    private String itemType;  // e.g., "weapon", "armor", "attachment", "consumable", "scrapable", "misc"
    private int damage;  // For weapons
    private int hitPercentage;  // For weapons (e.g., 80 for 80%)
    private int defense;  // For armor
    private int healthBoost;  // For consumables
    private int scrapValue;  // For scrapable items
    private int ammoCapacity;  // For weapons with ammo (e.g., 30 for M4, 0 for melee)
    private ArrayList<Integer> compatibleWeaponIDs;  // For attachments (list of weapon IDs this attachment works with)
    private boolean equipped;  // For equippable items

    // Item Constructor
    public Item(int itemID, String itemName, String itemDescription, int location) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = location;
        this.itemType = "misc";
        this.damage = 0;
        this.hitPercentage = 0;
        this.defense = 0;
        this.healthBoost = 0;
        this.scrapValue = 0;
        this.ammoCapacity = 0;
        this.compatibleWeaponIDs = new ArrayList<>();
        this.equipped = false;
    }

    // New Constructor with all fields
    public Item(int itemID, String itemName, String itemDescription, int location, String itemType, int damage, int hitPercentage, int defense, int healthBoost, int scrapValue) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = location;
        this.itemType = itemType;
        this.damage = damage;
        this.hitPercentage = hitPercentage;
        this.defense = defense;
        this.healthBoost = healthBoost;
        this.scrapValue = scrapValue;
        this.ammoCapacity = 0;
        this.compatibleWeaponIDs = new ArrayList<>();
        this.equipped = false;
    }

    // Constructor with ammoCapacity
    public Item(int itemID, String itemName, String itemDescription, int location, String itemType, int damage, int hitPercentage, int defense, int healthBoost, int scrapValue, int ammoCapacity) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = location;
        this.itemType = itemType;
        this.damage = damage;
        this.hitPercentage = hitPercentage;
        this.defense = defense;
        this.healthBoost = healthBoost;
        this.scrapValue = scrapValue;
        this.ammoCapacity = ammoCapacity;
        this.compatibleWeaponIDs = new ArrayList<>();
        this.equipped = false;
    }

    // Constructor with ammoCapacity and compatibleWeaponIDs
    public Item(int itemID, String itemName, String itemDescription, int location, String itemType, int damage, int hitPercentage, int defense, int healthBoost, int scrapValue, int ammoCapacity, ArrayList<Integer> compatibleWeaponIDs) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = location;
        this.itemType = itemType;
        this.damage = damage;
        this.hitPercentage = hitPercentage;
        this.defense = defense;
        this.healthBoost = healthBoost;
        this.scrapValue = scrapValue;
        this.ammoCapacity = ammoCapacity;
        this.compatibleWeaponIDs = new ArrayList<>(compatibleWeaponIDs);
        this.equipped = false;
    }
    // New Constructors FIX
    public Item(String itemName, String itemDescription) {
        this.itemID = 0;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.location = -1;
        this.itemType = "misc";
        this.damage = 0;
        this.hitPercentage = 0;
        this.defense = 0;
        this.healthBoost = 0;
        this.scrapValue = 0;
        this.ammoCapacity = 0;
        this.compatibleWeaponIDs = new ArrayList<>();
        this.equipped = false;
    }

    // Getters
    public int getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getLocation() {
        return location;
    }

    public String getItemType() {
        return itemType;
    }

    public int getDamage() {
        return damage;
    }

    public int getHitPercentage() {
        return hitPercentage;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealthBoost() {
        return healthBoost;
    }

    public int getScrapValue() {
        return scrapValue;
    }

    public int getAmmoCapacity() {
        return ammoCapacity;
    }

    public ArrayList<Integer> getCompatibleWeaponIDs() {
        return new ArrayList<>(compatibleWeaponIDs);
    }

    public boolean isCompatibleWith(int weaponID) {
        return compatibleWeaponIDs.contains(weaponID);
    }

    public boolean isEquipped() {
        return equipped;
    }

    // Setters
    public void setLocation(int location) {
        this.location = location;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHitPercentage(int hitPercentage) {
        this.hitPercentage = hitPercentage;
    }

    public void setAmmoCapacity(int ammoCapacity) {
        this.ammoCapacity = ammoCapacity;
    }

    // Display Item Details
    public void itemDetails() {
        System.out.println("- " + itemName + ": " + itemDescription);
        if ("weapon".equals(itemType)) {
            System.out.println("  Damage: " + damage + ", Hit %: " + hitPercentage);
            if (ammoCapacity > 0) {
                System.out.println("  Ammo Capacity: " + ammoCapacity);
            }
        } else if ("armor".equals(itemType)) {
            System.out.println("  Defense: " + defense);
        } else if ("consumable".equals(itemType)) {
            System.out.println("  Health Boost: " + healthBoost);
        } else if ("scrapable".equals(itemType)) {
            System.out.println("  Scrap Value: " + scrapValue);
        }
        if (equipped) {
            System.out.println("  (Equipped)");
        }
    }

    // Pickup/Drop Support Methods
    public boolean isInPlayerInventory() {
        return location == -1;
    }

    public boolean isInRoom(int roomID) {
        return location == roomID;
    }

    public void pickup() {
        this.location = -1;  // Move to player inventory
    }

    public void drop(int roomID) {
        this.location = roomID;  // Move to specified room
    }

    public String getLocationDescription() {
        if (location == -1) {
            return "in player inventory";
        } else {
            return "in room " + location;
        }
    }

    // New behavior methods
    public boolean isEquippable() {
        return "weapon".equals(itemType) || "armor".equals(itemType);
    }

    public boolean isConsumable() {
        return "consumable".equals(itemType);
    }

    public boolean isScrapable() {
        return "scrapable".equals(itemType);
    }

    public boolean isAttachment() {
        return "attachment".equals(itemType);
    }

    public void equip() {
        this.equipped = true;
    }

    public void unequip() {
        this.equipped = false;
    }

    // New Methods FIX
    public String getName() {
        return itemName;
    }

    public void pickUp() {
        this.location = -1;
    }
}
