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
        if (value < 0)          value = 0;
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
        if (billyHealth < 0) billyHealth = 0;
        // Billy is downed but NOT removed as companion — feed him to revive
    }

    public void healBilly(int amount) {
        billyHealth += amount;
        if (billyHealth > BILLY_MAX_HEALTH) billyHealth = BILLY_MAX_HEALTH;
    }

    public int     getBillyHealth() { return billyHealth; }
    public boolean isBillyAlive()   { return billyIsCompanion && billyHealth > 0; }
    public boolean isBillyDowned()  { return billyIsCompanion && billyHealth <= 0; }

    // -------------------------------------------------------
    // PICKUP / DROP
    // -------------------------------------------------------

    public String pickup(String itemName, Room room) {
        Item picked = room.removeItem(itemName);
        if (picked == null) return "That item is not in this room.";
        picked.pickUp();
        addItem(picked);
        return picked.getName() + " added to inventory.";
    }

    public String drop(String itemName, Room room) {
        // Check inventory first
        Item dropped = removeItem(itemName);
        // If not in inventory, check equipment
        if (dropped == null) {
            dropped = removeEquipment(itemName);
            if (dropped != null) dropped.unequip();
        }
        if (dropped == null) return "You do not have that item.";
        dropped.drop(room.getRoomNumber());
        room.addItem(dropped);
        return dropped.getName() + " dropped in " + room.getName() + ".";
    }

    // -------------------------------------------------------
    // DISPLAY
    // -------------------------------------------------------

    public String showInventory() {
        if (inventory.isEmpty()) return "Your inventory is empty.";
        StringBuilder sb = new StringBuilder("You are carrying:\n");
        for (Item item : inventory) {
            sb.append("  - ").append(item.getName())
                    .append(" [").append(item.getItemType()).append("]\n");
        }
        return sb.toString().trim();
    }

    public String showEquipment() {
        if (equipment.isEmpty()) return "You have no equipment.";
        StringBuilder sb = new StringBuilder("Your equipment:\n");
        for (Item item : equipment) {
            sb.append("  - ").append(item.getName());
            if ("weapon".equals(item.getItemType())) {
                sb.append(" [DMG: ").append(item.getDamage())
                        .append(" | HIT: ").append(item.getHitPercentage()).append("%");
                if (item.getAmmoCapacity() > 0)
                    sb.append(" | AMMO: ").append(item.getAmmoCapacity());
                sb.append("]");
            } else {
                sb.append(" [DEF: ").append(item.getDefense()).append("]");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    public String showStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Health: ").append(health).append("/").append(MAX_HEALTH).append("\n");
        if (billyIsCompanion)
            sb.append("Billy: ").append(billyHealth).append("/").append(BILLY_MAX_HEALTH).append(" HP\n");
        sb.append(getStatusMessage());
        return sb.toString().trim();
    }

    public String inspect(String itemName) {
        Item found = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) { found = item; break; }
        }
        if (found == null) {
            for (Item item : equipment) {
                if (item.getName().equalsIgnoreCase(itemName)) { found = item; break; }
            }
        }
        if (found == null) return "You do not have that item.";
        StringBuilder sb = new StringBuilder();
        sb.append(found.getName()).append(": ").append(found.getDescription()).append("\n");
        if ("weapon".equals(found.getItemType())) {
            sb.append("  Damage: ").append(found.getDamage())
                    .append(", Hit %: ").append(found.getHitPercentage());
            if (found.getAmmoCapacity() > 0)
                sb.append(", Ammo: ").append(found.getAmmoCapacity());
        } else if ("armor".equals(found.getItemType())) {
            sb.append("  Defense: ").append(found.getDefense());
        } else if ("consumable".equals(found.getItemType())) {
            sb.append("  Health Boost: ").append(found.getHealthBoost());
        }
        if (found.isEquipped()) sb.append("\n  (Equipped)");
        return sb.toString().trim();
    }

    // -------------------------------------------------------
    // EQUIP
    // -------------------------------------------------------

    public String equip(String itemName) {
        Item toEquip = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) { toEquip = item; break; }
        }
        if (toEquip == null)         return "You do not have that item.";
        if (!toEquip.isEquippable()) return "That item cannot be equipped.";
        toEquip.equip();
        removeItem(toEquip.getName());
        addEquipment(toEquip);
        StringBuilder sb = new StringBuilder("You equipped " + toEquip.getName() + ".\n");
        if ("weapon".equals(toEquip.getItemType())) {
            sb.append("  Damage: ").append(toEquip.getDamage())
                    .append(" | Hit chance: ").append(toEquip.getHitPercentage()).append("%");
        } else {
            sb.append("  Defense: ").append(toEquip.getDefense());
        }
        return sb.toString().trim();
    }

    // -------------------------------------------------------
    // ATTACH
    // -------------------------------------------------------

    public String attach(String attachName, String weaponName) {
        Item attachment = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(attachName)) { attachment = item; break; }
        }
        if (attachment == null)         return "You do not have " + attachName + " in your inventory.";
        if (!attachment.isAttachment()) return attachName + " is not an attachment.";

        Item weapon = null;
        for (Item item : equipment) {
            if (item.getName().equalsIgnoreCase(weaponName)) { weapon = item; break; }
        }
        if (weapon == null) {
            for (Item item : inventory) {
                if (item.getName().equalsIgnoreCase(weaponName)) { weapon = item; break; }
            }
        }
        if (weapon == null)                         return "You do not have " + weaponName + ".";
        if (!"weapon".equals(weapon.getItemType())) return weaponName + " is not a weapon.";
        if (!attachment.getCompatibleWeaponIDs().isEmpty()
                && !attachment.isCompatibleWith(weapon.getItemID()))
            return attachName + " is not compatible with " + weaponName + ".";

        int dmgBonus = attachment.getDamage();
        int hitBonus = attachment.getHitPercentage();
        if (dmgBonus > 0) weapon.setDamage(weapon.getDamage() + dmgBonus);
        if (hitBonus > 0) weapon.setHitPercentage(weapon.getHitPercentage() + hitBonus);
        removeItem(attachment.getName());

        StringBuilder sb = new StringBuilder("You attached " + attachName + " to " + weaponName + ".\n");
        if (dmgBonus > 0) sb.append("  +").append(dmgBonus).append(" damage.\n");
        if (hitBonus > 0) sb.append("  +").append(hitBonus).append("% hit chance.\n");
        sb.append("  ").append(weaponName).append(" -> DMG: ").append(weapon.getDamage())
                .append(" | HIT: ").append(weapon.getHitPercentage()).append("%");
        return sb.toString().trim();
    }

    // -------------------------------------------------------
    // USE CONSUMABLE
    // -------------------------------------------------------

    public String useConsumable(String itemName) {
        Item toUse = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) { toUse = item; break; }
        }
        if (toUse == null)         return "You do not have that item.";
        if (!toUse.isConsumable()) return toUse.getName() + " cannot be used that way. Try EQUIP or ATTACH.";
        int boost = toUse.getHealthBoost();
        removeItem(toUse.getName());
        if (boost > 0) {
            heal(boost);
            return "You used " + toUse.getName() + " and restored " + boost
                    + " HP. Health: " + health + "/" + MAX_HEALTH;
        }
        return "You used " + toUse.getName() + ". Something feels different...";
    }

    // -------------------------------------------------------
    // FEED BILLY
    // -------------------------------------------------------

    public String feedBilly(Room room) {
        // Revive downed Billy (companion but HP = 0)
        if (isBillyDowned()) {
            if (!hasItem("Rotten Flesh")) return "Billy is down. Give him Rotten Flesh to revive him.";
            removeItem("Rotten Flesh");
            billyHealth = BILLY_MAX_HEALTH;
            return "You offer Billy some Rotten Flesh. He stirs and slowly gets back up.\nBilly is back on his feet!";
        }
        if (!room.isBillyPresent())   return "Billy is not here.";
        if (billyIsCompanion)         return "Billy is already by your side.";
        if (!hasItem("Rotten Flesh")) return "I do not have anything Billy would want to eat.";
        removeItem("Rotten Flesh");
        tameBilly();
        room.setBillyPresent(false);
        for (Monster m : room.getMonsters()) {
            if (m.getMonsterType().equals(Monster.TYPE_BILLY)) {
                m.tame();
                break;
            }
        }
        return "Billy pauses, staring at the offering. Slowly he takes it from your hand.\n"
                + "His eyes soften with recognition... He remembers you.";
    }

    // -------------------------------------------------------
    // COMBAT HELPERS
    // -------------------------------------------------------

    /** Returns the equipped weapon, or null if none. */
    public Item getEquippedWeapon() {
        for (Item item : equipment) {
            if ("weapon".equals(item.getItemType())) return item;
        }
        return null;
    }

    /** Returns total defense from all equipped armor. */
    public int getTotalDefense() {
        int total = 0;
        for (Item item : equipment) {
            if ("armor".equals(item.getItemType())) total += item.getDefense();
        }
        return total;
    }

    // -------------------------------------------------------
    // RELOAD — refills equipped weapon back to its max ammo
    // -------------------------------------------------------

    public String reload(String weaponName) {
        Item weapon = null;
        for (Item item : equipment) {
            if ("weapon".equals(item.getItemType())) {
                if (weaponName.isEmpty() || item.getName().equalsIgnoreCase(weaponName)) {
                    weapon = item;
                    break;
                }
            }
        }
        if (weapon == null && !weaponName.isEmpty()) return "You do not have " + weaponName + " equipped.";
        if (weapon == null)                          return "You have no weapon equipped.";
        if (weapon.getMaxAmmo() == 0)                return weapon.getName() + " does not use ammo.";
        if (weapon.getAmmoCapacity() == weapon.getMaxAmmo())
            return weapon.getName() + " is already fully loaded. (" + weapon.getAmmoCapacity() + "/" + weapon.getMaxAmmo() + ")";

        weapon.setAmmoCapacity(weapon.getMaxAmmo());
        return "You reload " + weapon.getName() + ". Ammo: " + weapon.getAmmoCapacity() + "/" + weapon.getMaxAmmo();
    }

    // -------------------------------------------------------
    // UNEQUIP
    // -------------------------------------------------------

    public String unequip(String itemName) {
        Item item = removeEquipment(itemName);
        if (item == null) return "You do not have " + itemName + " equipped.";
        item.unequip();
        addItem(item);
        return "You unequipped " + item.getName() + ". It is now in your inventory.";
    }

}