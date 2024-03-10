#include "files.h"
#include "lodepng.h"
#include "sdl.h"
#include <ctype.h>
#include <string.h>

static void parseIntFromString(int* dest, char* source, u8* length)
{
  char number[32];
  memset(number, 0, 32);

  for (int i = 0; i < 32; i++)
  {
    number[i] = 0;
  }
  u8 pos = 0;
  while (isdigit(source[pos]))
  {
    pos++;
  }
  memcpy(number, source, pos);
  *dest   = atoi(number) * 2;
  *length = pos;
}

static void parseFloatFromString(float* dest, char* source, u8* length)
{
  char number[32];
  u8   pos = 0;
  while (source[pos] != ' ')
  {
    pos++;
  }
  memcpy(number, source, pos);
  *dest   = atof(number);
  *length = pos;
}

void parseFontTypes(struct Font* font, const char* fileLocation)
{
  font->type = (struct FontType*)malloc(sizeof(struct FontType) * 95);

  char* buffer;
  int   len;
  readFile(&buffer, &len, fileLocation);
  FILE* file;
  file = fopen(fileLocation, "r");
  char line[256];
  char number[32];
  for (int i = 0; i < 95; i++)
  {
    memset(number, 0, 32);
    fgets(line, sizeof(line), file);
    u8 pos = 0;
    u8 inc = 0;
    // remove number
    while (line[pos] != ' ')
    {
      pos++;
    }
    // remove whitespace, number and another whitespace
    pos += 3;
    parseFloatFromString(&font->type[i].left, &line[pos], &inc);
    pos += inc;

    // This should always be the start of the number
    // parse whitespace again
    while (line[pos] == ' ')
    {
      pos++;
    }

    parseFloatFromString(&font->type[i].right, &line[pos], &inc);
    pos += inc;
    while (line[pos] == ' ')
    {
      pos++;
    }
    parseIntFromString(&font->type[i].size, &line[pos], &inc);
  }
}

bool parsePNG(u8** data, u32* width, u32* height, const char* filename)
{
  unsigned error;

  error = lodepng_decode32_file(data, width, height, filename);
  if (error != 0)
  {
    printf("Failed to decode png '%s'\n", filename);
    return false;
  }

  return true;
}

bool readFile(char** buffer, int* len, const char* fileName)
{
  FILE* filePtr;
  long  fileSize, count;
  int   error;

  filePtr = fopen(fileName, "r");
  if (!filePtr)
  {
    return false;
  }

  fileSize            = fseek(filePtr, 0, SEEK_END);
  fileSize            = ftell(filePtr);

  *len                = fileSize;
  *buffer             = (char*)malloc(sizeof(char) * (fileSize + 1));
  (*buffer)[fileSize] = '\0';
  fseek(filePtr, 0, SEEK_SET);
  count = fread(*buffer, 1, fileSize, filePtr);
  if (count != fileSize)
  {
    free(*buffer);
    return false;
  }

  error = fclose(filePtr);
  if (error != 0)
  {
    free(*buffer);
    return false;
  }

  return true;
}

void initFont(struct Font* font, GLuint* textureId)
{

  // parsePNG(&font->image, FONT_IMAGE_LOCATION);

  glActiveTexture(GL_TEXTURE0);
  glGenTextures(1, textureId);
  glBindTexture(GL_TEXTURE_2D, *textureId);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, font->image.width, font->image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, font->image.data);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  sta_glGenerateMipmap(GL_TEXTURE_2D);

  font->spaceSize = 10;
  font->height    = 128.0f;
  parseFontTypes(font, FONT_DATA_LOCATION);
}

void parseTarga(u8** data, u32* width, u32* height, const char* filename)
{

  struct TargaHeader targaFileHeader;

  FILE*              filePtr;
  unsigned long      count, imageSize;
  unsigned char*     targaData;
  unsigned char*     targaImage;

  // Open the targa file for reading in binary.
  filePtr = fopen(filename, "rb");
  if (filePtr == NULL)
  {
    printf("ERROR: file doesn't exist %s\n", filename);
    return;
  }

  // Read in the file header.
  count = fread(&targaFileHeader, sizeof(struct TargaHeader), 1, filePtr);
  if (count != 1)
  {
    printf("ERROR: Failed to read into header\n");
    return;
  }

  // Get the important information from the header.
  *width  = (u32)targaFileHeader.width;
  *height = (u32)targaFileHeader.height;

  // Calculate the size of the 32 bit image data.
  if (targaFileHeader.bpp == 32)
  {
    imageSize = targaFileHeader.width * targaFileHeader.height * 4;
  }
  else if (targaFileHeader.bpp == 24)
  {
    imageSize = targaFileHeader.width * targaFileHeader.height * 3;
  }
  else
  {
    printf("Dont't know how to parse targa image with bpp of '%d'\n", targaFileHeader.bpp);
    exit(2);
  }

  // Allocate memory for the targa image data.
  targaImage = (unsigned char*)malloc(sizeof(unsigned char) * imageSize);

  // Read in the targa image data.
  count = fread(targaImage, 1, imageSize, filePtr);
  if (count != imageSize)
  {
    printf("ERROR: count read doesn't equal imageSize\n");
    return;
  }

  if (fclose(filePtr) != 0)
  {
    return;
  }

  targaData   = (u8*)malloc(sizeof(u8) * imageSize);
  bool bit32  = targaFileHeader.bpp == 32;

  u32  maxIdx = targaFileHeader.height * targaFileHeader.width;
  for (u32 idx = 0; idx < maxIdx; idx++)
  {
    targaData[idx]     = targaImage[idx + 2]; // Red
    targaData[idx + 1] = targaImage[idx + 1]; // Green
    targaData[idx + 2] = targaImage[idx];     // Blue
    if (bit32)
    {
      targaData[idx + 3] = targaImage[idx + 3]; // Alpha
    }
  }

  free(targaImage);
  *data = targaData;
}
