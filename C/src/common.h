#ifndef COMMON_H
#define COMMON_H

#include <stddef.h>
#include <stdint.h>
#include <sys/time.h>
#define DEBUG_INPUT false
#define DEBUG_PLAYER false

#define DEBUG_BOUNDS false


#define SCREENWIDTH 1024
#define SCREENHEIGHT 780


#define ONE_DIVIDED_BY_SCREENWIDTH 1.0f / (float)SCREENWIDTH
#define ONE_DIVIDED_BY_SCREENHEIGHT 1.0f / (float)SCREENHEIGHT

#define INBOUNDS(fst, snd) (MAX(MIN(fst, snd), -snd))

#define MIN(fst, snd) (fst < snd ? fst : snd)
#define MAX(fst, snd) (fst > snd ? fst : snd)

#define WORLDSPACEX_TO_VIEWSPACE(x) (((float)(x) * ONE_DIVIDED_BY_SCREENWIDTH))
#define WORLDSPACEY_TO_VIEWSPACE(y) (((float)(y) * ONE_DIVIDED_BY_SCREENHEIGHT))

#define MOUSEX_TO_VIEWSPACE(x)                                                 \
  ((float)(2.0f * x - SCREENWIDTH) / (float)SCREENWIDTH)
#define MOUSEY_TO_VIEWSPACE(x)                                                 \
  ((float)(2.0f * x - SCREENHEIGHT) / (float)SCREENHEIGHT)

#define ANIMATION_UPDATE_TIMER 100

#define FONT_IMAGE_LOCATION "./resources/fonts/font01.png"
#define FONT_DATA_LOCATION "./resources/fonts/font01.txt"
#define PLAYER_FILE_LOCATION                                                   \
  "./resources/images/PNG/Sprites/Ships/spaceShips_001.png"

#define ABS_F(x) ((x < 0.0f ? -x : x))

#define ASCII_ESCAPE 27
#define ASCII_RETURN 13
#define ASCII_SPACE 32
#define ASCII_BACKSPACE 8

#define TEXT_PROGRAM_VALUE 3
#define TEXT_TEXTURE_VALUE 1
#define TEXT_VAO_VALUE 1
#define TEXT_VBO_VALUE 1

#define TEXTURE_PROGRAM_VALUE 6
#define PLAYER_VAO_VALUE 2
#define PLAYER_VBO_VALUE 3


typedef uint8_t ui8;
typedef uint16_t ui16;
typedef uint32_t ui32;

typedef float f32;
typedef double f64;

typedef int8_t i8;
typedef int16_t i16;
typedef int i32;
typedef int64_t i64;

long long timeInMilliseconds(void);
void getInfoStrings(char *fpsString, char *msString, long long *lastTick,
                    long long *previousTick);

#endif
