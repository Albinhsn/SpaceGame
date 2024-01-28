# Galaga? 

## ToDo
* Be able to decrease the density of points
  * plus and minus texture at some portion of the screen
  * Always store original amount
    * recalculated when we increase/decrease
* Be able to store the line



* figure out data format for the game
* Actual Level and Wave class or smth
* Enemies need to shoot
  * Just another gcd?
* Start screen
* End screen
* Pause screen
* Debug mode
  * fps?

## Cleanup/Refactor
* float vec
  * Essentially refactor out the float[] everywhere to something easier to access
* figure out better way to name OpenGLTexture
* lookover platformlayer and it's purpose
  * refactor openglplatformlayer

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
