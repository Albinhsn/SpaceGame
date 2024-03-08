# Galaga? 

## ToDo
* Rather then using ResourceManager.getState and set variables everywhere 
  * Just make a getX call that returns that value
* Console commands
  * List X, all
  * Save X, all to file
  * restart wave?
* Override player take damage function to check invincibility or not
* Debug show hp over enemies heads?
* Sound
* fix docs

### Game things
* Rather then side to side enemies actually go downwards mostly
  * one of them is basically a kamikaze pilot
    * this requires to check collision between players and enemies

### Random

### Rendering
* Create one large image and be able to parse it
* font bitmap
    * differentiate between static and dynamic text rendering
    * fix to text actually looks good

### Data
* Figure out how we parse UI data from file
    * hoist out pretty much every data to a file :)
* add x and y acceleration to entitydata

### UI
* Layout
  * Able to render lists of element
  * Implicit rendering of element
