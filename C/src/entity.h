#ifndef ENTITY_H
#define ENTITY_H
#include "common.h"

struct Entity
{
  f32 x;
  f32 y;
  f32 width;
  f32 height;
  f32 rotation;
  u32 textureIdx;
};
typedef struct Entity Entity;


#endif
