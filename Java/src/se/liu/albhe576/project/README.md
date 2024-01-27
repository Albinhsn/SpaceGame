# Galaga? 

## ToDo
* Figure out editor for paths
* figure out data format for the game
* Actual Level and Wave class or smth
* Enemies need to shoot
  * Just another gcd?
* Start screen
* End screen
* Pause screen

## Cleanup/Refactor
* Change position from -1.0f - 1.0f to smth else
* float vec
  * Essentially refactor out the float[] everywhere to something easier to access
* remove renderEntity
* Shooting starts from outside your own bounds



### Notes

### Platform layer API
* Background
* Entity
  * pointer to what that texture looks like in the format
    * byte
  * position
    * x2 float
  * bounds 
      * x2 float



### Goals
* Abstract platform layer for swing, openGL, maybe vulkan
* Some sort of tool to create paths for enemies
