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
  f32 movementSpeed;
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
  i8      hp;
};
typedef struct Player Player;

struct Bullet
{
  Entity* entity;
  i32     accelerationFunctionIndex;
  u8      hp;
  bool    playerBullet;
};
typedef struct Bullet Bullet;

struct BulletData
{
  i32 textureIdx;
  i32 accelerationFunctionIndex;
  f32 movementSpeed;
  f32 width;
  f32 height;
};
typedef struct BulletData BulletData;

struct Enemy
{
  Entity* entity;
  u64     spawnTime;
  u64     lastShot;
  u64     scoreGiven;
  i32     type;
  i32     hp;
};
typedef struct Enemy Enemy;

#define MAX_ENTITY_COUNT 256
#define MAX_BULLET_COUNT 256
extern EntityData* g_entityData;
extern BulletData* g_bulletData;
extern Entity      g_entities[256];
extern Bullet      g_bullets[256];

Entity*            getPlayerEntity();
Entity*            getNewEntity();
Bullet*            getNewBullet();
void               loadEntityData();
void               removeOutOfBoundsBullets(u64 currentTick);
void               loadBulletData();
void               updateBullets(u64 lastTick);
void               updateEnemy(Enemy* enemy, u64 currentTick);
void               createNewBullet(Entity* entity, u64 entityIdx);
bool               entitiesCollided(Entity* e1, Entity* e2);
void               debugPlayer(Player* player);
void               debugEntity(Entity* entity);
void               createPlayer(Player* player);
void               initEntity(Entity* entity, f32 x, f32 y, f32 width, f32 height, u32 textureIdx, f32 rotation, f32 movementSpeed);
bool               updatePlayer(InputState* inputState, Player* player, Timer* timer);

#endif
