

## Entity data
### Location 
* resources/data/entity($TIMESTAMP).bin
### Count 
* 5
### Attributes
* textureId 
  * int 
* boundsWidth
  * float 
* boundsHeight
    * float 
* boundsXOffset
    * float 
* boundsYOffset
    * float 
* width
    * float 
* height 
    * float
* bulletTextureIdx
* bulletSpeed
* bulletWidth
* bulletHeight
## Wave data

### Location
* resources/data/wave($ID)($TIMESTAMP).bin
### Count
* 24
### Attributes
* enemyType 
  * int
* spawnTime
    * This should be a long
* spawnPositionX
  * float 
* spawnPositionY
  * float 
* pathId
  * int 

## State variables
vsync
screenWidth
screenHeight
waveIdx
scorePerEnemy
updateTimerMS
enemyMS
enemyGCDMin
enemyGCDMax
playerMS
playerGCDMS
buttonSizeSmallWidth
buttonSizeSmallHeight
buttonSizeMediumWidth
buttonSizeMediumHeight
fontSizeSmall
fontSizeMedium
fontSizeLarge
checkboxWidth
checkboxHeight
buttonTextureMapKey
hpHeartHeight
hpHeartWidth

