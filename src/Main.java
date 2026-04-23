import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // HashMap stores all rooms — key = room number, value = Room object
        HashMap<Integer, Room> roomsMap = new HashMap<>();

        // Load rooms, items, puzzles, and monsters
        loadRooms("src/Rooms.txt", roomsMap);
        loadItems("src/item.txt",  roomsMap);
        loadPuzzles(roomsMap);
        loadMonsters("src/monsters.txt", roomsMap);

        // Billy starts in Room 7 (Cells)
        if (roomsMap.containsKey(7)) {
            roomsMap.get(7).setBillyPresent(true);
        }

        if (roomsMap.isEmpty()) {
            System.out.println("No rooms loaded. Check that Rooms.txt is in the project folder.");
            return;
        }

        // Create player starting in Room 2 (wakes up in Dunkin Donuts after the dream)
        Player player = new Player(2);

        // Map window disabled until dunkin_map.png is added to src/image/
        // Uncomment these two lines and remove the null line once the image is ready:
        // GameMapWindow mapWindow = new GameMapWindow();
        // mapWindow.showMap();
        GameMapWindow mapWindow = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("          WELCOME TO DUNKIN CITY              ");
        System.out.println("    A zombie apocalypse in The Bronx, NY      ");
        System.out.println("==============================================");
        System.out.println("Type HELP at any time to see all commands.\n");

        // ---------------------------------------------------------------
        // MAIN GAME LOOP
        // ---------------------------------------------------------------
        while (true) {

            Room currentRoom = roomsMap.get(player.getCurrentRoomNumber());
            if (currentRoom == null) {
                System.out.println("Error: room not found. Returning to start.");
                player.setCurrentRoomNumber(2);
                continue;
            }

            // Print room header
            System.out.println("\n----------------------------------------------");
            System.out.println(currentRoom.getName());
            System.out.println("----------------------------------------------");
            System.out.println(currentRoom.getDescription());

            if (currentRoom.isVisited()) {
                System.out.println("(You have been here before.)");
            } else {
                currentRoom.setVisitedTrue();
            }

            System.out.println("Health: " + player.getHealth() + "/100");

            if (player.isBillyCompanion()) {
                System.out.println("Billy is with you. His health: " + player.getBillyHealth() + "/60");
            }

            if (currentRoom.hasMonsters()) {
                System.out.println("WARNING: There are enemies in this room!");
                for (Monster m : currentRoom.getMonsters()) {
                    if (m.isAlive()) {
                        System.out.println("  * " + m);
                    }
                }
            }

            System.out.print("\nEnter command (HELP for full list): ");
            String rawInput = scanner.nextLine().trim();
            String input    = rawInput.toUpperCase();

            // ---------------------------------------------------------------
            // QUIT
            // ---------------------------------------------------------------
            if (input.equals("Q") || input.equals("QUIT")) {
                System.out.println("Thanks for playing Dunkin City. Stay safe out there.");
                break;
            }

            // ---------------------------------------------------------------
            // HELP
            // ---------------------------------------------------------------
            if (input.equals("HELP")) {
                System.out.println("\n--- COMMANDS ---");
                System.out.println("N / E / S / W          -- move in that direction");
                System.out.println("SEARCH                 -- search the room for hidden items/puzzles");
                System.out.println("EXPLORE                -- list items visible in this room");
                System.out.println("PICKUP <item>          -- pick up an item from the room");
                System.out.println("DROP <item>            -- drop an item from your inventory");
                System.out.println("INVENTORY              -- show items you are carrying");
                System.out.println("EQUIPMENT              -- show your equipped gear");
                System.out.println("INSPECT <item>         -- read the description of an item");
                System.out.println("EQUIP <item>           -- equip a weapon or armor from inventory");
                System.out.println("USE <item> ON <target> -- use an item on a puzzle object");
                System.out.println("SOLVE <answer>         -- answer a riddle/code puzzle");
                System.out.println("GUESS <number>         -- guess a number for a range puzzle");
                System.out.println("EXAMINE <object>       -- examine a puzzle object in the room");
                System.out.println("LOOT                   -- loot a defeated enemy");
                System.out.println("ATTACK                 -- attack an enemy in this room");
                System.out.println("FEED BILLY             -- feed Billy rotten flesh to tame him");
                System.out.println("STATUS                 -- check your health and status");
                System.out.println("MAP                    -- toggle the game map window");
                System.out.println("Q                      -- quit the game");
                continue;
            }

            // ---------------------------------------------------------------
            // STATUS
            // ---------------------------------------------------------------
            if (input.equals("STATUS")) {
                System.out.println("Health: " + player.getHealth() + "/100");
                if (player.isBillyCompanion()) {
                    System.out.println("Billy's health: " + player.getBillyHealth() + "/60");
                }
                System.out.println(player.getStatusMessage());
                continue;
            }

            // ---------------------------------------------------------------
            // MAP — toggle map window
            // ---------------------------------------------------------------
            if (input.equals("MAP")) {
                if (mapWindow == null) {
                    System.out.println("Map is not available yet.");
                } else if (mapWindow.isVisible()) {
                    mapWindow.hideMap();
                    System.out.println("Map closed.");
                } else {
                    mapWindow.showMap();
                    System.out.println("Map opened.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // SEARCH
            // ---------------------------------------------------------------
            if (input.equals("SEARCH")) {
                boolean firstTime = currentRoom.search();
                if (firstTime) {
                    System.out.println("You look around carefully...");
                    // Show puzzle search text if room has an unsolved puzzle
                    if (currentRoom.hasPuzzle() && !currentRoom.getPuzzle().isSolved()) {
                        System.out.println(currentRoom.getPuzzle().getSearchText());
                    }
                    System.out.println("Type EXPLORE to see items, or EXAMINE <object> for puzzles.");
                } else {
                    System.out.println("You have already searched this room.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EXPLORE
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
            // PICKUP
            // ---------------------------------------------------------------
            if (input.startsWith("PICKUP ") && rawInput.length() > 7) {
                String itemName = rawInput.substring(7).trim();
                Item picked = currentRoom.removeItem(itemName);
                if (picked != null) {
                    picked.pickUp();
                    player.addItem(picked);
                    System.out.println(picked.getName() + " added to inventory.");
                } else {
                    System.out.println("That item is not in this room.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // DROP
            // ---------------------------------------------------------------
            if (input.startsWith("DROP ") && rawInput.length() > 5) {
                String itemName = rawInput.substring(5).trim();
                Item dropped = player.removeItem(itemName);
                if (dropped != null) {
                    dropped.drop(currentRoom.getRoomNumber());
                    currentRoom.addItem(dropped);
                    System.out.println(dropped.getName() + " dropped in " + currentRoom.getName() + ".");
                } else {
                    System.out.println("You do not have that item.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // INVENTORY
            // ---------------------------------------------------------------
            if (input.equals("INVENTORY")) {
                if (player.getInventory().isEmpty()) {
                    System.out.println("Your inventory is empty.");
                } else {
                    System.out.println("You are carrying:");
                    for (Item item : player.getInventory()) {
                        System.out.println("  - " + item.getName()
                                + (item.isEquipped() ? " [equipped]" : ""));
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EQUIPMENT
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
            // EQUIP <item>
            // ---------------------------------------------------------------
            if (input.startsWith("EQUIP ") && rawInput.length() > 6) {
                String itemName = rawInput.substring(6).trim();
                if (!player.hasItem(itemName)) {
                    System.out.println("You do not have that item.");
                } else {
                    Item toEquip = null;
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            toEquip = item;
                            break;
                        }
                    }
                    if (toEquip != null && toEquip.isEquippable()) {
                        toEquip.equip();
                        player.removeItem(itemName);
                        player.addEquipment(toEquip);
                        System.out.println("You equipped " + toEquip.getName() + ".");
                    } else {
                        System.out.println("That item cannot be equipped.");
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // INSPECT <item>
            // ---------------------------------------------------------------
            if (input.startsWith("INSPECT ") && rawInput.length() > 8) {
                String itemName = rawInput.substring(8).trim();
                boolean found = false;
                for (Item item : player.getInventory()) {
                    if (item.getName().equalsIgnoreCase(itemName)) {
                        item.itemDetails();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (Item item : player.getEquipment()) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            item.itemDetails();
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) System.out.println("You do not have that item.");
                continue;
            }

            // ---------------------------------------------------------------
            // EXAMINE <object> — look at a puzzle object in the room
            // ---------------------------------------------------------------
            if (input.startsWith("EXAMINE ") && rawInput.length() > 8) {
                String target = rawInput.substring(8).trim();
                if (currentRoom.hasPuzzle()) {
                    System.out.println(currentRoom.getPuzzle().examine(target));
                } else {
                    System.out.println("There is nothing like that here.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // USE <item> ON <target> — UseItemPuzzle interaction
            // ---------------------------------------------------------------
            if (input.startsWith("USE ") && input.contains(" ON ")) {
                int onIndex      = input.indexOf(" ON ");
                String itemName  = rawInput.substring(4, rawInput.toUpperCase().indexOf(" ON ")).trim();
                String target    = rawInput.substring(rawInput.toUpperCase().indexOf(" ON ") + 4).trim();

                if (!currentRoom.hasPuzzle()) {
                    System.out.println("There is nothing to use that on here.");
                } else {
                    Puzzle p = currentRoom.getPuzzle();
                    System.out.println(p.useItem(itemName, target, player));
                    if (player.isDead()) {
                        System.out.println("You have died. Game over.");
                        scanner.close();
                        return;
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // SOLVE <answer> — AnswerPuzzle text answer
            // ---------------------------------------------------------------
            if (input.startsWith("SOLVE ") && rawInput.length() > 6) {
                String answer = rawInput.substring(6).trim();
                if (!currentRoom.hasPuzzle()) {
                    System.out.println("There is no puzzle here to solve.");
                } else {
                    Puzzle p = currentRoom.getPuzzle();
                    System.out.println(p.solve(answer, player));
                    if (player.isDead()) {
                        System.out.println("You have died. Game over.");
                        scanner.close();
                        return;
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // GUESS <number> — AnswerPuzzle number-range guess
            // ---------------------------------------------------------------
            if (input.startsWith("GUESS ") && rawInput.length() > 6) {
                String guessStr = rawInput.substring(6).trim();
                if (!currentRoom.hasPuzzle()) {
                    System.out.println("There is no puzzle here to guess.");
                } else {
                    Puzzle p = currentRoom.getPuzzle();
                    if (p instanceof AnswerPuzzle) {
                        System.out.println(((AnswerPuzzle) p).guess(guessStr, player));
                    } else {
                        System.out.println("That puzzle is not solved by guessing a number.");
                    }
                    if (player.isDead()) {
                        System.out.println("You have died. Game over.");
                        scanner.close();
                        return;
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // FEED BILLY
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
            // LOOT — loot a defeated enemy
            // ---------------------------------------------------------------
            if (input.equals("LOOT")) {
                boolean looted = false;
                for (Monster m : currentRoom.getMonsters()) {
                    if (!m.isAlive()) {
                        System.out.println(m.loot(player.getInventory()));
                        looted = true;
                    }
                }
                if (!looted) {
                    System.out.println("There are no defeated enemies here to loot.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // ATTACK — combat with monsters in the room
            // ---------------------------------------------------------------
            if (input.equals("ATTACK") || input.startsWith("ATTACK ")) {
                if (!currentRoom.hasMonsters()) {
                    System.out.println("There is nothing to attack here.");
                    continue;
                }

                // Find first living monster
                Monster target = null;
                for (Monster m : currentRoom.getMonsters()) {
                    if (m.isAlive()) { target = m; break; }
                }

                if (target == null) {
                    System.out.println("All enemies here are already dead. Type LOOT to search the bodies.");
                    continue;
                }

                // --- Player attacks monster ---
                // Use equipped weapon if available, otherwise bare hands
                int playerDamage = 10; // bare-hands base damage
                Item weapon = null;
                for (Item eq : player.getEquipment()) {
                    if ("weapon".equals(eq.getItemType())) { weapon = eq; break; }
                }
                if (weapon != null) {
                    // Apply weapon hit chance
                    int roll = (int)(Math.random() * 100) + 1;
                    if (roll <= weapon.getHitPercentage()) {
                        playerDamage = weapon.getDamage();
                        // Consume ammo if applicable
                        if (weapon.getAmmoCapacity() > 0) {
                            weapon.setAmmoCapacity(weapon.getAmmoCapacity() - 1);
                            if (weapon.getAmmoCapacity() == 0) {
                                System.out.println(weapon.getName() + " is out of ammo!");
                                player.removeEquipment(weapon.getName());
                            }
                        }
                        System.out.println("You hit " + target.getName() + " with " + weapon.getName()
                                + " for " + playerDamage + " damage!");
                    } else {
                        playerDamage = 0;
                        System.out.println("You swing your " + weapon.getName() + " but miss!");
                    }
                } else {
                    System.out.println("You hit " + target.getName() + " with your bare hands for " + playerDamage + " damage!");
                }

                if (playerDamage > 0) {
                    System.out.println(target.takeDamage(playerDamage));
                }

                // --- Billy attacks if companion ---
                if (player.isBillyCompanion()) {
                    // Find a living enemy for Billy to attack (may differ from player's target)
                    Monster billyTarget = null;
                    for (Monster m : currentRoom.getMonsters()) {
                        if (m.isAlive()) { billyTarget = m; break; }
                    }
                    if (billyTarget != null) {
                        // Billy uses the monster Billy object's companion attack; we simulate it here
                        System.out.println("Billy lets out a low growl and attacks " + billyTarget.getName() + " for 8 damage!");
                        System.out.println(billyTarget.takeDamage(8));
                    }
                }

                // Remove dead monsters from the room
                List<Monster> toRemove = new ArrayList<>();
                for (Monster m : currentRoom.getMonsters()) {
                    if (!m.isAlive()) toRemove.add(m);
                }
                // We leave dead monsters in the room so the player can LOOT them,
                // but we won't count them in hasMonsters() for warnings.
                // (hasMonsters checks isEmpty — we filter alive monsters for warnings above.)

                // --- Monster attacks back ---
                for (Monster m : currentRoom.getMonsters()) {
                    if (!m.isAlive()) continue;

                    // Screamer tick
                    m.incrementTurnsInRoom();
                    if (m.shouldShriek()) {
                        m.resetShriekCounter();
                        Monster summoned = Monster.createNormalZombie();
                        currentRoom.addMonster(summoned);
                        System.out.println("The Screamer lets out a horrifying shriek! A new zombie shambles in!");
                    }

                    String[] attackResult = m.performAttack(player.isBillyCompanion() && player.isBillyAlive());
                    System.out.println(attackResult[0]);

                    int incomingDamage = Integer.parseInt(attackResult[1]);
                    if (incomingDamage > 0) {
                        // Reduce by armor defense
                        int totalDefense = 0;
                        for (Item eq : player.getEquipment()) {
                            if ("armor".equals(eq.getItemType())) {
                                totalDefense += eq.getDefense();
                            }
                        }
                        int netDamage = Math.max(0, incomingDamage - totalDefense);

                        if ("BILLY".equals(attackResult[2])) {
                            player.dealDamageToBilly(netDamage);
                            System.out.println("Billy took " + netDamage + " damage! His health: " + player.getBillyHealth() + "/60");
                            if (!player.isBillyAlive()) {
                                System.out.println("Billy has fallen. He is gone.");
                            }
                        } else {
                            player.takeDamage(netDamage);
                            System.out.println("You took " + netDamage + " damage! Your health: " + player.getHealth() + "/100");
                        }

                        if (player.isDead()) {
                            System.out.println("\nYou have died. Game over.");
                            scanner.close();
                            return;
                        }
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // MOVEMENT — N / E / S / W (also accepts North, East, South, West)
            // ---------------------------------------------------------------
            String moveInput = input;
            if (moveInput.length() > 1) moveInput = String.valueOf(moveInput.charAt(0));

            if (moveInput.equals("N") || moveInput.equals("E") ||
                    moveInput.equals("S") || moveInput.equals("W")) {

                int nextRoomNum = currentRoom.getNextRoomNumber(moveInput);

                if (nextRoomNum == 0) {
                    System.out.println("You cannot go that way.");
                } else if (!roomsMap.containsKey(nextRoomNum)) {
                    System.out.println("That path leads nowhere.");
                } else {
                    player.setCurrentRoomNumber(nextRoomNum);
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
    // loadRooms — reads Rooms.txt
    // format: roomNumber,name,description,north,east,south,west
    // ---------------------------------------------------------------
    private static void loadRooms(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split on first 7 commas only (description may contain commas)
                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue;

                int    number = Integer.parseInt(parts[0].trim());
                String name   = parts[1].trim();
                String desc   = parts[2].trim();
                int    north  = Integer.parseInt(parts[3].trim());
                int    east   = Integer.parseInt(parts[4].trim());
                int    south  = Integer.parseInt(parts[5].trim());
                int    west   = Integer.parseInt(parts[6].trim());

                roomsMap.put(number, new Room(number, name, desc, north, east, south, west));
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number format error in: " + filename);
        }
    }

    // ---------------------------------------------------------------
    // loadItems — reads item.txt
    // format: id,name,description,location,type,damage,hitPct,defense,hpBoost,scrapVal,ammo[,compatibleIDs]
    // ---------------------------------------------------------------
    private static void loadItems(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 12);
                if (parts.length < 11) continue;

                int    itemID         = Integer.parseInt(parts[0].trim());
                String name           = parts[1].trim();
                String desc           = parts[2].trim();
                int    location       = Integer.parseInt(parts[3].trim());
                String type           = parts[4].trim();
                int    damage         = Integer.parseInt(parts[5].trim());
                int    hitPct         = Integer.parseInt(parts[6].trim());
                int    defense        = Integer.parseInt(parts[7].trim());
                int    hpBoost        = Integer.parseInt(parts[8].trim());
                int    scrapVal       = Integer.parseInt(parts[9].trim());
                int    ammo           = Integer.parseInt(parts[10].trim());

                ArrayList<Integer> compatIDs = new ArrayList<>();
                if (parts.length >= 12) {
                    for (String id : parts[11].trim().split("\\|")) {
                        try { compatIDs.add(Integer.parseInt(id.trim())); }
                        catch (NumberFormatException ignored) {}
                    }
                }

                Item item = new Item(itemID, name, desc, location, type, damage, hitPct, defense, hpBoost, scrapVal, ammo, compatIDs);

                // location 0 means "starts nowhere" (available via puzzle/loot only)
                if (location > 0 && roomsMap.containsKey(location)) {
                    roomsMap.get(location).addItem(item);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number format error in: " + filename);
        }
    }

    // ---------------------------------------------------------------
    // loadPuzzles — assigns puzzles from PuzzleRegistry to rooms
    // Mapping: puzzle objectName -> room number
    // ---------------------------------------------------------------
    private static void loadPuzzles(HashMap<Integer, Room> roomsMap) {
        // Map each puzzle to its room number
        int[][] puzzleRoomMap = {
                // index corresponds to order in PuzzleRegistry.createAllPuzzles()
                // Room assignments based on Rooms.txt lore
        };

        // Assign puzzles by room using known lore mappings
        assignPuzzle(roomsMap, "FR#01PZ", 14);  // Scooter        — Main Street
        assignPuzzle(roomsMap, "FR#02PZ", 27);  // Backroom door  — Backroom
        assignPuzzle(roomsMap, "FR#03PZ", 10);  // Vending machine — Motel
        assignPuzzle(roomsMap, "FR#04PZ",  6);  // Police locker  — Police Station
        assignPuzzle(roomsMap, "FR#05PZ", 30);  // Medical cabinet — Medical Facility
        assignPuzzle(roomsMap, "FR#06PZ",  5);  // Monkey cage    — Zoo
        assignPuzzle(roomsMap, "FR#07PZ", 29);  // Bank vault     — Bank
        assignPuzzle(roomsMap, "FR#08PZ", 10);  // Motel keys     — Motel (second puzzle skipped if room already has one)
        assignPuzzle(roomsMap, "FR#09PZ", 24);  // Toy chest      — Childcare Center
    }

    private static void assignPuzzle(HashMap<Integer, Room> roomsMap, String puzzleId, int roomNumber) {
        if (!roomsMap.containsKey(roomNumber)) return;
        Room room = roomsMap.get(roomNumber);
        if (room.hasPuzzle()) return; // don't overwrite an existing puzzle
        Puzzle puzzle = PuzzleRegistry.getPuzzleById(puzzleId);
        if (puzzle != null) {
            room.setPuzzle(puzzle);
        }
    }

    // ---------------------------------------------------------------
    // loadMonsters — reads monsters.txt
    // format: roomNumber,monsterType
    // monsterType must exactly match one of Monster's TYPE_ constants
    // e.g. "Normal Zombie", "Crawler", "CIA Officer", "Zombie Billy"
    // ---------------------------------------------------------------
    private static void loadMonsters(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                int    roomNumber  = Integer.parseInt(parts[0].trim());
                String monsterType = parts[1].trim();

                if (!roomsMap.containsKey(roomNumber)) continue;

                Monster monster = createMonsterByType(monsterType);
                if (monster == null) {
                    System.out.println("Warning: unknown monster type \"" + monsterType + "\" in " + filename);
                    continue;
                }

                roomsMap.get(roomNumber).addMonster(monster);
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filename);
        } catch (NumberFormatException e) {
            System.out.println("Number format error in: " + filename);
        }
    }

    // Maps the type string from monsters.txt to the correct factory method.
    // Add a new case here whenever a new monster type is added to Monster.java.
    private static Monster createMonsterByType(String monsterType) {
        switch (monsterType) {
            case Monster.TYPE_CRAWLER:           return Monster.createCrawler();
            case Monster.TYPE_SPITTER:           return Monster.createSpitter();
            case Monster.TYPE_NORMAL_ZOMBIE:     return Monster.createNormalZombie();
            case Monster.TYPE_SCREAMER:          return Monster.createScreamer();
            case Monster.TYPE_LA_BRONX_GANGSTER: return Monster.createLaBronxGangster();
            case Monster.TYPE_CIA_OFFICER:       return Monster.createCIAOfficer();
            case Monster.TYPE_INFECTED_CIA:      return Monster.createInfectedCIA();
            case Monster.TYPE_BILLY:             return Monster.createBilly();
            default:                             return null;
        }
    }
}