#include "bounds.h"

#if DEBUG_BOUNDS
static void initDebugBounds(struct Image* image, struct Vec3i32 color)
{
  int lineThickness = 6;
  int bpp           = 4;

  image->width      = 128;
  image->height     = 128;
  image->bpp        = bpp;
  i32 size          = image->width * image->height * bpp;
  image->data       = (unsigned char*)malloc(sizeof(unsigned char*) * size);
  for (i32 i = 0; i < size; i++)
  {
    image->data[i] = 0;
  }

  int bottomRow = (image->height - lineThickness) * image->width * bpp;

  for (int i = 0; i < image->width * lineThickness * bpp; i += 4)
  {
    image->data[i + 0]             = color.x;
    image->data[i + 1]             = color.y;
    image->data[i + 2]             = color.z;
    image->data[i + 3]             = 255;

    image->data[bottomRow + i + 0] = color.x;
    image->data[bottomRow + i + 1] = color.y;
    image->data[bottomRow + i + 2] = color.z;
    image->data[bottomRow + i + 3] = 255;
  }

  int lastColumn = (image->width - lineThickness) * bpp;
  for (int i = 0; i < image->height; i++)
  {
    int yOffset = i * image->width * bpp;
    for (int j = 0; j < lineThickness * bpp; j += 4)
    {
      image->data[yOffset + j + 0]              = color.x;
      image->data[yOffset + j + 1]              = color.y;
      image->data[yOffset + j + 2]              = color.z;
      image->data[yOffset + j + 3]              = 255;

      image->data[yOffset + lastColumn + j + 0] = color.x;
      image->data[yOffset + lastColumn + j + 1] = color.y;
      image->data[yOffset + lastColumn + j + 2] = color.z;
      image->data[yOffset + lastColumn + j + 3] = 255;
    }
  }
}

#endif
