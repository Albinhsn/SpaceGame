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

struct EntityData
{
  i32 hp;
  i32 textureIdx;
  f32 width;
  f32 height;
  i32 bulletTextureIdx;
  f32 bulletSpeed;
  f32 bulletWidth;
  f32 bulletHeight;
  i32 score;
  f32 movementSpeed;
};
typedef struct EntityData EntityData;

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

extern u32            entityCount;
extern u32            bulletCount;
extern Entity         entities[256];
extern Bullet         bullets[256];

Entity*               getPlayer();
Entity*               getNewEntity();
Bullet*               getNewBullet();
void                  loadEntityData();
void debugPlayer(Player *player);
void                  createPlayer(Player* player);
void                  createBullet(Bullet* bullet, Entity* parent);
void                  initEntity(Entity* entity, f32 x, f32 y, f32 width, f32 height, u32 textureIdx, f32 rotation);
bool                  updatePlayer(InputState* inputState, Player* player, Timer* timer);

#endif
