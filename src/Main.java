import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // HashMap stores all rooms loaded from rooms.txt
        // Key = room number, Value = Room object
        HashMap<Integer, Room> roomsMap = new HashMap<>();

        // load all rooms from file -- same method as Willy Wonka, zero changes
        loadRooms("rooms.txt", roomsMap);

        // load items into rooms from file -- same method as Willy Wonka
        loadItems("Items.txt", roomsMap);

        // load puzzles into rooms from file -- same method as Willy Wonka
        loadPuzzles("Puzzles.txt", roomsMap);

        // load monsters into rooms from file
        // YOUR TEAMMATE (Monster class owner) fills in the Monster class
        // and writes a monsters.txt file in the same pattern as Items.txt
        // format: monsterName,monsterType,health,roomNumber
        // loadMonsters("Monsters.txt", roomsMap);  <-- uncomment when Monster.java exists

        // set up Billy in Room 7 so the room knows he is there
        if (roomsMap.containsKey(7)) {
            roomsMap.get(7).setBillyPresent(true);
        }

        // stop if no rooms loaded
        if (roomsMap.isEmpty()) {
            System.out.println("No rooms loaded. Check that rooms.txt is in the project folder.");
            return;
        }

        // create the player starting in Room 1 (Dream State intro)
        Player player = new Player(1);

        // scanner reads everything the player types
        Scanner scanner = new Scanner(System.in);

        // print the game title when it starts
        System.out.println("==============================================");
        System.out.println("          WELCOME TO DUNKIN CITY              ");
        System.out.println("    A zombie apocalypse in The Bronx, NY      ");
        System.out.println("==============================================");
        System.out.println("Type HELP at any time to see all commands.\n");

        // ---------------------------------------------------------------
        // MAIN GAME LOOP -- keeps running until player quits or dies
        // ---------------------------------------------------------------
        while (true) {

            // get the current room object from the map
            Room currentRoom = roomsMap.get(player.getCurrentRoomNumber());

            // safety check -- should never happen but prevents a crash
            if (currentRoom == null) {
                System.out.println("Error: room not found. Returning to start.");
                player.setCurrentRoomNumber(1);
                continue;
            }

            // print room name and description every time player enters
            System.out.println("\n----------------------------------------------");
            System.out.println(currentRoom.getName());
            System.out.println("----------------------------------------------");
            System.out.println(currentRoom.getDescription());

            // tell player if they have been here before
            if (currentRoom.isVisited()) {
                System.out.println("(You have been here before.)");
            } else {
                currentRoom.setVisitedTrue();
            }

            // show health so player always knows their status
            System.out.println("Health: " + player.getHealth() + "/100");

            // show Billy's status if he is a companion
            if (player.isBillyCompanion()) {
                System.out.println("Billy is with you. His health: " + player.getBillyHealth() + "/60");
            }

            // warn player if there are monsters in the room
            // your teammate's Monster class will handle the actual combat
            if (currentRoom.hasMonsters()) {
                System.out.println("WARNING: There are enemies in this room!");
            }

            // show command prompt
            System.out.print("\nEnter command (HELP for full list): ");

            // read input and trim spaces
            String rawInput = scanner.nextLine().trim();
            String input = rawInput.toUpperCase();

            // ---------------------------------------------------------------
            // QUIT
            // ---------------------------------------------------------------
            if (input.equals("Q") || input.equals("QUIT")) {
                System.out.println("Thanks for playing Dunkin City. Stay safe out there.");
                break;
            }

            // ---------------------------------------------------------------
            // HELP -- shows all available commands (FR#05PL)
            // ---------------------------------------------------------------
            if (input.equals("HELP")) {
                System.out.println("\n--- COMMANDS ---");
                System.out.println("N / E / S / W      -- move in that direction");
                System.out.println("SEARCH             -- search the room for hidden items");
                System.out.println("EXPLORE            -- list items visible in this room");
                System.out.println("PICKUP <item>      -- pick up an item from the room");
                System.out.println("DROP <item>        -- drop an item from your inventory");
                System.out.println("INVENTORY          -- show items you are carrying");
                System.out.println("EQUIPMENT          -- show your equipped gear");
                System.out.println("INSPECT <item>     -- read the description of an item");
                System.out.println("STATUS             -- check your health and status");
                System.out.println("FEED BILLY         -- feed Billy rotten flesh to tame him");
                System.out.println("ATTACK             -- attack an enemy (when in combat)");
                System.out.println("Q                  -- quit the game");
                continue;
            }

            // ---------------------------------------------------------------
            // STATUS -- shows health and easter egg after 3 in a row (FR#02PL)
            // ---------------------------------------------------------------
            if (input.equals("STATUS")) {
                System.out.println("Health: " + player.getHealth() + "/100");
                System.out.println(player.getStatusMessage());
                continue;
            }

            // ---------------------------------------------------------------
            // SEARCH -- reveals hidden items/clues in the room (FR#01RM)
            // ---------------------------------------------------------------
            if (input.equals("SEARCH")) {
                boolean firstTime = currentRoom.search();
                if (firstTime) {
                    // print the examine text for this room
                    // your teammate (View) can move this print to View.java later
                    System.out.println("[SEARCH] You look around carefully...");
                    // items that were hidden are now visible -- EXPLORE to see them
                    System.out.println("Type EXPLORE to see what is in this room.");
                } else {
                    System.out.println("[SEARCH] You have already searched this room.");
                    System.out.println("Type EXPLORE to see what is in this room.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EXPLORE -- lists items in the room (same as Willy Wonka)
            // ---------------------------------------------------------------
            if (input.equals("EXPLORE")) {
                if (currentRoom.getItems().isEmpty()) {
                    System.out.println("There are no items here.");
                } else {
                    System.out.println("You see:");
                    for (Item item : currentRoom.getItems()) {
                        System.out.println("  - " + item.getName());
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // PICKUP -- picks up item from room into inventory (same as Willy Wonka)
            // ---------------------------------------------------------------
            if (input.startsWith("PICKUP ") && rawInput.length() > 7) {
                String itemName = rawInput.substring(7).trim();
                Item picked = currentRoom.removeItem(itemName);
                if (picked != null) {
                    player.addItem(picked);
                    System.out.println(picked.getName() + " added to inventory.");
                } else {
                    System.out.println("That item is not in this room.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // DROP -- drops item from inventory into room (same as Willy Wonka)
            // ---------------------------------------------------------------
            if (input.startsWith("DROP ") && rawInput.length() > 5) {
                String itemName = rawInput.substring(5).trim();
                Item dropped = player.removeItem(itemName);
                if (dropped != null) {
                    currentRoom.addItem(dropped);
                    System.out.println(dropped.getName() + " dropped in " + currentRoom.getName() + ".");
                } else {
                    System.out.println("You do not have that item.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // INVENTORY -- shows carried items (same as Willy Wonka)
            // ---------------------------------------------------------------
            if (input.equals("INVENTORY")) {
                if (player.getInventory().isEmpty()) {
                    System.out.println("Your inventory is empty.");
                } else {
                    System.out.println("You are carrying:");
                    for (Item item : player.getInventory()) {
                        System.out.println("  - " + item.getName());
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EQUIPMENT -- shows equipped gear (FR#04PL)
            // ---------------------------------------------------------------
            if (input.equals("EQUIPMENT")) {
                if (player.getEquipment().isEmpty()) {
                    System.out.println("You have no equipment.");
                } else {
                    System.out.println("Your equipment:");
                    for (Item item : player.getEquipment()) {
                        System.out.println("  - " + item.getName());
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // INSPECT -- shows item description (same as Willy Wonka)
            // ---------------------------------------------------------------
            if (input.startsWith("INSPECT ") && rawInput.length() > 8) {
                String itemName = rawInput.substring(8).trim();
                if (player.hasItem(itemName)) {
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            System.out.println(item.getDescription());
                            break;
                        }
                    }
                } else {
                    System.out.println("You do not have that item in your inventory.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // FEED BILLY -- tames Billy as companion in Room 7 (FR#03ZB)
            // ---------------------------------------------------------------
            if (input.equals("FEED BILLY")) {
                if (!currentRoom.isBillyPresent()) {
                    System.out.println("Billy is not here.");
                } else if (player.isBillyCompanion()) {
                    System.out.println("Billy is already by your side. He does not need convincing anymore.");
                } else if (player.hasItem("Rotten Flesh")) {
                    player.removeItem("Rotten Flesh");
                    player.tameBilly();
                    currentRoom.setBillyPresent(false);
                    System.out.println("Billy pauses, staring at the offering. Slowly he takes it from your hand.");
                    System.out.println("His eyes soften with recognition... He remembers you.");
                } else {
                    System.out.println("I do not have anything Billy would want to eat.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // ATTACK -- placeholder for your teammate's Monster/combat system
            // your teammate fills in the combat logic here
            // ---------------------------------------------------------------
            if (input.equals("ATTACK") || input.startsWith("ATTACK ")) {
                if (!currentRoom.hasMonsters()) {
                    System.out.println("There is nothing to attack here.");
                } else {
                    // TODO: your teammate's combat system goes here
                    // example of how it will connect:
                    // Monster target = (Monster) currentRoom.getMonsters().get(0);
                    // CombatController.handleCombat(player, target, scanner);
                    System.out.println("Combat system coming soon -- your teammate is building this.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // MOVEMENT -- N / E / S / W (same logic as Willy Wonka)
            // also handles if player types North, East, South, West
            // ---------------------------------------------------------------

            // trim input down to just the first letter so North = N, East = E etc
            String moveInput = input;
            if (moveInput.length() > 1) {
                moveInput = String.valueOf(moveInput.charAt(0));
            }

            if (moveInput.equals("N") || moveInput.equals("E") ||
                    moveInput.equals("S") || moveInput.equals("W")) {

                int nextRoomNum = currentRoom.getNextRoomNumber(moveInput);

                if (nextRoomNum == 0) {
                    System.out.println("You cannot go that way.");

                } else if (!roomsMap.containsKey(nextRoomNum)) {
                    System.out.println("That path leads nowhere.");

                } else {
                    // move the player
                    player.setCurrentRoomNumber(nextRoomNum);

                    // check for puzzle in the new room -- same as Willy Wonka
                    Room newRoom = roomsMap.get(player.getCurrentRoomNumber());
                    if (newRoom.hasPuzzle() && !newRoom.getPuzzle().isSolved()) {
                        Puzzle puzzle = newRoom.getPuzzle();
                        puzzle.resetAttempts();
                        System.out.println("\n[PUZZLE] " + puzzle.getDescription());

                        while (true) {
                            System.out.print("Your answer: ");
                            String answer = scanner.nextLine().trim();
                            if (puzzle.attemptAnswer(answer)) {
                                System.out.println("Correct! You solved the puzzle.");
                                break;
                            }
                            int left = puzzle.getRemainingAttempts();
                            if (left > 0) {
                                System.out.println("Wrong. " + left + " attempt(s) remaining.");
                            } else {
                                // puzzle fail -- deal 50% damage penalty per SRS
                                int penalty = player.getHealth() / 2;
                                player.takeDamage(penalty);
                                System.out.println("You failed the puzzle and took " + penalty + " damage!");
                                if (player.isDead()) {
                                    System.out.println("You have died. Game over.");
                                    scanner.close();
                                    return;
                                }
                                break;
                            }
                        }
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // UNKNOWN COMMAND
            // ---------------------------------------------------------------
            System.out.println("Unknown command. Type HELP to see all commands.");
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // loadRooms -- identical to Willy Wonka, zero changes needed
    // reads rooms.txt and puts Room objects into the HashMap
    // ---------------------------------------------------------------
    private static void loadRooms(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                int number      = Integer.parseInt(parts[0].trim());
                String name     = parts[1].trim();
                String desc     = parts[2].trim();
                int north       = Integer.parseInt(parts[3].trim());
                int east        = Integer.parseInt(parts[4].trim());
                int south       = Integer.parseInt(parts[5].trim());
                int west        = Integer.parseInt(parts[6].trim());

                roomsMap.put(number, new Room(number, name, desc, north, east, south, west));
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number error in: " + filename);
        }
    }

    // ---------------------------------------------------------------
    // loadItems -- identical to Willy Wonka, zero changes needed
    // format: itemName,description,roomNumber
    // ---------------------------------------------------------------
    private static void loadItems(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String name     = parts[0].trim();
                String desc     = parts[1].trim();
                int roomNumber  = Integer.parseInt(parts[2].trim());

                if (roomsMap.containsKey(roomNumber)) {
                    roomsMap.get(roomNumber).addItem(new Item(name, desc));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number error in: " + filename);
        }
    }

    // ---------------------------------------------------------------
    // loadPuzzles -- identical to Willy Wonka, zero changes needed
    // format: roomNumber,description,answer,maxAttempts
    // ---------------------------------------------------------------
    private static void loadPuzzles(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                int roomNumber  = Integer.parseInt(parts[0].trim());
                String desc     = parts[1].trim();
                String answer   = parts[2].trim();
                int attempts    = Integer.parseInt(parts[3].trim());

                if (roomsMap.containsKey(roomNumber)) {
                    roomsMap.get(roomNumber).setPuzzle(new Puzzle(desc, answer, attempts));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number error in: " + filename);
        }
    }

    // ---------------------------------------------------------------
    // loadMonsters -- YOUR TEAMMATE fills this in when Monster.java exists
    // format (suggested): monsterName,monsterType,health,roomNumber
    // uncomment the call to this method at the top of main() when ready
    // ---------------------------------------------------------------
    /*
    private static void loadMonsters(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String name     = parts[0].trim();
                String type     = parts[1].trim();
                int health      = Integer.parseInt(parts[2].trim());
                int roomNumber  = Integer.parseInt(parts[3].trim());

                Monster monster = new Monster(name, type, health);
                if (roomsMap.containsKey(roomNumber)) {
                    roomsMap.get(roomNumber).addMonster(monster);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number error in: " + filename);
        }
    }
    */
}