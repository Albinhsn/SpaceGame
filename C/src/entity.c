#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include <stdbool.h>

static bool withinScreen(Entity* player)
{
  i32 x = (i32)(100.0f - player->width / 2.0f);
  i32 y = (i32)(100.0f - player->height / 2.0f);
  return !(player->x <= -x || player->x >= x || player->y <= -y || player->y >= y);
}

bool playerCanShoot(Player* player, Timer* timer)
{
  printf("%d %d\n", player->lastShot, timer->lastTick);
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
  f32 ms = PLAYER_MS;

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

  return inputState->keyboardStateRelease[ASCII_SPACE] && playerCanShoot(player, timer);
}

void initEntity(Entity* entity, f32 x, f32 y, f32 width, f32 height, u32 textureIdx, f32 rotation)
{
  entity->x          = x;
  entity->y          = y;
  entity->width      = width;
  entity->height     = height;
  entity->textureIdx = textureIdx;
  entity->rotation   = rotation;
}

void createBullet(Bullet* bullet, Entity* parent)
{
  f32 y = parent->y + parent->height;
  initEntity(bullet->entity, parent->x, parent->y, 2.0f, 4.0f, TEXTURE_PLAYER_BULLET, 0.0f);
  bullet->parent = parent;
  bullet->hp     = 1;
}
