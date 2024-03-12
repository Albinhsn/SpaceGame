#ifndef WAVE_H
#define WAVE_H

#include "common.h"
#include "entity.h"

struct WaveEnemyData
{
  i64 spawnTime;
  i32 enemyType;
  f32 spawnPositionX;
  f32 spawnPositionY;
  i32 pathId;
};
typedef struct WaveEnemyData WaveEnemyData;

struct WaveData
{
  WaveEnemyData* enemyData;
  u32            enemyCount;
};
typedef struct WaveData WaveData;

struct Wave
{
  Enemy* enemies;
  u32    enemyCount;
  u32    timeWaveStarted;
};
typedef struct Wave Wave;
extern Wave         waves[16];

bool                enemyIsAlive(Enemy* enemy, u64 timeWaveStarted, u64 currentTick);
void                loadWaves();
void                getWave(Wave* res, u64 idx);
void                updateWave(Wave* wave, u64 currentTick);
bool                waveIsOver(Wave* wave, u64 currentTick);
void               removeOutOfBoundsEntities(Wave* wave, u64 currentTick);

#endif
