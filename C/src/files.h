#ifndef IMAGE_H
#define IMAGE_H
#include "common.h"
#include "string.h"
#include "vector.h"
#include <GL/gl.h>
#include <stdbool.h>
#include <stdio.h>

#define IDLE_STATE     0
#define RUNNING_STATE  1
#define ATTACK_1_STATE 2
#define ATTACK_2_STATE 3
#define ATTACK_3_STATE 4

struct PNG
{
  u32 width;
  u32 height;
  u8* data;
  u8  bpp;
};
typedef struct PNG PNG;

struct Image
{
  u32 width, height;
  u8  bpp;
  u8* data;
};
typedef struct Image Image;

struct FontType
{
  float left, right;
  int   size;
};
typedef struct FontType FontType;
struct Font
{
  FontType* type;
  Image     image;
  float     height;
  int       spaceSize;
  u32       vertexArrayId;
  u32       vertexBufferId;
  u32       programId;
  u32       textureId;
};
typedef struct Font Font;

struct TargaHeader
{
  unsigned char  data1[12];
  unsigned short width;
  unsigned short height;
  unsigned char  bpp;
  unsigned char  data2;
};
typedef struct TargaHeader TargaHeader;

void                       parseFontTypes(struct Font* font, const char* fileLocation);
void                       initFont(struct Font* font, GLuint* textureId);
void                       parseTarga(u8** data, u32* width, u32* height, const char* filename);
bool                       readFile(char** buffer, int* len, const char* fileName);
bool                       parsePNG(u8** data, u32* width, u32* height, const char* filename);

#endif
