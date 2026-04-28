import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Entry point. Loads game data from files and runs the game loop.
 * All game logic lives in Room, Player, Monster, and Puzzle classes.
 */
public class Main {

    public static void main(String[] args) {

        HashMap<Integer, Room> roomsMap = new HashMap<>();
        loadRooms("src/Rooms.txt", roomsMap);
        loadItems("src/item.txt", roomsMap);
        loadPuzzles(roomsMap);
        loadMonsters("src/Monsters.txt", roomsMap);

        if (roomsMap.containsKey(7)) roomsMap.get(7).setBillyPresent(true);

        if (roomsMap.isEmpty()) {
            System.out.println("No rooms loaded. Check that Rooms.txt is in src/.");
            return;
        }

        Player player = new Player(1);
        GameMapWindow mapWindow = initMap();
        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("          WELCOME TO DUNKIN CITY              ");
        System.out.println("    A zombie apocalypse in The Bronx, NY      ");
        System.out.println("==============================================");
        System.out.println("Type HELP at any time to see all commands.\n");

        while (true) {
            Room room = roomsMap.get(player.getCurrentRoomNumber());
            if (room == null) { player.setCurrentRoomNumber(1); continue; }

            System.out.println(room.getHeader());
            System.out.println(room.getExitsString());
            System.out.println("Health: " + player.getHealth() + "/100");
            if (player.isBillyCompanion())
                System.out.println("Billy: " + player.getBillyHealth() + "/60 HP");
            String warning = room.getMonstersWarning();
            if (!warning.isEmpty()) System.out.println(warning);

            System.out.print("\nEnter command (HELP for full list): ");
            String raw   = scanner.nextLine().trim();
            String input = raw.toUpperCase();

            if (input.equals("Q") || input.equals("QUIT")) {
                System.out.println("Thanks for playing Dunkin City. Stay safe out there.");
                break;
            }
            if (input.equals("HELP"))       { System.out.println(getHelpText()); continue; }
            if (input.equals("STATUS"))     { System.out.println(player.showStatus()); continue; }
            if (input.equals("MAP"))        { toggleMap(mapWindow); continue; }
            if (input.equals("SEARCH"))     { System.out.println(room.doSearch()); continue; }
            if (input.equals("EXPLORE"))    { System.out.println(room.explore()); continue; }
            if (input.equals("INVENTORY"))  { System.out.println(player.showInventory()); continue; }
            if (input.equals("EQUIPMENT"))  { System.out.println(player.showEquipment()); continue; }
            if (input.equals("LOOT"))       { System.out.println(room.doLoot(player)); continue; }
            if (input.equals("RELOAD") || input.startsWith("RELOAD ")) {
                String wName = input.startsWith("RELOAD ") ? raw.substring(7).trim() : "";
                System.out.println(player.reload(wName)); continue;
            }
            if (input.equals("FEED BILLY")) { System.out.println(player.feedBilly(room)); continue; }

            if (input.equals("ATTACK") || input.startsWith("ATTACK ")) {
                System.out.println(room.doAttack(player));
                if (player.isDead()) { System.out.println("\nYou have died. Game over."); break; }
                continue;
            }
            if (input.startsWith("PICKUP ") && raw.length() > 7) {
                System.out.println(player.pickup(raw.substring(7).trim(), room)); continue;
            }
            if (input.startsWith("DROP ") && raw.length() > 5) {
                System.out.println(player.drop(raw.substring(5).trim(), room)); continue;
            }
            if (input.startsWith("INSPECT ") && raw.length() > 8) {
                System.out.println(player.inspect(raw.substring(8).trim())); continue;
            }
            if (input.startsWith("EQUIP ") && raw.length() > 6) {
                System.out.println(player.equip(raw.substring(6).trim())); continue;
            }
            if (input.startsWith("UNEQUIP ") && raw.length() > 8) {
                System.out.println(player.unequip(raw.substring(8).trim())); continue;
            }
            if (input.startsWith("ATTACH ") && input.contains(" TO ")) {
                String attachName = raw.substring(7, raw.toUpperCase().indexOf(" TO ")).trim();
                String weaponName = raw.substring(raw.toUpperCase().indexOf(" TO ") + 4).trim();
                System.out.println(player.attach(attachName, weaponName)); continue;
            }
            if (input.startsWith("USE ")) {
                String afterUse = raw.substring(4).trim();
                if (input.contains(" ON ")) {
                    String itemName = afterUse.substring(0, afterUse.toUpperCase().indexOf(" ON ")).trim();
                    String target   = afterUse.substring(afterUse.toUpperCase().indexOf(" ON ") + 4).trim();
                    System.out.println(room.usePuzzleItem(itemName, target, player));
                } else {
                    System.out.println(player.useConsumable(afterUse));
                }
                if (player.isDead()) { System.out.println("You have died. Game over."); break; }
                continue;
            }
            if (input.startsWith("EXAMINE ") && raw.length() > 8) {
                System.out.println(room.examineObject(raw.substring(8).trim())); continue;
            }
            if (input.startsWith("SOLVE ") && raw.length() > 6) {
                System.out.println(room.solvePuzzle(raw.substring(6).trim(), player));
                if (player.isDead()) { System.out.println("You have died. Game over."); break; }
                continue;
            }
            if (input.startsWith("GUESS ") && raw.length() > 6) {
                System.out.println(room.guessPuzzle(raw.substring(6).trim(), player));
                if (player.isDead()) { System.out.println("You have died. Game over."); break; }
                continue;
            }

            // Movement
            String dir = input.length() > 1 ? String.valueOf(input.charAt(0)) : input;
            if (dir.equals("N") || dir.equals("E") || dir.equals("S") || dir.equals("W")) {
                int next = room.getNextRoomNumber(dir);
                if (next == 0)                         System.out.println("You cannot go that way.");
                else if (!roomsMap.containsKey(next))  System.out.println("That path leads nowhere.");
                else                                   player.setCurrentRoomNumber(next);
                continue;
            }

            System.out.println("Unknown command. Type HELP to see all commands.");
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // FILE LOADERS
    // ---------------------------------------------------------------

    private static void loadRooms(String filename, HashMap<Integer, Room> map) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",", 8);
                if (p.length < 7) continue;
                int    id   = Integer.parseInt(p[0].trim());
                String exam = p.length >= 8 ? p[7].trim() : "";
                map.put(id, new Room(id, p[1].trim(), p[2].trim(),
                        Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()),
                        Integer.parseInt(p[5].trim()), Integer.parseInt(p[6].trim()), exam));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }
    }

    private static void loadItems(String filename, HashMap<Integer, Room> map) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",", 12);
                if (p.length < 11) continue;
                int    loc  = Integer.parseInt(p[3].trim());
                int    ammo = Integer.parseInt(p[10].trim());
                ArrayList<Integer> compat = new ArrayList<>();
                if (p.length >= 12)
                    for (String id : p[11].trim().split("\\|"))
                        try { compat.add(Integer.parseInt(id.trim())); } catch (NumberFormatException ignored) {}
                Item item = new Item(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(), loc,
                        p[4].trim(), Integer.parseInt(p[5].trim()), Integer.parseInt(p[6].trim()),
                        Integer.parseInt(p[7].trim()), Integer.parseInt(p[8].trim()),
                        Integer.parseInt(p[9].trim()), ammo, compat);
                if (loc > 0 && map.containsKey(loc)) map.get(loc).addItem(item);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading items: " + e.getMessage());
        }
    }

    private static void loadPuzzles(HashMap<Integer, Room> map) {
        int[][] assignments = { {8,1},{17,2},{13,3},{6,4},{12,5},{5,6},{29,7},{10,8},{24,9} };
        for (int[] a : assignments) {
            if (!map.containsKey(a[0])) continue;
            Room room = map.get(a[0]);
            if (room.hasPuzzle()) continue;
            Puzzle p = PuzzleRegistry.getPuzzleById("FR#0" + a[1] + "PZ");
            if (p != null) room.setPuzzle(p);
        }
    }

    private static void loadMonsters(String filename, HashMap<Integer, Room> map) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",", 2);
                if (p.length < 2) continue;
                int     roomNum = Integer.parseInt(p[0].trim());
                String  type    = p[1].trim();
                if (!map.containsKey(roomNum)) continue;
                Monster m = Monster.createByType(type);
                if (m == null) { System.out.println("Unknown monster type: " + type); continue; }
                m.assignLootForRoom(roomNum);
                map.get(roomNum).addMonster(m);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading monsters: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // MAP / HELP
    // ---------------------------------------------------------------

    private static GameMapWindow initMap() {
        try {
            GameMapWindow w = new GameMapWindow();
            w.showMap();
            return w;
        } catch (Exception e) {
            System.out.println("(Map unavailable — add dunkin_map.png to src/image/ to enable it.)");
            return null;
        }
    }

    private static void toggleMap(GameMapWindow w) {
        if (w == null) { System.out.println("Map unavailable."); return; }
        if (w.isVisible()) { w.hideMap(); System.out.println("Map closed."); }
        else               { w.showMap(); System.out.println("Map opened."); }
    }

    private static String getHelpText() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("src/help.txt"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException e) {
            return "Help file not found. Add help.txt to src/.";
        }
        return sb.toString().trim();
    }
}