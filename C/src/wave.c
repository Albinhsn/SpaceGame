#include "wave.h"
#include "common.h"
#include "string.h"
#include <stdio.h>

Wave      waves[16];
WaveData* waveData;

void      getWave(Wave* res, u64 idx)
{
  WaveData data        = waveData[idx];
  res->enemyCount      = data.enemyCount;
  res->timeWaveStarted = 0;
  res->enemies         = (Enemy*)malloc(sizeof(Enemy) * res->enemyCount);
  for (i32 i = 0; i < data.enemyCount; i++)
  {
    WaveEnemyData enemyData   = data.enemyData[i];
    EntityData    entityData  = g_entityData[enemyData.enemyType];
    res->enemies[i].spawnTime = enemyData.spawnTime;
    res->enemies[i].lastShot  = 0;
    res->enemies[i].type      = enemyData.enemyType;
    res->enemies[i].entity    = getNewEntity();
    res->enemies[i].hp        = entityData.hp;
    initEntity(res->enemies[i].entity, enemyData.spawnPositionX, enemyData.spawnPositionY, entityData.width, entityData.height, entityData.textureIdx, 180.0f);
  }
}

static void debugWaveEnemyData(WaveEnemyData* data)
{
  // printf("%d\n", data->enemyType);
  printf("%d %ld %f %f %d\n", data->enemyType, data->spawnTime, data->spawnPositionX, data->spawnPositionY, data->pathId);
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

  return enemy->entity != 0 && enemy->spawnTime <= currentTick * 0.1f - timeWaveStarted;
}

void updateWave(Wave* wave, u64 currentTick)
{
  Enemy* enemies = wave->enemies;
  for (int i = 0; i < wave->enemyCount; i++)
  {
    if (enemyIsAlive(&enemies[i], wave->timeWaveStarted, currentTick))
    {
      wave->enemies[i].entity->y -= 0.2f;
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
  i32 numberOfWaves = atoi(line);
  waveData          = (WaveData*)malloc(sizeof(WaveData) * numberOfWaves);

  for (u32 i = 0; i < numberOfWaves; i++)
  {
    fgets(line, sizeof(line), file);
    line[strlen(line) - 1] = '\0';
    printf("INFO: Reading data from '%s'\n", line);
    loadWaveData(&waveData[i], line);
  }

  fclose(file);
}
