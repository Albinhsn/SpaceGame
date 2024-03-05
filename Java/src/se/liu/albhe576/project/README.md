# Galaga? 

## ToDo
* font bitmap
    * differentiate between static and dynamic text rendering
* Rather then side to side enemies actually go downwards mostly
    * one of them is basically a kamikaze pilot
        * this requires to check collision between players and enemies
    * Create proper paths
      * interpolate between two positions, enemyType also have some sort of function to modify the path

### Game things
* Wave logic
  * Actually hoist out to wave function
  * i.e the wave controls when shit spawns
  * i.e not need for an enemy class?

### Random
* fix docs
* actual logging?

### Rendering
* Create one large image and be able to parse it

### Data
* Figure out how we parse UI data from file
    * hoist out pretty much every data to a file :)

### UI
* Able to render lists of element
* Implicit rendering of element
* Layout
