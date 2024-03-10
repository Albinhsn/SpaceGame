#ifndef COMMON_H
#define COMMON_H

#include <stddef.h>
#include <stdint.h>
#include <sys/time.h>

#define DEBUG_INPUT false
#define DEBUG_PLAYER false

#define DEFAULT_SCREENWIDTH 1024
#define DEFAULT_SCREENHEIGHT 780

#define MIN(fst, snd) (fst < snd ? fst : snd)
#define MAX(fst, snd) (fst > snd ? fst : snd)

#define M_PI 3.14159265359

#define RED (( Vec4f32){1.0f, 0.0f, 0.0f, 1.0f})
#define YELLOW (( Vec4f32){1.0f, 1.0f, 0.0f, 1.0f})
#define GREEN (( Vec4f32){0.0f, 1.0f, 0.0f, 1.0f})
#define CYAN (( Vec4f32){0.0f, 1.0f, 1.0f, 1.0f})
#define PURPLE (( Vec4f32){1.0f, 0.0f, 1.0f, 1.0f})
#define BLUE (( Vec4f32){1.0f, 0.0f, 0.0f, 1.0f})

#define ASCII_ESCAPE 27
#define ASCII_RETURN 13
#define ASCII_SPACE 32
#define ASCII_BACKSPACE 8

#define FONT_IMAGE_LOCATION "./resources/fonts/font01.png"
#define FONT_DATA_LOCATION "./resources/fonts/font01.txt"

typedef uint8_t u8;
typedef uint16_t u16;
typedef uint32_t u32;
typedef uint64_t u64;

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
