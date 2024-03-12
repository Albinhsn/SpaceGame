#include "background.h"
#include "common.h"
#include "renderer.h"

static f32 getRandomMeteorX()
{
  return (f32)(rand() % 200) - 100.0f;
}
static f32 getRandomMeteorY()
{
  return (f32)(rand() % 50 + 100);
}

static inline void resetMeteor(Entity* entity)
{
  entity->x             = getRandomMeteorX();
  entity->y             = getRandomMeteorY();
  entity->width         = getRandomFloat(0.25f, 1.00f);
  entity->height        = entity->width;
  entity->movementSpeed = getRandomFloat(0.2f, 1.65f);
  entity->rotation      = 0.0f;
  entity->textureIdx    = TEXTURE_BACKGROUND_METEOR;
}
static inline void initMeteor(Entity* entity)
{
  entity->x             = getRandomMeteorX();
  entity->y             = getRandomFloat(-100.0f, 100.0f);
  entity->width         = getRandomFloat(0.25f, 1.00f);
  entity->height        = entity->width;
  entity->movementSpeed = getRandomFloat(0.2f, 1.65f);
  entity->rotation      = 0.0f;
  entity->textureIdx    = TEXTURE_BACKGROUND_METEOR;
}

static void initMeteors(Entity** entity_, u64 count)
{
  Entity* entities = *entity_;
  for (u64 i = 0; i < count; i++)
  {
    initMeteor(&entities[i]);
  }
}

static void updateMeteors(Entity** entity_, u64 count)
{
  Entity* entities = *entity_;
  for (u64 i = 0; i < count; i++)
  {
    entities[i].y -= entities[i].movementSpeed;
    if (entities[i].y < -100.0f)
    {
      resetMeteor(&entities[i]);
    }
  }
}

void initBackground(Background* background)
{

  background->numberOfMeteors = getStateVariable("numberOfMeteors");
  background->meteors         = (Entity*)malloc(sizeof(Entity) * background->numberOfMeteors);
  initMeteors(&background->meteors, background->numberOfMeteors);
  background->lastUpdate = 0;

  resetTimer(&background->timer);
  startTimer(&background->timer);
}

void updateBackground(Background* background)
{
  updateTimer(&background->timer);
  if (background->lastUpdate + 16.0f <= background->timer.lastTick)
  {
    u32 numberOfMeteors = getStateVariable("numberOfMeteors");
    if (numberOfMeteors != background->numberOfMeteors)
    {
      free(background->meteors);
      background->numberOfMeteors = numberOfMeteors;
      background->meteors         = (Entity*)malloc(sizeof(Entity) * background->numberOfMeteors);
      initMeteors(&background->meteors, background->numberOfMeteors);
    }

    updateMeteors(&background->meteors, background->numberOfMeteors);
    background->lastUpdate = background->timer.lastTick;
  }
  for (u32 i = 0; i < background->numberOfMeteors; i++)
  {
    renderEntity(&background->meteors[i]);
  }
}
