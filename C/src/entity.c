#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include <endian.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

u32         g_entityCount = 1;
u32         g_bulletCount = 0;
Bullet      g_bullets[256];
Entity      g_entities[256];
EntityData* g_entityData = 0;
BulletData* g_bulletData = 0;

static bool withinScreen(Entity* entity)
{
  i32 x = (i32)(100.0f - entity->width / 2.0f);
  i32 y = (i32)(100.0f - entity->height / 2.0f);
  return !(entity->x <= -x || entity->x >= x || entity->y <= -y || entity->y >= y);
}

void updateBullets()
{
  for (u32 i = 0; i < g_bulletCount; i++)
  {
    Bullet bullet = g_bullets[i];
    if (bullet.entity != 0)
    {
      bullet.entity->y += bullet.entity->rotation == 0 ? bullet.entity->movementSpeed : -bullet.entity->movementSpeed;
    }
  }
}

bool entitiesCollided(Entity* e1, Entity* e2)
{
  if (!withinScreen(e1) || !withinScreen(e2))
  {
    return false;
  }
  f32 minE1X = e1->x - e1->width;
  f32 maxE1X = e1->x + e1->width;
  f32 minE1Y = e1->y - e1->height;
  f32 maxE1Y = e1->y + e1->height;

  f32 minE2X = e2->x - e2->width;
  f32 maxE2X = e2->x + e2->width;
  f32 minE2Y = e2->y - e2->height;
  f32 maxE2Y = e2->y + e2->height;

  if (minE1X > maxE2X || maxE1X < minE2X)
  {
    return false;
  }
  return !(minE1Y > maxE2Y) && !(maxE1Y < minE2Y);
}
Entity* getPlayerEntity()
{
  return &g_entities[0];
}

Entity* getNewEntity()
{
  return &g_entities[g_entityCount++];
}

Bullet* getNewBullet()
{
  return &g_bullets[g_bulletCount++];
}

void createNewBullet(Entity* entity, u64 entityIdx)
{
  Bullet* bullet        = getNewBullet();
  bullet->entity        = getNewEntity();

  BulletData bulletData = g_bulletData[entityIdx];

  f32        y          = entity->y + entity->height;
  initEntity(bullet->entity, entity->x, entity->y, bulletData.width, bulletData.height, bulletData.textureIdx, entityIdx == 4 ? 0 : 180.0f, bulletData.movementSpeed);
  bullet->parent = entity;
  bullet->hp     = 1;
}

bool playerCanShoot(Player* player, Timer* timer)
{
  if (player->lastShot > timer->lastTick)
  {
    return false;
  }
  player->lastShot = timer->lastTick + PLAYER_GCD;
  return true;
}

bool updatePlayer(InputState* inputState, Player* player, Timer* timer)
{

  f32 xAcc = 0.0f, yAcc = 0.0f;
  f32 ms = player->entity->movementSpeed;

  if (inputState->keyboardStateDown['w'])
  {
    yAcc += ms;
  }
  if (inputState->keyboardStateDown['s'])
  {
    yAcc -= ms;
  }

  if (inputState->keyboardStateDown['d'])
  {
    xAcc += ms;
  }
  if (inputState->keyboardStateDown['a'])
  {
    xAcc -= ms;
  }

  Entity* entity = player->entity;
  entity->x += xAcc;
  entity->y += yAcc;
  if (!withinScreen(entity))
  {
    entity->x -= xAcc;
    entity->y -= yAcc;
  }

  return inputState->keyboardStateDown[ASCII_SPACE] && playerCanShoot(player, timer);
}

void initEntity(Entity* entity, f32 x, f32 y, f32 width, f32 height, u32 textureIdx, f32 rotation, f32 movementSpeed)
{
  entity->x             = x;
  entity->y             = y;
  entity->width         = width;
  entity->height        = height;
  entity->textureIdx    = textureIdx;
  entity->rotation      = rotation;
  entity->movementSpeed = movementSpeed;
}

