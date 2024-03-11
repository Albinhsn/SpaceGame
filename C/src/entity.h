#ifndef ENTITY_H
#define ENTITY_H
#include "common.h"
#include "input.h"
#include "timer.h"

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

struct Player
{
  Entity* entity;
  u64     lastShot;
  u8      hp;
};
typedef struct Player Player;

struct Bullet
{
  Entity* entity;
  Entity* parent;
  u8      hp;
};
typedef struct Bullet Bullet;

void                  createBullet(Bullet* bullet, Entity* parent);
void                  initEntity(Entity* entity, f32 x, f32 y, f32 width, f32 height, u32 textureIdx, f32 rotation);
bool                  updatePlayer(InputState* inputState, Player* player, Timer* timer);

#endif
