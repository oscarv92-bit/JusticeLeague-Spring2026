import java.util.ArrayList;

public class Item {

    private int itemID;
    private String itemName;
    private String itemDescription;
    private int location;
    private String itemType;
    private int damage;
    private int hitPercentage;
    private int defense;
    private int healthBoost;
    private int scrapValue;
    private int ammoCapacity;
    private int maxAmmo;
    private ArrayList<Integer> compatibleWeaponIDs;
    private boolean equipped;

    // -------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------

    /** Minimal constructor used by Puzzle reward creation and legacy code. */
    public Item(String itemName, String itemDescription) {
        this(0, itemName, itemDescription, -1, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>());
    }

    /** Basic constructor (no stats). */
    public Item(int itemID, String itemName, String itemDescription, int location) {
        this(itemID, itemName, itemDescription, location, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>());
    }

    /** Constructor with common stats (no ammo, no compatible weapons). */
    public Item(int itemID, String itemName, String itemDescription, int location,
                String itemType, int damage, int hitPercentage, int defense,
                int healthBoost, int scrapValue) {
        this(itemID, itemName, itemDescription, location, itemType, damage, hitPercentage,
                defense, healthBoost, scrapValue, 0, new ArrayList<>());
    }

    /** Constructor with ammo capacity. */
    public Item(int itemID, String itemName, String itemDescription, int location,
                String itemType, int damage, int hitPercentage, int defense,
                int healthBoost, int scrapValue, int ammoCapacity) {
        this(itemID, itemName, itemDescription, location, itemType, damage, hitPercentage,
                defense, healthBoost, scrapValue, ammoCapacity, new ArrayList<>());
    }

    /** Full constructor — all fields. */
    public Item(int itemID, String itemName, String itemDescription, int location,
                String itemType, int damage, int hitPercentage, int defense,
                int healthBoost, int scrapValue, int ammoCapacity,
                ArrayList<Integer> compatibleWeaponIDs) {
        this.itemID               = itemID;
        this.itemName             = itemName;
        this.itemDescription      = itemDescription;
        this.location             = location;
        this.itemType             = itemType;
        this.damage               = damage;
        this.hitPercentage        = hitPercentage;
        this.defense              = defense;
        this.healthBoost          = healthBoost;
        this.scrapValue           = scrapValue;
        this.ammoCapacity         = ammoCapacity;
        this.maxAmmo              = ammoCapacity;
        this.compatibleWeaponIDs  = new ArrayList<>(compatibleWeaponIDs);
        this.equipped             = false;
    }

    // -------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------

    public int    getItemID()          { return itemID; }

    /** Primary name getter — use this everywhere. */
    public String getName()            { return itemName; }

    /** Alias kept for any code that calls getItemName(). */
    public String getItemName()        { return itemName; }

    public String getDescription()     { return itemDescription; }
    public String getItemDescription() { return itemDescription; }

    public int    getLocation()        { return location; }
    public String getItemType()        { return itemType; }
    public int    getDamage()          { return damage; }
    public int    getHitPercentage()   { return hitPercentage; }
    public int    getDefense()         { return defense; }
    public int    getHealthBoost()     { return healthBoost; }
    public int    getScrapValue()      { return scrapValue; }
    public int    getAmmoCapacity()    { return ammoCapacity; }
    public int    getMaxAmmo()         { return maxAmmo; }

    public ArrayList<Integer> getCompatibleWeaponIDs() {
        return new ArrayList<>(compatibleWeaponIDs);
    }

    public boolean isCompatibleWith(int weaponID) {
        return compatibleWeaponIDs.contains(weaponID);
    }

    public boolean isEquipped() { return equipped; }

    // -------------------------------------------------------
    // SETTERS
    // -------------------------------------------------------

    public void setLocation(int location)         { this.location       = location; }
    public void setDamage(int damage)             { this.damage         = damage; }
    public void setHitPercentage(int hp)          { this.hitPercentage  = hp; }
    public void setAmmoCapacity(int ammoCapacity) { this.ammoCapacity   = ammoCapacity; }

    // -------------------------------------------------------
    // INVENTORY / EQUIP HELPERS
    // -------------------------------------------------------

    public boolean isInPlayerInventory()  { return location == -1; }
    public boolean isInRoom(int roomID)   { return location == roomID; }

    public void pickUp()              { this.location = -1; }
    public void pickup()              { this.location = -1; }        // alias
    public void drop(int roomID)      { this.location = roomID; }

    public String getLocationDescription() {
        return location == -1 ? "in player inventory" : "in room " + location;
    }

    public boolean isEquippable() { return "weapon".equals(itemType) || "armor".equals(itemType); }
    public boolean isConsumable() { return "consumable".equals(itemType); }
    public boolean isScrapable()  { return "scrapable".equals(itemType); }
    public boolean isAttachment() { return "attachment".equals(itemType); }

    public void equip()   { this.equipped = true; }
    public void unequip() { this.equipped = false; }

    // -------------------------------------------------------
    // DISPLAY
    // -------------------------------------------------------

    public void itemDetails() {
        System.out.println("- " + itemName + ": " + itemDescription);
        if ("weapon".equals(itemType)) {
            System.out.println("  Damage: " + damage + ", Hit %: " + hitPercentage);
            if (ammoCapacity > 0) System.out.println("  Ammo Capacity: " + ammoCapacity);
        } else if ("armor".equals(itemType)) {
            System.out.println("  Defense: " + defense);
        } else if ("consumable".equals(itemType)) {
            System.out.println("  Health Boost: " + healthBoost);
        } else if ("scrapable".equals(itemType)) {
            System.out.println("  Scrap Value: " + scrapValue);
        }
        if (equipped) System.out.println("  (Equipped)");
    }

    @Override
    public String toString() {
        return itemName + " [" + itemType + "]";
    }
}