import java.util.ArrayList;

public class Room {

    private int    roomNumber;
    private String name;
    private String description;
    private boolean visited;
    private boolean searched;

    private int north;
    private int east;
    private int south;
    private int west;

    private ArrayList<Item>    items;
    private ArrayList<Monster> monsters;
    private Puzzle puzzle;
    private boolean billyPresent;
    private String examineText;

    // -------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------

    public Room(int roomNumber, String name, String description,
                int north, int east, int south, int west, String examineText) {
        this.roomNumber   = roomNumber;
        this.name         = name;
        this.description  = description;
        this.north        = north;
        this.east         = east;
        this.south        = south;
        this.west         = west;
        this.visited      = false;
        this.searched     = false;
        this.items        = new ArrayList<>();
        this.monsters     = new ArrayList<>();
        this.puzzle       = null;
        this.billyPresent = false;
        this.examineText   = (examineText != null) ? examineText : "";
    }

    // -------------------------------------------------------
    // ROOM INFO
    // -------------------------------------------------------

    public int    getRoomNumber()  { return roomNumber; }
    public String getName()        { return name; }
    public String getDescription() { return description; }

    // -------------------------------------------------------
    // VISITED
    // -------------------------------------------------------

    public boolean isVisited()    { return visited; }
    public void setVisitedTrue()  { visited = true; }

    // -------------------------------------------------------
    // SEARCHED
    // -------------------------------------------------------

    public boolean isSearched() { return searched; }

    /** Returns true only on the first search. */
    public boolean search() {
        boolean firstTime = !searched;
        searched = true;
        return firstTime;
    }

    // -------------------------------------------------------
    // MOVEMENT
    // -------------------------------------------------------

    public int getNextRoomNumber(String direction) {
        switch (direction.toUpperCase()) {
            case "N": return north;
            case "E": return east;
            case "S": return south;
            case "W": return west;
            default:  return 0;
        }
    }

    // -------------------------------------------------------
    // ITEMS
    // -------------------------------------------------------

    public void addItem(Item item)     { items.add(item); }

