# The Enchanted Library
ITEC-3860 Individual Assignment 3

## Overview
The Enchanted Library is a Java text-based adventure game. In this game, the player explores different rooms inside a magical library, collects useful items, solves a puzzle, and fights monsters. The game loads room, item, puzzle, and monster data from external text files.

## Game Features
This game includes the following features:

1. Room navigation using north, south, east, and west commands
2. Room descriptions loaded from a text file
3. Tracking of visited rooms
4. Item pickup and drop
5. Item examination
6. Inventory system
7. Equip and unequip commands
8. Heal command using healing items
9. Puzzle examination and solving with limited attempts
10. Monster examination
11. Option to attack or ignore a monster
12. Turn-based combat system
13. Game over system with restart or exit option

## Data Structures Used

### Room
The Room class stores:
- room id
- room name
- room description
- exits to other rooms
- visited status
- list of items in the room
- puzzle in the room
- monster in the room
- monster defeated status

A HashMap is used to store exits by direction.
An ArrayList is used to store room items.

### Player
The Player class stores:
- player name
- maximum health
- current health
- base damage
- inventory
- equipped item
- current room

An ArrayList is used to store the player inventory.

### Item
The Item class stores:
- item name
- description
- attack bonus
- healing points
- consumable status
- equipped status

Items can be used for combat or healing depending on their values.

### Puzzle
The Puzzle class stores:
- puzzle name
- description
- correct answer
- maximum attempts
- current attempt count
- solved status

This class checks if the player answer is correct and keeps track of remaining attempts.

### Monster
The Monster class stores:
- monster name
- description
- maximum health
- current health
- attack damage
- critical hit threshold
- defeated status

The monster can deal normal damage or double damage depending on a random critical hit check.

## Engine Classes

### Main
This is the starting point of the program. It introduces the game, asks for the player name, and starts the adventure.

### GameEngine
This class controls the overall game flow. It handles:
- loading game data
- starting a new game
- room navigation
- item commands
- puzzle commands
- monster commands
- game over and restart logic

### CombatSystem
This class handles combat between the player and a monster. During combat, the player can:
- attack
- heal
- view inventory
- equip an item
- unequip an item

### FileParser
This class reads the external text files and creates the game objects for:
- rooms
- items
- puzzles
- monsters

## Commands

### Movement Commands
- NORTH
- SOUTH
- EAST
- WEST

### Room Commands
- LOOK
- EXAMINE ITEM
- PICKUP
- DROP
- INVENTORY

### Item Commands
- EQUIP
- UNEQUIP
- HEAL

### Puzzle Commands
- EXAMINE PUZZLE
- SOLVE
- IGNORE PUZZLE

### Monster Commands
- EXAMINE MONSTER
- ATTACK
- IGNORE MONSTER

### Other Commands
- HELP
- QUIT

## Combat System
When the player enters a room with a monster, the monster can be examined first. The player may then choose to attack or ignore it.

If combat starts, the game enters turn-based combat mode. In each round:
1. The game shows player health and monster health
2. The player chooses an action
3. The monster attacks if it is still alive

The player wins when the monster health reaches 0.
The player loses when the player health reaches 0.

If the player loses, the game shows a game over message and gives the option to restart or exit.

## Files Used

### Java Files
- engine/Main.java
- engine/GameEngine.java
- engine/CombatSystem.java
- engine/FileParser.java
- models/Room.java
- models/Player.java
- models/Item.java
- models/Puzzle.java
- models/Monster.java

### Data Files
- data/rooms.txt
- data/items.txt
- data/puzzles.txt
- data/monsters.txt

## How to Run the Program

### In IntelliJ
1. Open the project in IntelliJ IDEA
2. Make sure the data folder is in the project directory
3. Run engine.Main
4. Enter your player name when prompted
5. Follow the commands shown in the game

### Using Command Line
1. Compile the Java files
2. Run the program using:
   java engine.Main

## Notes
- All room, item, puzzle, and monster information is loaded from text files
- Missing room exits use -1 in the rooms.txt file
- The player begins in Room 0, which is the Entrance Hall
- The game was designed using object-oriented programming principles with separate model and engine classes