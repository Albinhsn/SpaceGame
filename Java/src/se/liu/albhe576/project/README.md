# Galaga? 

## ToDo

### Game things
* Wave logic
  * Actually hoist out to wave function
  * i.e the wave controls when shit spawns
  * i.e not need for an enemy class?
* Rather then side to side enemies actually go downwards mostly
    * one of them is basically a kamikaze pilot
        * this requires to check collision between players and enemies
    * Create proper paths
      * interpolate between two positions, enemyType also have some sort of function to modify the path

### Random
* fix docs
* actual logging?
* if a texture is failed to be loaded, create a token texture

### Rendering
* font bitmap
    * differentiate between static and dynamic text rendering
* Create one large image and be able to parse it

### Data
* figure out better way to add score and make that per enemy
* Figure out how we parse UI data from file
    * hoist out pretty much every data to a file :)
        * try to minimize the amount of code/data in UIComponents
        * movementspeed in data?

### UI
* Able to render lists of element
* Implicit rendering of element
* Layout
