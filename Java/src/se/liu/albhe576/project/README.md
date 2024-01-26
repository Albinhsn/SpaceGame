# Galaga? 

## ToDo
* design enemies/levels
* Fileformat for entitys
* Binary reader
* Create enemies
* Hittable

## Cleanup/Refactor
* float vec
* Change position from -1.0f - 1.0f to smth else
* remove renderEntity


* Levels, Waves, Boss?
* Enemies
* AI
* Binary reader

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
* Use binary file to store data, create an inspector tool for it
* Some sort of tool to create paths for enemies

### Binary file inspector
* Store/Create a format for which we try to parse the given file 