    public Item removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(itemName)) {
                return items.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getItems() { return items; }

    public boolean hasItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) return true;
        }
        return false;
    }

    // -------------------------------------------------------
    // MONSTERS
    // -------------------------------------------------------

    public void addMonster(Monster monster)    { monsters.add(monster); }
    public void removeMonster(Monster monster) { monsters.remove(monster); }
    public ArrayList<Monster> getMonsters()    { return monsters; }
    public boolean hasMonsters()               { return !monsters.isEmpty(); }

    // -------------------------------------------------------
    // PUZZLE
    // -------------------------------------------------------

    public void   setPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }
    public Puzzle getPuzzle()              { return puzzle; }
    public boolean hasPuzzle()             { return puzzle != null; }

    // -------------------------------------------------------
    // BILLY
    // -------------------------------------------------------

    public boolean isBillyPresent()             { return billyPresent; }
    public void    setBillyPresent(boolean b)   { this.billyPresent = b; }
    // -------------------------------------------------------
    // DISPLAY — Room describes itself, Main just prints
    // -------------------------------------------------------

    public String getExamineText() { return examineText; }

    public String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------------------------------------------\n");
        sb.append("Room ").append(roomNumber).append(" - ").append(name).append("\n");
        sb.append("----------------------------------------------\n");
        sb.append(description);
        if (visited) sb.append("\n(You have been here before.)");
        return sb.toString();
    }

    public String getExitsString() {
        StringBuilder sb = new StringBuilder("Exits: ");
        int n = getNextRoomNumber("N"), e = getNextRoomNumber("E");
        int s = getNextRoomNumber("S"), w = getNextRoomNumber("W");
        if (n != 0) sb.append("N->").append(n).append(" ");
        if (e != 0) sb.append("E->").append(e).append(" ");
        if (s != 0) sb.append("S->").append(s).append(" ");
        if (w != 0) sb.append("W->").append(w).append(" ");
        String result = sb.toString().trim();
        return result.equals("Exits:") ? "Exits: None" : result;
    }

    public String explore() {
        if (items.isEmpty()) return "There are no items here.";
        StringBuilder sb = new StringBuilder("You see:\n");
        for (Item item : items) {
            sb.append("  - ").append(item.getName())
                    .append(" [").append(item.getItemType()).append("]\n");
        }
        return sb.toString().trim();
    }

    public String getMonstersWarning() {
        boolean hasLiving = false;
        for (Monster m : monsters) { if (m.isAlive()) { hasLiving = true; break; } }
        if (!hasLiving) return "";
        StringBuilder sb = new StringBuilder("WARNING: There are enemies in this room!\n");
        for (Monster m : monsters) {
            if (m.isAlive()) sb.append("  * ").append(m).append("\n");
        }
        return sb.toString().trim();
    }

    public String doSearch() {
        boolean firstTime = !searched;
        searched = true;
        StringBuilder sb = new StringBuilder();
        if (firstTime) {
            sb.append("You look around carefully...\n");
            if (!examineText.isEmpty()) sb.append(examineText).append("\n");
            if (puzzle != null && !puzzle.isSolved())
                sb.append(puzzle.getSearchText()).append("\n");
            sb.append(explore()).append("\n");
            sb.append("Use PICKUP <item> to take something, or EXAMINE <object> for puzzles.");
        } else {
            sb.append("You have already searched this room.\n");
            sb.append(explore());
        }
        return sb.toString().trim();
    }

    public String examineObject(String target) {
        if (puzzle == null) return "There is nothing like that here.";
        return puzzle.examine(target);
    }

    public String usePuzzleItem(String itemName, String target, Player player) {
        if (puzzle == null) return "There is nothing to use that on here.";
        return puzzle.useItem(itemName, target, player);
    }

    public String solvePuzzle(String answer, Player player) {
        if (puzzle == null) return "There is no puzzle here to solve.";
        return puzzle.solve(answer, player);
    }

    public String guessPuzzle(String guessStr, Player player) {
        if (puzzle == null) return "There is no puzzle here.";
        if (puzzle instanceof AnswerPuzzle) return ((AnswerPuzzle) puzzle).guess(guessStr, player);
        return "That puzzle is not solved by guessing a number.";
    }

    public boolean hasLivingMonsters() {
        for (Monster m : monsters) { if (m.isAlive()) return true; }
        return false;
    }


    // -------------------------------------------------------
    // COMBAT — Room coordinates the fight between its monsters
    // and the player. All damage logic is in Monster/Player.
    // -------------------------------------------------------

    public String doAttack(Player player) {
        Monster target = null;
        for (Monster m : monsters) { if (m.isAlive()) { target = m; break; } }
        if (target == null) {
            return monsters.isEmpty()
                    ? "There is nothing to attack here."
                    : "All enemies are dead. Type LOOT to search the bodies.";
        }

        StringBuilder sb = new StringBuilder();

        // Player attacks
        Item weapon = player.getEquippedWeapon();
        int playerDamage = (weapon != null) ? 0 : 10;
        if (weapon != null) {
            int roll = (int)(Math.random() * 100) + 1;
            if (roll <= weapon.getHitPercentage()) {
                playerDamage = weapon.getDamage();
                if (weapon.getAmmoCapacity() > 0) {
                    weapon.setAmmoCapacity(weapon.getAmmoCapacity() - 1);
                    sb.append("  [Ammo remaining: ").append(weapon.getAmmoCapacity()).append("]\n");
                    if (weapon.getAmmoCapacity() == 0) {
                        sb.append(weapon.getName()).append(" is out of ammo! Type RELOAD to reload or UNEQUIP to switch weapons.");
                    }
                }
                sb.append("You hit ").append(target.getName()).append(" with ").append(weapon.getName())
                        .append(" for ").append(playerDamage).append(" damage!");
            } else {
                if (weapon.getMaxAmmo() > 0) {
                    sb.append("You fire ").append(weapon.getName()).append(" but miss!");
                } else {
                    sb.append("You swing ").append(weapon.getName()).append(" but miss!");
                }
            }
        } else {
            sb.append("You hit ").append(target.getName())
                    .append(" with your bare hands for ").append(playerDamage).append(" damage!");
        }
        if (playerDamage > 0) sb.append("\n").append(target.takeDamage(playerDamage));

        // Billy attacks
        if (player.isBillyCompanion() && player.isBillyAlive()) {
            for (Monster m : monsters) {
                if (m.isAlive()) {
                    sb.append("\nBilly attacks ").append(m.getName()).append(" for 8 damage!\n");
                    sb.append(m.takeDamage(8));
                    break;
                }
            }
        }

        // Monsters retaliate
        for (Monster m : monsters) {
            if (!m.isAlive()) continue;
            if (m.shouldShriek()) {
                m.resetShriekCounter();
                addMonster(Monster.createNormalZombie());
                sb.append("\nThe Screamer shrieks! A new zombie shambles in!");
            }
            String result = m.attackPlayer(player);
            if (!result.isEmpty()) sb.append("\n").append(result);
        }

        return sb.toString().trim();
    }

    public String doLoot(Player player) {
        StringBuilder sb = new StringBuilder();
        boolean any = false;
        for (Monster m : monsters) {
            if (!m.isAlive()) {
                sb.append(m.loot(player.getInventory())).append("\n");
                any = true;
            }
        }
        return any ? sb.toString().trim() : "There are no defeated enemies here to loot.";
    }


}