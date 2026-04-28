import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monster {
    //monster types
    public static final String TYPE_CRAWLER = "Crawler";
    public static final String TYPE_SPITTER = "Spitter";
    public static final String TYPE_NORMAL_ZOMBIE = "Normal Zombie";
    public static final String TYPE_SCREAMER = "Screamer";
    public static final String TYPE_LA_BRONX_GANGSTER = "La Bronx Gangster";
    public static final String TYPE_CIA_OFFICER = "CIA Officer";
    public static final String TYPE_INFECTED_CIA = "Infected CIA Agent";
    public static final String TYPE_BILLY = "Zombie Billy";

    //Core variables
    private final String monsterType;
    private final String name;
    private int currentHP;
    private final int maxHP;
    private final int accuracy;
    private final int damage;
    private final List<Item> lootTable;
    private boolean looted;
    private boolean alive;

    //Billy only
    private boolean isCompanion;
    private boolean billyKilledByPlayer;

    //Screamer ability
    private int turnsInRoom;

    private static final Random rng = new Random();


    ///////// CONSTRUCTOR //////////
    public Monster(String monsterType, String name, int maxHP, int accuracy, int damage)  {
        this.monsterType = monsterType;
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.accuracy = accuracy;
        this.damage = damage;
        this.lootTable = new ArrayList<>();
        this.looted = false;
        this.alive = true;
        this.isCompanion = false;
        this.billyKilledByPlayer = false;
        this.turnsInRoom = 0;
    }
    ////// MONSTER METHODS (HP, ACCURACY, DAMAGE) ///////
    public static Monster createCrawler() {
        return new Monster(TYPE_CRAWLER, "Crawler", 30, 40, 4);
    }

    public static Monster createSpitter() {
        return new Monster(TYPE_SPITTER, "Spitter", 44, 40, 5);
    }

    public static Monster createNormalZombie() {
        return new Monster(TYPE_NORMAL_ZOMBIE, "Zombie", 40, 50, 4);
    }

    public static Monster createScreamer() {
        return new Monster(TYPE_SCREAMER, "Screamer", 32, 30, 3);
    }

    public static Monster createLaBronxGangster() {
        return new Monster(TYPE_LA_BRONX_GANGSTER, "La Bronx Gangster", 50, 40, 6);
    }

    public static Monster createCIAOfficer() {
        return new Monster(TYPE_CIA_OFFICER, "CIA Officer", 70, 65, 9);
    }

    public static Monster createInfectedCIA() {
        return new Monster(TYPE_INFECTED_CIA, "Infected CIA Agent", 80, 70, 6);
    }

    public static Monster createBilly() {
        return new Monster(TYPE_BILLY, "Billy", 60, 60, 8);
    }

    /////// DAMAGE TAKEN ///////
    public String takeDamage(int damage) {
        if (!alive) {
            return name + " is already dead.";
        }

        currentHP -= damage;
        if (currentHP <= 0) {
            currentHP = 0;
            alive = false;
            return "You dealt " + damage + " damage to " + name + ". " + name + " has been defeated.";
        }
        return name + " took " + damage + " damage and has " + currentHP + " HP left.";
    }

    /**
     * Resolves the monster's attack this round.
     * Rolls 1-100; hit if roll <= accuracy.
     * Returns String[3]: [0] narrative, [1] damage as String, [2] target ("PLAYER" or "BILLY").
     * Caller applies the damage to the correct target.
     * If Billy is present, target is chosen randomly (50/50).
     */
    public String[] performAttack(boolean billyPresent) {
        if (!alive) {
            return new String[] { name + " is dead and cannot attack.", "0", "NONE" };
        }

        // Pick target
        String target     = "PLAYER";
        String targetName = "you";
        if (billyPresent && rng.nextInt(2) == 1) {
            target     = "BILLY";
            targetName = "Billy";
        }

        // Hit check
        boolean hit = (rng.nextInt(100) + 1) <= accuracy;

        if (!hit) {
            return new String[] {
                    name + " attacks " + targetName + " but misses!",
                    "0",
                    target
            };
        }

        return new String[] {
                name + " hits " + targetName + " for " + damage + " damage!",
                String.valueOf(damage),
                target
        };
    }

    /**
     * Billy's companion auto-attack after the player's turn.
     * Returns String[3]: [0] narrative, [1] damage as String, [2] "ENEMY".
     * Game.java picks which enemy to apply the damage to.
     */
    public String[] companionAttack() {
        if (!isCompanion || !alive) {
            return new String[] { "", "0", "ENEMY" };
        }
        return new String[] {
                "Billy lets out a low growl and attacks an enemy for " + damage + " damage!",
                String.valueOf(damage),
                "ENEMY"
        };
    }

    ///////// SCREAMER SHRIEK (summons extra enemy after 3 turns) //////////
    public void incrementTurnsInRoom() {
        if (monsterType.equals(TYPE_SCREAMER) && alive) {
            turnsInRoom++;
        }
    }

    /** Returns true when the Screamer should shriek and summon an enemy. */
    public boolean shouldShriek() {
        return monsterType.equals(TYPE_SCREAMER) && alive && turnsInRoom >= 3;
    }

    /** Reset after the shriek fires so it can trigger again in 3 more turns. */
    public void resetShriekCounter() {
        turnsInRoom = 0;
    }

    ////// LOOT //////

    /** Adds an item to this monster's loot table during game setup. */
    public void addLoot(Item item) {
        lootTable.add(item);
    }

    /**
     * Transfers all loot to the player's inventory list.
     * Returns a narrative message.
     */
    public String loot(List<Item> playerInventory) {
        if (alive)  return "That would be a bad idea while it's still moving.";
        if (looted) return "There's nothing left on the body worth taking.";

        if (lootTable.isEmpty()) {
            looted = true;
            return "You search the body but find nothing useful.";
        }

        StringBuilder sb = new StringBuilder("You search the body and find: ");
        for (int i = 0; i < lootTable.size(); i++) {
            Item item = lootTable.get(i);
            item.pickUp();
            playerInventory.add(item);
            sb.append(item.getName());
            if (i < lootTable.size() - 1) sb.append(", ");
        }
        sb.append(".");
        looted = true;
        return sb.toString();
    }

    ///////// BILLY COMPANION INTERACTIONS /////////
    /**
     * Attempts to turn Billy into a companion by feeding him rotten flesh.
     * Caller should remove Rotten Flesh from player inventory on success.
     */
    public String feedBilly(boolean hasFleshInInventory) {
        if (!monsterType.equals(TYPE_BILLY)) return "That doesn't seem like a viable interaction.";
        if (isCompanion)                      return "Billy is already by my side. He doesn't need convincing anymore.";
        if (!hasFleshInInventory)             return "I don't have anything Billy would want to eat.";

        isCompanion = true;
        return "Billy pauses, staring at the offering. Slowly, he takes it from your hand.\n" +
                "His eyes soften with recognition... He remembers you.";
    }

    /** Narrative interaction with Billy. Does not change any state. */
    public String interactWithBilly() {
        if (!monsterType.equals(TYPE_BILLY)) return "Billy isn't here.";
        if (!alive)                           return "Billy is gone.";
        if (!isCompanion)                     return "Billy snarls softly, unsure of my intentions.";

        if ((double) currentHP / maxHP < 0.25) {
            return "Billy sways slightly, barely holding himself together.";
        }
        return "Billy tilts his head slightly and lets out a low groan. He seems loyal... in his own way.";
    }

    /** Flags that the player killed Billy (triggers the Silver Locket narrative in Game.java). */
    public void markKilledByPlayer() {
        if (monsterType.equals(TYPE_BILLY)) {
            billyKilledByPlayer = true;
        }
    }

    /** Tames Billy — removes him from the enemy list without a death message. */
    public void tame() {
        if (monsterType.equals(TYPE_BILLY)) {
            isCompanion = true;
            alive = false;
        }
    }

    ////////// GETTERS //////////

    public String    getMonsterType()         { return monsterType; }
    public String    getName()                { return name; }
    public int       getCurrentHP()           { return currentHP; }
    public int       getMaxHP()               { return maxHP; }
    public int       getAccuracy()            { return accuracy; }
    public int       getDamage()              { return damage; }
    public boolean   isAlive()                { return alive; }
    public boolean   isLooted()               { return looted; }
    public boolean   isCompanion()            { return isCompanion; }
    public boolean   wasBillyKilledByPlayer() { return billyKilledByPlayer; }
    public List<Item> getLootTable()          { return lootTable; }

    @Override
    public String toString() {
        return name + " [" + currentHP + "/" + maxHP + " HP]" +
                (!alive      ? " [DEAD]"      : "") +
                (isCompanion ? " [COMPANION]" : "");
    }
    // -------------------------------------------------------
    // FACTORY
    // -------------------------------------------------------

    public static Monster createByType(String type) {
        switch (type) {
            case TYPE_CRAWLER:           return createCrawler();
            case TYPE_SPITTER:           return createSpitter();
            case TYPE_NORMAL_ZOMBIE:     return createNormalZombie();
            case TYPE_SCREAMER:          return createScreamer();
            case TYPE_LA_BRONX_GANGSTER: return createLaBronxGangster();
            case TYPE_CIA_OFFICER:       return createCIAOfficer();
            case TYPE_INFECTED_CIA:      return createInfectedCIA();
            case TYPE_BILLY:             return createBilly();
            default:                     return null;
        }
    }

    // -------------------------------------------------------
    // LOOT ASSIGNMENT — monster knows its own drops
    // -------------------------------------------------------

    public void assignLootForRoom(int roomNumber) {
        switch (monsterType) {
            case TYPE_CRAWLER:
                if (roomNumber == 2) {
                    addLoot(new Item(19, "Dunkin Donut Apron",
                            "This apron has donut cream stains but do not underestimate it.",
                            -1, "armor", 0, 0, 1, 0, 1, 0, new ArrayList<>()));
                    addLoot(new Item(26, "Dunkin Donut Plastic Glove",
                            "A pair of plastic gloves enchanted with Dunkin employees hard work.",
                            -1, "armor", 0, 0, 1, 0, 0, 0, new ArrayList<>()));
                }
                addLoot(new Item(28, "Rotten Flesh", "A chunk of flesh looking for its final destination.",
                        -1, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
                break;
            case TYPE_SCREAMER:
                if (roomNumber == 5)
                    addLoot(new Item(18, "Skirt", "A pretty pink skirt that makes you feel bonita.",
                            -1, "armor", 0, 0, 1, 0, 1, 0, new ArrayList<>()));
                addLoot(new Item(28, "Rotten Flesh", "A chunk of flesh looking for its final destination.",
                        -1, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
                break;
            case TYPE_NORMAL_ZOMBIE:
                if (roomNumber == 6)
                    addLoot(new Item(7, "Glock 19", "A powerful semi-automatic handgun.",
                            -1, "weapon", 35, 75, 0, 0, 0, 10, new ArrayList<>()));
                addLoot(new Item(28, "Rotten Flesh", "A chunk of flesh looking for its final destination.",
                        -1, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
                addLoot(new Item(41, "Scrap Cloth", "Just an old piece of cloth that is able to stop bleeding.",
                        -1, "consumable", 0, 0, 0, 10, 0, 0, new ArrayList<>()));
                break;
            case TYPE_LA_BRONX_GANGSTER:
                addLoot(new Item(7, "Glock 19", "A powerful semi-automatic handgun.",
                        -1, "weapon", 35, 75, 0, 0, 0, 3, new ArrayList<>()));
                addLoot(new Item(21, "La Bronx Bandana",
                        "A fiber headband that symbolizes honor for a group of gangsters.",
                        -1, "armor", 0, 0, 1, 0, 1, 0, new ArrayList<>()));
                break;
            case TYPE_CIA_OFFICER:
                addLoot(new Item(6, "M4", "A full automatic rifle that packs powerful piercing rounds.",
                        -1, "weapon", 20, 75, 0, 0, 0, 10, new ArrayList<>()));
                break;
            case TYPE_INFECTED_CIA:
                addLoot(new Item(14, "Armor plate", "Armor plate that absorbs impact from damages.",
                        -1, "armor", 0, 0, 3, 0, 0, 0, new ArrayList<>()));
                break;
            case TYPE_BILLY:
                addLoot(new Item(29, "Silver Locket",
                        "A silver locket from Billy. A Kawaii zombie wanted nothing but a lifelong partner.",
                        -1, "misc", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
                break;
            case TYPE_SPITTER:
                addLoot(new Item(41, "Scrap Cloth", "Just an old piece of cloth that is able to stop bleeding.",
                        -1, "consumable", 0, 0, 0, 10, 0, 0, new ArrayList<>()));
                break;
            default: break;
        }
    }

    // -------------------------------------------------------
    // COMBAT ROUND — called by Room.doAttack()
    // -------------------------------------------------------

    public String attackPlayer(Player player) {
        if (!alive) return "";
        incrementTurnsInRoom();
        String[] result = performAttack(player.isBillyCompanion() && player.isBillyAlive());
        StringBuilder sb = new StringBuilder(result[0]);
        int incoming = Integer.parseInt(result[1]);
        if (incoming > 0) {
            int defense = player.getTotalDefense();
            int net = Math.max(0, incoming - defense);
            if (defense > 0) sb.append("\n  Your armor absorbed ").append(defense).append(" damage.");
            if ("BILLY".equals(result[2])) {
                player.dealDamageToBilly(net);
                sb.append("\nBilly took ").append(net).append(" damage! His health: ")
                        .append(player.getBillyHealth()).append("/60");
                if (!player.isBillyAlive()) sb.append("\nBilly has been downed! Feed him Rotten Flesh to revive him.");
            } else {
                player.takeDamage(net);
                sb.append("\nYou took ").append(net).append(" damage! Health: ")
                        .append(player.getHealth()).append("/100");
            }
        }
        return sb.toString().trim();
    }


}