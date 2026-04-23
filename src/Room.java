import java.util.ArrayList;

public class Room {

    // ID for this room -- matches the number in rooms.txt and the HashMap key in Main
    private int roomNumber;

    // name shown when player enters the room
    private String name;

    // description shown when player enters the room
    private String description;

    // tracks if the player has visited this room before
    private boolean visited;

    // tracks if the player has used SEARCH in this room (FR#01RM)
    // hidden items/monsters are only revealed after searching
    private boolean searched;

    // exits -- each stores the room number to go to, 0 means no exit that way
    private int north;
    private int east;
    private int south;
    private int west;

    // items visible and lootable in this room
    // uses Item directly -- matches your teammates Item class
    private ArrayList<Item> items;

    // monsters present in this room
    // uses Monster directly -- matches your teammates Monster class
    // stored as Object for now so it compiles before Monster.java exists
    // your teammate just needs to call room.addMonster(monsterObject)
    private ArrayList<Object> monsters;

    // one puzzle per room -- null means no puzzle (matches Willy Wonka pattern)
    private Puzzle puzzle;

    // Billy companion -- only relevant in Room 7 (Cells)
    // false means Billy has not been tamed yet
    private boolean billyPresent;

    // CONSTRUCTOR
    // called by Main.loadRooms() -- identical signature to Willy Wonka
    // so loadRooms() needs zero changes to work with this class
    public Room(int roomNumber, String name, String description,
                int north, int east, int south, int west) {

        this.roomNumber  = roomNumber;
        this.name        = name;
        this.description = description;
        this.north       = north;
        this.east        = east;
        this.south       = south;
        this.west        = west;
        this.visited     = false;
        this.searched    = false;
        this.items       = new ArrayList<>();
        this.monsters    = new ArrayList<>();
        this.puzzle      = null;
        this.billyPresent = false;
    }

    // -------------------------------------------------------
    // ROOM INFO
    // -------------------------------------------------------

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // -------------------------------------------------------
    // VISITED -- same as Willy Wonka, unchanged
    // -------------------------------------------------------

    public boolean isVisited() {
        return visited;
    }

    public void setVisitedTrue() {
        visited = true;
    }

    // -------------------------------------------------------
    // SEARCHED (FR#01RM)
    // Main/Controller calls room.search() when player types SEARCH
    // returns true the first time so Controller knows to reveal hidden things
    // -------------------------------------------------------

    public boolean isSearched() {
        return searched;
    }

    // called by Controller when player uses SEARCH command
    // returns true only on the first search so Controller can reveal hidden content
    public boolean search() {
        boolean firstTime = !searched;
        searched = true;
        return firstTime;
    }

    // -------------------------------------------------------
    // MOVEMENT -- same as Willy Wonka, unchanged
    // Main uses this exact method to handle N/E/S/W input
    // -------------------------------------------------------

    public int getNextRoomNumber(String direction) {
        direction = direction.toUpperCase();
        switch (direction) {
            case "N": return north;
            case "E": return east;
            case "S": return south;
            case "W": return west;
            default:  return 0;
        }
    }

    // -------------------------------------------------------
    // ITEMS -- same as Willy Wonka so Main.loadItems() works unchanged
    // -------------------------------------------------------

    public void addItem(Item item) {
        items.add(item);
    }

    // removes and returns item by name, null if not found
    public Item removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(itemName)) {
                return items.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public boolean hasItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------
    // MONSTERS
    // Your teammate (Monster class owner) calls room.addMonster(monster)
    // from their loadMonsters() method in Main, same pattern as loadItems()
    // Using Object so this compiles before Monster.java is written
    // Once Monster.java exists, swap Object to Monster in this file only
    // -------------------------------------------------------

    // adds a monster to this room
    public void addMonster(Object monster) {
        monsters.add(monster);
    }

    // removes a specific monster from this room (called after it is defeated)
    public void removeMonster(Object monster) {
        monsters.remove(monster);
    }

    // returns all monsters currently in this room
    public ArrayList<Object> getMonsters() {
        return monsters;
    }

    // returns true if at least one monster is still in this room
    // Controller checks this to decide whether to trigger combat
    public boolean hasMonsters() {
        return !monsters.isEmpty();
    }

    // -------------------------------------------------------
    // PUZZLE -- same as Willy Wonka so Main.loadPuzzle() works unchanged
    // -------------------------------------------------------

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public boolean hasPuzzle() {
        return puzzle != null;
    }

    // -------------------------------------------------------
    // BILLY COMPANION (FR#03ZB)
    // Room 7 (Cells) starts with billyPresent = true
    // set by Main/FileStorage after loading: roomsMap.get(7).setBillyPresent(true)
    // Player.tameBilly() sets this to false and adds Billy as companion on Player
    // -------------------------------------------------------

    public boolean isBillyPresent() {
        return billyPresent;
    }

    public void setBillyPresent(boolean present) {
        this.billyPresent = present;
    }
}