void createBullet(Bullet* bullet, Entity* parent)
{
  f32 y = parent->y + parent->height;
  initEntity(bullet->entity, parent->x, parent->y, 2.0f, 4.0f, TEXTURE_PLAYER_BULLET, 0.0f, 0.5f);
  bullet->parent = parent;
  bullet->hp     = 1;
}

void debugEntity(Entity* entity)
{

  printf("%f %f %f %f %f %d\n", entity->x, entity->y, entity->width, entity->height, entity->rotation, entity->textureIdx);
}

void debugBulletData(BulletData* bulletData)
{
  printf("%d %d %f %f %f\n", bulletData->textureIdx, bulletData->accelerationFunctionIndex, bulletData->movementSpeed, bulletData->width, bulletData->height);
}

void debugEntityData(EntityData* entityData)
{
  printf("%d %d %f %f %d %f\n", entityData->hp, entityData->textureIdx, entityData->width, entityData->height, entityData->score, entityData->movementSpeed);
}

void debugPlayer(Player* player)
{
  printf("%d %ld, ", player->hp, player->lastShot);
  debugEntity(player->entity);
}

void createPlayer(Player* player)
{
  player->entity  = getPlayerEntity();
  EntityData data = g_entityData[4];
  initEntity(player->entity, 0.0f, 0.0f, data.width, data.height, data.textureIdx, 0.0f, data.movementSpeed);
  player->hp       = data.hp;
  player->lastShot = 0;
}

void loadBulletData()
{
  const char* fileLocation = "./resources/entities/bulletData.txt";
  FILE*       file;
  char        line[256];

  file = fopen(fileLocation, "rb");
  fgets(line, sizeof(line), file);
  i32 numberOfBullets = atoi(line);
  g_bulletData        = (BulletData*)malloc(sizeof(BulletData) * numberOfBullets);

  fgets(line, sizeof(line), file);
  fclose(file);

  FILE* entityDataFile = fopen(line, "rb");
  printf("INFO: Reading data from '%s'\n", line);

  memset(g_bulletData, 0, sizeof(BulletData) * numberOfBullets);
  u32 count = fread(g_bulletData, 1, sizeof(BulletData) * numberOfBullets, entityDataFile);

  // printf("%d\n", numberOfBullets);

  for (u32 i = 0; i < numberOfBullets; i++)
  {
    BulletData d                = g_bulletData[i];
    d.textureIdx                = htobe32(d.textureIdx);
    d.accelerationFunctionIndex = htobe32(d.accelerationFunctionIndex);
    d.width                     = convertFloatToBE(d.width);
    d.height                    = convertFloatToBE(d.height);
    d.movementSpeed             = convertFloatToBE(d.movementSpeed);
    g_bulletData[i]             = d;
  }
}

void loadEntityData()
{
  const char* fileLocation = "./resources/entities/entityData.txt";
  FILE*       file;
  char        line[256];

  file = fopen(fileLocation, "rb");
  fgets(line, sizeof(line), file);
  i32 numberOfEntities = atoi(line);
  g_entityData         = (EntityData*)malloc(sizeof(EntityData) * numberOfEntities);

  fgets(line, sizeof(line), file);
  fclose(file);

  FILE* entityDataFile = fopen(line, "rb");
  printf("INFO: Reading data from '%s'\n", line);
  memset(g_entityData, 0, sizeof(EntityData) * numberOfEntities);
  u32 count = fread(g_entityData, 1, sizeof(EntityData) * numberOfEntities, entityDataFile);
  for (u32 i = 0; i < numberOfEntities; i++)
  {
    EntityData d    = g_entityData[i];
    d.hp            = htobe32(d.hp);
    d.textureIdx    = htobe32(d.textureIdx);
    d.score         = htobe32(d.score);
    d.width         = convertFloatToBE(d.width);
    d.height        = convertFloatToBE(d.height);
    d.movementSpeed = convertFloatToBE(d.movementSpeed);
    g_entityData[i] = d;
  }
}
