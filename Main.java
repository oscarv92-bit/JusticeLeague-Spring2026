import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        HashMap<Integer, Room> roomsMap = new HashMap<>();

        loadRooms("src/Rooms.txt", roomsMap);
        loadItems("src/item.txt",  roomsMap);
        loadPuzzles(roomsMap);
        loadMonsters("src/monsters.txt", roomsMap);

        // Billy starts in Room 7 (Cells)
        if (roomsMap.containsKey(7)) {
            roomsMap.get(7).setBillyPresent(true);
        }

        if (roomsMap.isEmpty()) {
            System.out.println("No rooms loaded. Check that Rooms.txt is in src/.");
            return;
        }

        // Player starts in Room 1 (Dream State intro)
        Player player = new Player(1);

        // Map window — loads automatically if dunkin_map.png exists in src/image/
        GameMapWindow mapWindow = null;
        try {
            mapWindow = new GameMapWindow();
            mapWindow.showMap();
        } catch (Exception e) {
            System.out.println("(Map image not found — add dunkin_map.png to src/image/ to enable it.)");
            mapWindow = null;
        }

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
                player.setCurrentRoomNumber(1);
                continue;
            }

            // ---- Room header ----
            System.out.println("\n----------------------------------------------");
            System.out.println("Room " + currentRoom.getRoomNumber() + " — " + currentRoom.getName());
            System.out.println("----------------------------------------------");
            System.out.println(currentRoom.getDescription());

            if (currentRoom.isVisited()) {
                System.out.println("(You have been here before.)");
            } else {
                currentRoom.setVisitedTrue();
            }

            // ---- Show exits with destination room numbers ----
            StringBuilder exits = new StringBuilder("Exits: ");
            int n = currentRoom.getNextRoomNumber("N");
            int e = currentRoom.getNextRoomNumber("E");
            int s = currentRoom.getNextRoomNumber("S");
            int w = currentRoom.getNextRoomNumber("W");
            if (n != 0) exits.append("N->").append(n).append(" ");
            if (e != 0) exits.append("E->").append(e).append(" ");
            if (s != 0) exits.append("S->").append(s).append(" ");
            if (w != 0) exits.append("W->").append(w).append(" ");
            System.out.println(exits.toString().equals("Exits: ") ? "Exits: None" : exits.toString());

            System.out.println("Health: " + player.getHealth() + "/100");

            if (player.isBillyCompanion()) {
                System.out.println("Billy: " + player.getBillyHealth() + "/60 HP");
            }

            // ---- Living monsters warning ----
            boolean hasLiving = false;
            for (Monster m : currentRoom.getMonsters()) {
                if (m.isAlive()) { hasLiving = true; break; }
            }
            if (hasLiving) {
                System.out.println("WARNING: There are enemies in this room!");
                for (Monster m : currentRoom.getMonsters()) {
                    if (m.isAlive()) System.out.println("  * " + m);
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
                System.out.println("N / E / S / W               -- move in that direction");
                System.out.println("SEARCH                      -- search the room for hidden items/puzzles");
                System.out.println("EXPLORE                     -- list items visible in this room");
                System.out.println("PICKUP <item>               -- pick up an item from the room");
                System.out.println("DROP <item>                 -- drop an item from your inventory");
                System.out.println("INVENTORY                   -- show items you are carrying");
                System.out.println("EQUIPMENT                   -- show your equipped gear");
                System.out.println("INSPECT <item>              -- read the description of an item");
                System.out.println("EQUIP <item>                -- equip a weapon or armor from inventory");
                System.out.println("ATTACH <attachment> TO <weapon> -- attach a mod to a weapon");
                System.out.println("USE <item>                  -- use a consumable item");
                System.out.println("USE <item> ON <target>      -- use an item on a puzzle object");
                System.out.println("SOLVE <answer>              -- answer a riddle or code puzzle");
                System.out.println("GUESS <number>              -- guess a number for a range puzzle");
                System.out.println("EXAMINE <object>            -- examine a puzzle object in the room");
                System.out.println("LOOT                        -- loot a defeated enemy");
                System.out.println("ATTACK                      -- attack an enemy in this room");
                System.out.println("FEED BILLY                  -- feed Billy rotten flesh to tame him");
                System.out.println("STATUS                      -- check your health and status");
                System.out.println("MAP                         -- toggle the map window");
                System.out.println("Q                           -- quit the game");
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
            // MAP
            // ---------------------------------------------------------------
            if (input.equals("MAP")) {
                if (mapWindow == null) {
                    System.out.println("Map unavailable. Add dunkin_map.png to src/image/ to enable it.");
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
                        System.out.println("  - " + item.getName() + " [" + item.getItemType() + "]");
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
                        System.out.println("  - " + item.getName() + " [" + item.getItemType() + "]");
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
                        if (item.getItemType().equals("weapon")) {
                            System.out.println("  - " + item.getName()
                                    + " [DMG: " + item.getDamage()
                                    + " | HIT: " + item.getHitPercentage() + "%"
                                    + (item.getAmmoCapacity() > 0 ? " | AMMO: " + item.getAmmoCapacity() : "")
                                    + "]");
                        } else {
                            System.out.println("  - " + item.getName() + " [DEF: " + item.getDefense() + "]");
                        }
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EQUIP <item>
            // ---------------------------------------------------------------
            if (input.startsWith("EQUIP ") && rawInput.length() > 6) {
                String itemName = rawInput.substring(6).trim();
                Item toEquip = null;
                for (Item item : player.getInventory()) {
                    if (item.getName().equalsIgnoreCase(itemName)) { toEquip = item; break; }
                }
                if (toEquip == null) {
                    System.out.println("You do not have that item.");
                } else if (!toEquip.isEquippable()) {
                    System.out.println("That item cannot be equipped.");
                } else {
                    toEquip.equip();
                    player.removeItem(toEquip.getName());
                    player.addEquipment(toEquip);
                    System.out.println("You equipped " + toEquip.getName() + ".");
                    if (toEquip.getItemType().equals("weapon")) {
                        System.out.println("  Damage: " + toEquip.getDamage() + " | Hit chance: " + toEquip.getHitPercentage() + "%");
                    } else {
                        System.out.println("  Defense: " + toEquip.getDefense());
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // ATTACH <attachment> TO <weapon>
            // Works on weapons in inventory OR already equipped.
            // Saw Blade adds damage. Scopes add hit percentage.
            // ---------------------------------------------------------------
            if (input.startsWith("ATTACH ") && input.contains(" TO ")) {
                String attachName = rawInput.substring(7, rawInput.toUpperCase().indexOf(" TO ")).trim();
                String weaponName = rawInput.substring(rawInput.toUpperCase().indexOf(" TO ") + 4).trim();

                // Find attachment in inventory
                Item attachment = null;
                for (Item item : player.getInventory()) {
                    if (item.getName().equalsIgnoreCase(attachName)) { attachment = item; break; }
                }
                if (attachment == null) {
                    System.out.println("You do not have " + attachName + " in your inventory.");
                    continue;
                }
                if (!attachment.isAttachment()) {
                    System.out.println(attachName + " is not an attachment.");
                    continue;
                }

                // Find weapon — check equipment first, then inventory
                Item weapon = null;
                for (Item item : player.getEquipment()) {
                    if (item.getName().equalsIgnoreCase(weaponName)) { weapon = item; break; }
                }
                if (weapon == null) {
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(weaponName)) { weapon = item; break; }
                    }
                }
                if (weapon == null) {
                    System.out.println("You do not have " + weaponName + ".");
                    continue;
                }
                if (!weapon.getItemType().equals("weapon")) {
                    System.out.println(weaponName + " is not a weapon.");
                    continue;
                }

                // Check compatibility
                if (!attachment.getCompatibleWeaponIDs().isEmpty()
                        && !attachment.isCompatibleWith(weapon.getItemID())) {
                    System.out.println(attachName + " is not compatible with " + weaponName + ".");
                    continue;
                }

                // Apply bonuses
                int dmgBonus = attachment.getDamage();
                int hitBonus = attachment.getHitPercentage();
                if (dmgBonus > 0) weapon.setDamage(weapon.getDamage() + dmgBonus);
                if (hitBonus > 0) weapon.setHitPercentage(weapon.getHitPercentage() + hitBonus);

                player.removeItem(attachment.getName());
                System.out.println("You attached " + attachName + " to " + weaponName + ".");
                if (dmgBonus > 0) System.out.println("  +" + dmgBonus + " damage.");
                if (hitBonus > 0) System.out.println("  +" + hitBonus + "% hit chance.");
                System.out.println("  " + weaponName + " -> DMG: " + weapon.getDamage()
                        + " | HIT: " + weapon.getHitPercentage() + "%");
                continue;
            }

            // ---------------------------------------------------------------
            // INSPECT <item>
            // ---------------------------------------------------------------
            if (input.startsWith("INSPECT ") && rawInput.length() > 8) {
                String itemName = rawInput.substring(8).trim();
                Item found = null;
                for (Item item : player.getInventory()) {
                    if (item.getName().equalsIgnoreCase(itemName)) { found = item; break; }
                }
                if (found == null) {
                    for (Item item : player.getEquipment()) {
                        if (item.getName().equalsIgnoreCase(itemName)) { found = item; break; }
                    }
                }
                if (found != null) {
                    found.itemDetails();
                } else {
                    System.out.println("You do not have that item.");
                }
                continue;
            }

            // ---------------------------------------------------------------
            // USE <item>            — consume a food or medical item
            // USE <item> ON <target> — use item on a puzzle object
            // ---------------------------------------------------------------
            if (input.startsWith("USE ")) {
                String afterUse = rawInput.substring(4).trim();

                if (input.contains(" ON ")) {
                    // Puzzle interaction
                    String itemName = afterUse.substring(0, afterUse.toUpperCase().indexOf(" ON ")).trim();
                    String target   = afterUse.substring(afterUse.toUpperCase().indexOf(" ON ") + 4).trim();
                    if (!currentRoom.hasPuzzle()) {
                        System.out.println("There is nothing to use that on here.");
                    } else {
                        System.out.println(currentRoom.getPuzzle().useItem(itemName, target, player));
                        if (player.isDead()) {
                            System.out.println("You have died. Game over.");
                            scanner.close(); return;
                        }
                    }
                } else {
                    // Consume item
                    Item toUse = null;
                    for (Item item : player.getInventory()) {
                        if (item.getName().equalsIgnoreCase(afterUse)) { toUse = item; break; }
                    }
                    if (toUse == null) {
                        System.out.println("You do not have that item.");
                    } else if (!toUse.isConsumable()) {
                        System.out.println(toUse.getName() + " cannot be used that way. Try EQUIP or ATTACH.");
                    } else {
                        int boost = toUse.getHealthBoost();
                        if (boost > 0) {
                            player.heal(boost);
                            System.out.println("You used " + toUse.getName() + " and restored " + boost + " HP.");
                            System.out.println("Health: " + player.getHealth() + "/100");
                        } else {
                            System.out.println("You used " + toUse.getName() + ". Something feels different...");
                        }
                        player.removeItem(toUse.getName());
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // EXAMINE <object>
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
            // SOLVE <answer>
            // ---------------------------------------------------------------
            if (input.startsWith("SOLVE ") && rawInput.length() > 6) {
                String answer = rawInput.substring(6).trim();
                if (!currentRoom.hasPuzzle()) {
                    System.out.println("There is no puzzle here to solve.");
                } else {
                    System.out.println(currentRoom.getPuzzle().solve(answer, player));
                    if (player.isDead()) {
                        System.out.println("You have died. Game over.");
                        scanner.close(); return;
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // GUESS <number>
            // ---------------------------------------------------------------
            if (input.startsWith("GUESS ") && rawInput.length() > 6) {
                String guessStr = rawInput.substring(6).trim();
                if (!currentRoom.hasPuzzle()) {
                    System.out.println("There is no puzzle here.");
                } else {
                    Puzzle p = currentRoom.getPuzzle();
                    if (p instanceof AnswerPuzzle) {
                        System.out.println(((AnswerPuzzle) p).guess(guessStr, player));
                    } else {
                        System.out.println("That puzzle is not solved by guessing a number.");
                    }
                    if (player.isDead()) {
                        System.out.println("You have died. Game over.");
                        scanner.close(); return;
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
                    System.out.println("Billy is already by your side.");
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
            // LOOT
            // ---------------------------------------------------------------
            if (input.equals("LOOT")) {
                boolean anyLooted = false;
                for (Monster m : currentRoom.getMonsters()) {
                    if (!m.isAlive()) {
                        System.out.println(m.loot(player.getInventory()));
                        anyLooted = true;
                    }
                }
                if (!anyLooted) System.out.println("There are no defeated enemies here to loot.");
                continue;
            }

            // ---------------------------------------------------------------
            // ATTACK
            // ---------------------------------------------------------------
            if (input.equals("ATTACK") || input.startsWith("ATTACK ")) {

                Monster target = null;
                for (Monster m : currentRoom.getMonsters()) {
                    if (m.isAlive()) { target = m; break; }
                }

                if (target == null) {
                    if (currentRoom.getMonsters().isEmpty()) {
                        System.out.println("There is nothing to attack here.");
                    } else {
                        System.out.println("All enemies are dead. Type LOOT to search the bodies.");
                    }
                    continue;
                }

                // Player attacks
                int  playerDamage = 10;
                Item weapon       = null;
                for (Item eq : player.getEquipment()) {
                    if ("weapon".equals(eq.getItemType())) { weapon = eq; break; }
                }

                if (weapon != null) {
                    int roll = (int)(Math.random() * 100) + 1;
                    if (roll <= weapon.getHitPercentage()) {
                        playerDamage = weapon.getDamage();
                        if (weapon.getAmmoCapacity() > 0) {
                            weapon.setAmmoCapacity(weapon.getAmmoCapacity() - 1);
                            System.out.println("  [Ammo remaining: " + weapon.getAmmoCapacity() + "]");
                            if (weapon.getAmmoCapacity() == 0) {
                                System.out.println(weapon.getName() + " is out of ammo!");
                                player.removeEquipment(weapon.getName());
                            }
                        }
                        System.out.println("You hit " + target.getName() + " with "
                                + weapon.getName() + " for " + playerDamage + " damage!");
                    } else {
                        playerDamage = 0;
                        System.out.println("You swing " + weapon.getName() + " but miss!");
                    }
                } else {
                    System.out.println("You hit " + target.getName()
                            + " with your bare hands for " + playerDamage + " damage!");
                }

                if (playerDamage > 0) System.out.println(target.takeDamage(playerDamage));

                // Billy attacks
                if (player.isBillyCompanion() && player.isBillyAlive()) {
                    Monster billyTarget = null;
                    for (Monster m : currentRoom.getMonsters()) {
                        if (m.isAlive()) { billyTarget = m; break; }
                    }
                    if (billyTarget != null) {
                        System.out.println("Billy attacks " + billyTarget.getName() + " for 8 damage!");
                        System.out.println(billyTarget.takeDamage(8));
                    }
                }

                // Monsters attack back
                for (Monster m : currentRoom.getMonsters()) {
                    if (!m.isAlive()) continue;

                    m.incrementTurnsInRoom();
                    if (m.shouldShriek()) {
                        m.resetShriekCounter();
                        currentRoom.addMonster(Monster.createNormalZombie());
                        System.out.println("The Screamer shrieks! A new zombie shambles in!");
                    }

                    String[] result = m.performAttack(player.isBillyCompanion() && player.isBillyAlive());
                    System.out.println(result[0]);

                    int incoming = Integer.parseInt(result[1]);
                    if (incoming > 0) {
                        int totalDefense = 0;
                        for (Item eq : player.getEquipment()) {
                            if ("armor".equals(eq.getItemType())) totalDefense += eq.getDefense();
                        }
                        int net = Math.max(0, incoming - totalDefense);
                        if (totalDefense > 0) System.out.println("  Your armor absorbed " + totalDefense + " damage.");

                        if ("BILLY".equals(result[2])) {
                            player.dealDamageToBilly(net);
                            System.out.println("Billy took " + net + " damage! His health: "
                                    + player.getBillyHealth() + "/60");
                            if (!player.isBillyAlive()) System.out.println("Billy has fallen. He is gone.");
                        } else {
                            player.takeDamage(net);
                            System.out.println("You took " + net + " damage! Health: "
                                    + player.getHealth() + "/100");
                        }

                        if (player.isDead()) {
                            System.out.println("\nYou have died. Game over.");
                            scanner.close(); return;
                        }
                    }
                }
                continue;
            }

            // ---------------------------------------------------------------
            // MOVEMENT — N / E / S / W
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
    // loadRooms
    // ---------------------------------------------------------------
    private static void loadRooms(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
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
    // loadItems
    // ---------------------------------------------------------------
    private static void loadItems(String filename, HashMap<Integer, Room> roomsMap) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 12);
                if (parts.length < 11) continue;
                int    itemID   = Integer.parseInt(parts[0].trim());
                String name     = parts[1].trim();
                String desc     = parts[2].trim();
                int    location = Integer.parseInt(parts[3].trim());
                String type     = parts[4].trim();
                int    damage   = Integer.parseInt(parts[5].trim());
                int    hitPct   = Integer.parseInt(parts[6].trim());
                int    defense  = Integer.parseInt(parts[7].trim());
                int    hpBoost  = Integer.parseInt(parts[8].trim());
                int    scrapVal = Integer.parseInt(parts[9].trim());
                int    ammo     = Integer.parseInt(parts[10].trim());
                ArrayList<Integer> compatIDs = new ArrayList<>();
                if (parts.length >= 12) {
                    for (String id : parts[11].trim().split("\\|")) {
                        try { compatIDs.add(Integer.parseInt(id.trim())); }
                        catch (NumberFormatException ignored) {}
                    }
                }
                Item item = new Item(itemID, name, desc, location, type, damage, hitPct,
                        defense, hpBoost, scrapVal, ammo, compatIDs);
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
    // loadPuzzles
    // ---------------------------------------------------------------
    private static void loadPuzzles(HashMap<Integer, Room> roomsMap) {
        assignPuzzle(roomsMap, "FR#01PZ", 14);  // Scooter         — Main Street
        assignPuzzle(roomsMap, "FR#02PZ", 27);  // Backroom door   — Backroom (CVS)
        assignPuzzle(roomsMap, "FR#03PZ", 10);  // Vending machine — Motel
        assignPuzzle(roomsMap, "FR#04PZ",  6);  // Police locker   — Police Station
        assignPuzzle(roomsMap, "FR#05PZ", 30);  // Medical cabinet — Medical Facility
        assignPuzzle(roomsMap, "FR#06PZ",  5);  // Monkey cage     — Zoo
        assignPuzzle(roomsMap, "FR#07PZ", 29);  // Bank vault      — Bank
        assignPuzzle(roomsMap, "FR#08PZ", 12);  // Pharmacy puzzle — Pharmacy
        assignPuzzle(roomsMap, "FR#09PZ", 24);  // Toy chest       — Childcare Center
    }

    private static void assignPuzzle(HashMap<Integer, Room> roomsMap, String puzzleId, int roomNumber) {
        if (!roomsMap.containsKey(roomNumber)) return;
        Room room = roomsMap.get(roomNumber);
        if (room.hasPuzzle()) return;
        Puzzle puzzle = PuzzleRegistry.getPuzzleById(puzzleId);
        if (puzzle != null) room.setPuzzle(puzzle);
    }

    // ---------------------------------------------------------------
    // loadMonsters
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
                    System.out.println("Warning: unknown monster type \"" + monsterType + "\"");
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

    private static Monster createMonsterByType(String monsterType) {
        switch (monsterType) {
            case Monster.TYPE_CRAWLER:            return Monster.createCrawler();
            case Monster.TYPE_SPITTER:            return Monster.createSpitter();
            case Monster.TYPE_NORMAL_ZOMBIE:      return Monster.createNormalZombie();
            case Monster.TYPE_SCREAMER:           return Monster.createScreamer();
            case Monster.TYPE_LA_BRONX_GANGSTER:  return Monster.createLaBronxGangster();
            case Monster.TYPE_CIA_OFFICER:        return Monster.createCIAOfficer();
            case Monster.TYPE_INFECTED_CIA:       return Monster.createInfectedCIA();
            case Monster.TYPE_BILLY:              return Monster.createBilly();
            default:                              return null;
        }
    }
}