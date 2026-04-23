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

    // -------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------

    public Room(int roomNumber, String name, String description,
                int north, int east, int south, int west) {
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
}