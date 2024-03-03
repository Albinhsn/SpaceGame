# Galaga? 

## ToDo
### Data
* Create one large image and be able to parse it

### Game things
* Create proper paths
  * interpolate between two positions, enemyType also have some sort of function to modify the path

### Random
* Wave logic
  * Actually hoist out to wave function
  * i.e the wave controls when shit spawns
  * i.e not need for an enemy class?
* Boss bullets should be bigger and shoot more often

* Menus
  * Main Menu
  * Settings
    * vsync
    * audio?
    * screen size
  * Menu
  * Game over menu

### Cleanup
* Some sort of GC/Check that collision is done within screen bounds and stuff
  * think we did collision check?
  * don't think GC is neccessary atm with only 1 wave

### Then editor?
