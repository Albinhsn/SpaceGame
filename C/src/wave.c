#include "wave.h"
#include "common.h"
#include "entity.h"
#include "string.h"
#include <stdio.h>

WaveData* g_waveData;
u64       g_numberOfWaves = 0;

void      getWave(Wave* res, u64 idx)
{
  if (idx >= g_numberOfWaves)
  {
    if (res->enemies != 0)
    {
      free(res->enemies);
    }
    res->enemies    = 0;
    res->enemyCount = 0;
    return;
  }

  WaveData data        = g_waveData[idx];
  res->enemyCount      = data.enemyCount;
  res->timeWaveStarted = 0;
  if (res->enemies != 0)
  {
    free(res->enemies);
  }
  res->enemies = (Enemy*)malloc(sizeof(Enemy) * res->enemyCount);
  for (i32 i = 0; i < data.enemyCount; i++)
  {
    WaveEnemyData enemyData    = data.enemyData[i];
    EntityData    entityData   = g_entityData[enemyData.enemyType];
    res->enemies[i].spawnTime  = enemyData.spawnTime;
    res->enemies[i].lastShot   = 0;
    res->enemies[i].type       = enemyData.enemyType;
    res->enemies[i].entity     = getNewEntity();
    res->enemies[i].hp         = entityData.hp;
    res->enemies[i].scoreGiven = entityData.score;
    initEntity(res->enemies[i].entity, enemyData.spawnPositionX, enemyData.spawnPositionY, entityData.width, entityData.height, entityData.textureIdx, 180.0f, entityData.movementSpeed);
  }
}

static void loadWaveData(WaveData* data, const char* line)
{
  u64   fileSize;
  FILE* filePtr = fopen(line, "rb");
  fileSize      = fseek(filePtr, 0, SEEK_END);
  fileSize      = ftell(filePtr);
  fseek(filePtr, 0, SEEK_SET);

  data->enemyCount = fileSize / sizeof(WaveEnemyData);
  data->enemyData  = (WaveEnemyData*)malloc(sizeof(u8) * fileSize);
  u64 count        = fread(data->enemyData, 1, fileSize, filePtr);
  if (count != fileSize)
  {
    printf("Failed to read %ld, only got %ld from '%s'\n", count, data->enemyCount * sizeof(WaveEnemyData), line);
    exit(2);
  }
  for (i32 i = 0; i < data->enemyCount; i++)
  {
    WaveEnemyData d    = data->enemyData[i];
    i64           tmp  = htobe32(d.enemyType);
    d.pathId           = htobe32(d.pathId);
    d.enemyType        = htobe32(d.spawnTime);
    d.spawnTime        = tmp;
    d.spawnPositionX   = convertFloatToBE(d.spawnPositionX);
    d.spawnPositionY   = convertFloatToBE(d.spawnPositionY);
    data->enemyData[i] = d;
  }
}

bool enemyIsAlive(Enemy* enemy, u64 timeWaveStarted, u64 currentTick)
{
  return enemy->entity != 0 && enemy->spawnTime <= currentTick - timeWaveStarted;
}

static inline bool isOutOfBounds(Enemy* enemy)
{
  Entity* entity = enemy->entity;
  f32     minX   = entity->x - entity->width * 0.5f;
  f32     maxX   = entity->x + entity->width * 0.5f;

  f32     minY   = entity->y - entity->height * 0.5f;
  f32     maxY   = entity->y + entity->height * 0.5f;

  return minX <= -160.0f || maxX >= 160.0f || minY <= -160.0f || maxY >= 160.0f;
}

bool waveIsOver(Wave* wave, u64 currentTick)
{
  Enemy* enemies = wave->enemies;
  for (u32 i = 0; i < wave->enemyCount; i++)
  {
    if (enemies[i].entity != 0)
    {
      return false;
    }
  }
  return true;
}

void removeOutOfBoundsEntities(Wave* wave, u64 currentTick)
{
  Bullet* bullets = g_bullets;
  for (u32 idx = 0; idx < MAX_BULLET_COUNT; idx++)
  {
    if (bullets[idx].entity != 0 && (bullets[idx].entity->y <= -120.0f || bullets[idx].entity->x >= 120.0f))
    {
      memset(bullets[idx].entity, 0, sizeof(Entity));
      memset(&bullets[idx], 0, sizeof(Bullet));
    }
  }

  Enemy* enemies = wave->enemies;
  for (u32 i = 0; i < wave->enemyCount; i++)
  {
    if (enemies[i].entity != 0 && (isOutOfBounds(&enemies[i]) || enemies[i].hp <= 0))
    {
      printf("Removed dead entity!\n");
      memset(enemies[i].entity, 0, sizeof(Entity));
      enemies[i].entity = 0;
    }
  }
}

void updateWave(Wave* wave, u64 currentTick)
{
  Enemy* enemies = wave->enemies;
  for (int i = 0; i < wave->enemyCount; i++)
  {
    if (enemyIsAlive(&enemies[i], wave->timeWaveStarted, currentTick))
    {
      updateEnemy(&enemies[i], currentTick);
    }
  }
}

void loadWaves()
{
  const char* waveLocation = "./resources/entities/waveData.txt";
  FILE*       file;
  char        line[256];

  file = fopen(waveLocation, "rb");
  fgets(line, sizeof(line), file);
  g_numberOfWaves = atoi(line);
  g_waveData      = (WaveData*)malloc(sizeof(WaveData) * g_numberOfWaves);

  for (u32 i = 0; i < g_numberOfWaves; i++)
  {
    fgets(line, sizeof(line), file);
    line[strlen(line) - 1] = '\0';
    printf("INFO: Reading data from '%s'\n", line);
    loadWaveData(&g_waveData[i], line);
  }

  fclose(file);
}
