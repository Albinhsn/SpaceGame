#include "files.h"
#include "lodepng.h"
#include "sdl.h"
#include <ctype.h>
#include <string.h>

static void parseIntFromString(int* dest, char* source, ui8* length)
{
  char number[32];
  memset(number, 0, 32);

  for (int i = 0; i < 32; i++)
  {
    number[i] = 0;
  }
  ui8 pos = 0;
  while (isdigit(source[pos]))
  {
    pos++;
  }
  memcpy(number, source, pos);
  *dest   = atoi(number) * 2;
  *length = pos;
}

static void parseFloatFromString(float* dest, char* source, ui8* length)
{
  char number[32];
  ui8  pos = 0;
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
  read_file(&buffer, &len, fileLocation);
  FILE* file;
  file = fopen(fileLocation, "r");
  char line[256];
  char number[32];
  for (int i = 0; i < 95; i++)
  {
    memset(number, 0, 32);
    fgets(line, sizeof(line), file);
    ui8 pos = 0;
    ui8 inc = 0;
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

bool parsePNG(struct Image* png, const char* filename)
{
  unsigned error;
  png->bpp = 4;

  error    = lodepng_decode32_file(&png->data, &png->width, &png->height, filename);
  if (error != 0)
  {
    printf("Failed to decode png '%s'\n", filename);
    return false;
  }

  return true;
}

bool read_file(char** buffer, int* len, const char* fileName)
{
  FILE* filePtr;
  long  fileSize, count;
  int   error;

  filePtr = fopen(fileName, "r");
  if (!filePtr)
  {
    return NULL;
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

  parsePNG(&font->image, FONT_IMAGE_LOCATION);

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

struct Image* LoadTarga(const char* filename)
{

  struct Image*      image = (struct Image*)malloc(sizeof(struct Image));
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
    return NULL;
  }

  // Read in the file header.
  count = fread(&targaFileHeader, sizeof(struct TargaHeader), 1, filePtr);
  if (count != 1)
  {
    printf("ERROR: Failed to read into header\n");
    return NULL;
  }

  // Get the important information from the header.
  image->width  = (int)targaFileHeader.width;
  image->height = (int)targaFileHeader.height;
  image->bpp    = (int)targaFileHeader.bpp;

  // Calculate the size of the 32 bit image data.
  if (image->bpp == 32)
  {
    imageSize = image->width * image->height * 4;
  }
  else if (image->bpp == 24)
  {
    imageSize = image->width * image->height * 3;
  }
  else
  {
    printf("Dont't know how to parse targa image with bpp of '%d'\n", image->bpp);
    exit(2);
  }

  // Allocate memory for the targa image data.
  targaImage = (unsigned char*)malloc(sizeof(unsigned char) * imageSize);

  // Read in the targa image data.
  count = fread(targaImage, 1, imageSize, filePtr);
  if (count != imageSize)
  {
    printf("ERROR: count read doesn't equal imageSize\n");
    return NULL;
  }

  if (fclose(filePtr) != 0)
  {
    return NULL;
  }

  targaData  = (unsigned char*)malloc(sizeof(unsigned char) * imageSize);
  bool bit32 = image->bpp == 32;

  for (int idx = 0; idx < image->height * image->width; idx++)
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
  image->data = targaData;

  return image;
}
