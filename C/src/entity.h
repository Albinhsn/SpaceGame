#ifndef ENTITY_H
#define ENTITY_H

#include "bounds.h"
#include "common.h"
#include "files.h"
#include "input.h"
#include "sdl.h"
#include "vector.h"

#define SPEED 10
#define HERO_STATES 7

#define PLAYER_WIDTH 0.05f
#define PLAYER_HEIGHT 0.1f

struct Entity {
  f32 x, y;
  struct Bounds bounds;
  long long lastAttack;
};

static inline void initPlayerEntity(struct Entity *entity) {
  entity->x = SCREENWIDTH / 2.0f;
  entity->y = 0.0f;
  entity->bounds = (struct Bounds){BOUND_WIDTH, BOUND_HEIGHT};
  entity->lastAttack = 0;
}
void updatePlayer(struct Entity *player, struct InputState inputState,
                  struct Entity *entities);
void updatePlayerBounds(struct Entity *player, GLuint vertexArrayId,
                        GLuint vertexBufferId);
#endif
