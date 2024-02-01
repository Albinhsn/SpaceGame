#include "entity.h"
#include "sdl.h"

#define GCD 50
#define CAN_ATTACK(player) (player->lastAttack + GCD <= timeInMilliseconds())

static inline bool isMoving(struct InputState state) {
  return state.keyboardStateDown['d'] || state.keyboardStateDown['w'] ||
         state.keyboardStateDown['a'] || state.keyboardStateDown['s'];
}

static bool collisionDetection(struct Entity *player, struct Entity *entities) {
  GLfloat w = player->bounds.width, h = player->bounds.height;
  GLfloat xOffset = WORLDSPACEX_TO_VIEWSPACE(player->x);
  // ToDo remove hardcoded collision detection value
  GLfloat yOffset = player->y * 0.002 - 0.08;

  GLfloat minX = -w + xOffset;
  GLfloat maxX = w + xOffset;

  GLfloat minY = -h + yOffset;
  GLfloat maxY = h + yOffset;

  if (minX < -1.0f || maxX > 1.0f) {
    return true;
  }
  if (minY < -1.0f || maxY > 1.0f) {
    return true;
  }

  return false;
}

void updatePlayerBounds(struct Entity *player, GLuint vertexArrayId,
                        GLuint vertexBufferId) {
  GLfloat w = player->bounds.width, h = player->bounds.height;
  GLfloat xOffset = player->x * 0.002;
  GLfloat yOffset = player->y * 0.002 - 0.08;
  GLfloat bufferData[20] = {
      -w + xOffset, -h + yOffset, 0.0f, 0.0f, 1.0f, // Bottom left
      w + xOffset,  -h + yOffset, 0.0f, 1.0f, 1.0f, // Bottom right
      -w + xOffset, h + yOffset,  0.0f, 0.0f, 0.0f, // Top left
      w + xOffset,  h + yOffset,  0.0f, 1.0f, 0.0f  // Top right
  };
  sta_glBindVertexArray(vertexArrayId);
  sta_glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
  sta_glBufferData(GL_ARRAY_BUFFER, 20 * sizeof(GLfloat), bufferData,
                   GL_STATIC_DRAW);
}

static inline void updateAcceleration(i8 *x, i8 *y,
                                      struct InputState inputState) {
  if (inputState.keyboardStateDown['w']) {
    *y += SPEED;
  }
  if (inputState.keyboardStateDown['s']) {
    *y -= SPEED;
  }

  if (inputState.keyboardStateDown['a']) {
    *x -= SPEED;
  }
  if (inputState.keyboardStateDown['d']) {
    *x += SPEED;
  }
}

void updatePlayer(struct Entity *player, struct InputState inputState,
                  struct Entity *entities) {
  i8 yAcceleration = 0, xAcceleration = 0;
  updateAcceleration(&xAcceleration, &yAcceleration, inputState);

  // ToDo player shooting code

  if (isMoving(inputState)) {
    player->x += xAcceleration;
    player->y += yAcceleration;
    if (collisionDetection(player, entities)) {
      player->x -= xAcceleration;
      player->y -= yAcceleration;
    }
  }

  GLfloat x1, x2;
  x1 = xAcceleration >= 0 ? 0.0f : 1.0f;
  x2 = xAcceleration < 0 ? 0.0f : 1.0f;

  // ToDo hoist this out, this is done every frame regardless
  GLfloat xOffset = WORLDSPACEX_TO_VIEWSPACE(player->x);
  GLfloat yOffset = WORLDSPACEY_TO_VIEWSPACE(player->y);

  GLfloat minX = -PLAYER_WIDTH + xOffset;
  GLfloat maxX = PLAYER_WIDTH + xOffset;

  GLfloat minY = -PLAYER_HEIGHT + yOffset;
  GLfloat maxY = PLAYER_HEIGHT + yOffset;

  GLfloat bufferData[20] = {
      minX, minY, 0.0f, x1, 1.0f, // Bottom left
      maxX, minY, 0.0f, x2, 1.0f, // Bottom right
      minX, maxY, 0.0f, x1, 0.0f, // Top left
      maxX, maxY, 0.0f, x2, 0.0f  // Top right
  };
  sta_glBindVertexArray(PLAYER_VAO_VALUE);
  sta_glBindBuffer(GL_ARRAY_BUFFER, PLAYER_VBO_VALUE);
  sta_glBufferData(GL_ARRAY_BUFFER, 20 * sizeof(GLfloat), bufferData,
                   GL_STATIC_DRAW);
  sta_glBindVertexArray(0);
}
