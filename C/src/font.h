#ifndef FONT_H
#define FONT_H

#define TEXT_MAX_LENGTH 32
#include "sdl.h"
#include "texture.h"
#include <ctype.h>

struct FontType
{
  float left, right;
  int   size;
};
typedef struct FontType FontType;

struct Font
{
  GLuint   programId;
  GLuint   vertexArrayId;
  GLuint   vertexBufferId;
  GLuint   textureId;
  FontType type[256];
};
typedef struct Font Font;

void                initFont(Font* font);
void                buildUpdatedTextVertexArray(Font* font, f32* vertices, u32 vertexCount, const char* text, f32 x, f32 y, f32 spaceSize, f32 fontSize);
void updateText(Font* font, f32 x, f32 y, const char* text);

#endif
