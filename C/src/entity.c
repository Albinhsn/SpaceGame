#include "entity.h"
#include "input.h"
#include "timer.h"
#include <endian.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

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

static inline f32 sinAcceleration(u64 lastTick, Entity* entity)
{
  return sin(lastTick / 500.0f) / 5.0f;
}

static inline f32 cosAcceleration(u64 lastTick, Entity* entity)
{
  return cos(lastTick / 500.0f) / 5.0f;
}

static inline f32 noMovement(u64 lastTick, Entity* entity)
{
  return 0.0f;
}

static f32 msAcceleration(u64 lastTick, Entity* entity)
{
  return entity->movementSpeed;
}

static f32 sinAccelerationMS(u64 lastTick, Entity* entity)
{
  return sinAcceleration(lastTick, entity) + msAcceleration(lastTick, entity);
}

static f32 cosAccelerationMS(u64 lastTick, Entity* entity)
{
  return cosAcceleration(lastTick, entity) + msAcceleration(lastTick, entity);
}

static f32 bossMovementY(u64 lastTick, Entity* entity)
{
  return entity->y >= 50.0f ? 0.2f : sinAcceleration(lastTick, entity);
}

static f32((*accelerationFunctions[][2])(u64, Entity*)) = {
    {  sinAcceleration, cosAccelerationMS},
    {  sinAcceleration,    msAcceleration},
    {       noMovement, sinAccelerationMS},
    {  cosAcceleration,     bossMovementY},
    {cosAccelerationMS,   sinAcceleration},
    {   msAcceleration,   sinAcceleration},
    {       noMovement,    msAcceleration},
    {       noMovement,    msAcceleration},
    {       noMovement,    msAcceleration},
};

static inline bool enemyWillShoot(Enemy* enemy, u64 currentTick)
{
  f32 gcd = rand() % 500 + 1000.0f;
  if (enemy->lastShot <= currentTick)
  {
    enemy->lastShot = currentTick + gcd;
    return true;
  }
  return false;
}

void updateEnemy(Enemy* enemy, u64 currentTick)
{
  enemy->entity->x += accelerationFunctions[enemy->type][0](currentTick, enemy->entity);
  enemy->entity->y -= accelerationFunctions[enemy->type][1](currentTick, enemy->entity);

  if (enemyWillShoot(enemy, currentTick))
  {
    createNewBullet(enemy->entity, enemy->type);
  }
}

void updateBullets(u64 lastTick)
{
  for (u32 i = 0; i < MAX_BULLET_COUNT; i++)
  {
    Bullet bullet = g_bullets[i];
    if (bullet.entity != 0)
    {
      bullet.entity->x += accelerationFunctions[bullet.accelerationFunctionIndex][0](lastTick, bullet.entity);
      bullet.entity->y += accelerationFunctions[bullet.accelerationFunctionIndex][1](lastTick, bullet.entity);
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
  for (i32 i = 1; i < MAX_ENTITY_COUNT; i++)
  {
    if (g_entities[i].textureIdx == 0)
    {
      return &g_entities[i];
    }
  }
  return 0;
}

Bullet* getNewBullet()
{
  for (i32 i = 0; i < 256; i++)
  {
    if (g_bullets[i].entity == 0)
    {
      printf("Bullet: %d\n", i);
      return &g_bullets[i];
    }
  }
  return 0;
}

void createNewBullet(Entity* entity, u64 entityIdx)
{
  Bullet* bullet        = getNewBullet();
  bullet->entity        = getNewEntity();

  BulletData bulletData = g_bulletData[entityIdx];

  f32        y          = entity->y + entity->height;
  bullet->playerBullet  = entityIdx == 4;
  initEntity(bullet->entity, entity->x, y, bulletData.width, bulletData.height, bulletData.textureIdx, bullet->playerBullet ? 0 : 180.0f,
             bullet->playerBullet ? bulletData.movementSpeed : -bulletData.movementSpeed);
  bullet->accelerationFunctionIndex = bulletData.accelerationFunctionIndex;
  bullet->hp                        = 1;
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
  fread(g_bulletData, 1, sizeof(BulletData) * numberOfBullets, entityDataFile);

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
  fread(g_entityData, 1, sizeof(EntityData) * numberOfEntities, entityDataFile);
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
