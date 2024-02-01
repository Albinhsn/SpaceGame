#ifndef IMAGE_H
#define IMAGE_H
#include "common.h"
#include <GL/gl.h>
#include "vector.h"
#include "string.h"
#include <stdbool.h>
#include <stdio.h>

#define IDLE_STATE     0
#define RUNNING_STATE  1
#define ATTACK_1_STATE 2
#define ATTACK_2_STATE 3
#define ATTACK_3_STATE 4

struct PNG
{
  unsigned int   width;
  unsigned int   height;
  unsigned char* data;
  unsigned int   bpp;
};

struct Image
{
  unsigned int   width, height;
  unsigned int   bpp;
  unsigned char* data;
};

struct BufferData
{
  struct Vec3 vertices;
  struct Vec3 indices;
};

struct Mesh
{
  int                bufferDatalength;
  struct BufferData* bufferData;
};

struct Texture
{
  GLuint textureUnit;
  GLuint textureId;
};
struct FontType
{
  float left, right;
  int   size;
};
struct Font
{
  struct FontType* type;
  struct Image     image;
  float            height;
  int              spaceSize;
};



struct TargaHeader
{
  unsigned char  data1[12];
  unsigned short width;
  unsigned short height;
  unsigned char  bpp;
  unsigned char  data2;
};

void parseFontTypes(struct Font* font, const char* fileLocation);
void               initFont(struct Font* font, GLuint* textureId);
struct Image* LoadTarga(const char* filename);
bool read_file(char** buffer, int* len, const char* fileName);
bool          parsePNG(struct Image* png, const char* filename);

#endif
