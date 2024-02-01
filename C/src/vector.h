#ifndef VECTOR_H
#define VECTOR_H

#include "common.h"

#define CREATE_VEC2i32(x,y) ((struct Vec2i32){x,y})
#define CREATE_VEC3i32(x,y,z) ((struct Vec3i32){x,y,z})
#define CREATE_VEC4i32(x,y,z,w) ((struct Vec4i32){x,y,z,w})

#define CREATE_VEC2f32(x,y) ((struct Vec2f32){x,y})
#define CREATE_VEC3f32(x,y,z) ((struct Vec3f32){x,y,z})
#define CREATE_VEC4f32(x,y,z,w) ((struct Vec4f32){x,y,z,w})

struct Vec4f32
{
  union
  {
    f32 pos[4];
    struct
    {
      f32 x;
      f32 y;
      f32 z;
      f32 w;
    };
    struct
    {
      f32 r;
      f32 g;
      f32 b;
      f32 a;
    };
  };
};

struct Vec2f32
{
  union
  {
    f32 pos[2];
    struct
    {
      f32 x;
      f32 y;
    };
  };
};

struct Vec2i32
{
  union
  {
    i32 pos[2];
    struct
    {
      i32 x;
      i32 y;
    };
  };
};

struct Vec3
{
  f32 x, y, z;
};

struct Vec3i32
{
  i32 x, y, z;
};

struct Vec4
{
  f32 x, y, z, w;
};

struct Vec4i32
{
  i32 x, y, z, w;
};

#endif
