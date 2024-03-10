#ifndef RENDERER_H
#define RENDERER_H

#include "common.h"
#include "entity.h"
#include "sdl.h"

#define TEXT_MAX_LENGTH 32

struct Renderer
{
  SDL_Window*   window;
  SDL_GLContext context;
  GLuint        textureProgramId;
  GLuint        textureVertexId;
  GLuint        fontProgramId;
  GLuint        fontVertexId;
};

enum TextureModel
{
  TEXTURE_PLAYER_MODEL,
  TEXTURE_PLAYER_BULLET,
  TEXTURE_ENEMY_MODEL_1,
  TEXTURE_ENEMY_MODEL_2,
  TEXTURE_ENEMY_MODEL_3,
  TEXTURE_BOSS_MODEL_1,
  TEXTURE_ENEMY_BULLET_1,
  TEXTURE_ENEMY_BULLET_2,
  TEXTURE_ENEMY_BULLET_3,
  TEXTURE_ENEMY_BULLET_4,
  TEXTURE_BACKGROUND_METEOR,
  TEXTURE_HP_HEART,
  TEXTURE_GREY_BUTTON_05,
  TEXTURE_GREY_BOX,
  TEXTURE_GREY_CHECKMARK_GREY,
  TEXTURE_GREY_SLIDER_UP,
  TEXTURE_GREY_SLIDER_HORIZONTAL,
  TEXTURE_GREY_BUTTON_14,
};

typedef struct Renderer Renderer;

extern Renderer         g_renderer;

void                    initRenderer();
void                    renderTexture(Entity* entity);
void                    renderTextCentered();
void                    renderTextStartsAt();
void                    renderTextEndsAt();
u32                     getTextureId(enum TextureModel textureModel);

static inline void      initNewFrame()
{
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
  glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
}

#endif
