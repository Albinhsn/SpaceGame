#include "common.h"
#include "entity.h"
#include "files.h"
#include "sdl.h"

#define PLAYER_ENTITY(entities) (entities[0])
#define PLAYER_ANIMATIONS(animations) (animations[0])

#define PLAYER_PROGRAM(programs) (programs[0])
#define TEXT_PROGRAM(programs) (programs[2])
#define PLAYBUTTON_PROGRAM(programs) (programs[3])
#define EXITBUTTON_PROGRAM(programs) (programs[4])

#define PLAYER_VERTEXARRAY(vertexArrays) (vertexArrays[0])
#define PLAYER_BOUNDS_VERTEXARRAY(vertexArrays) (vertexArrays[2])
#define TEXT_VERTEXARRAY(vertexArrays) (vertexArrays[3])
#define PLAYBUTTON_VERTEXARRAY(vertexArrays) (vertexArrays[4])
#define EXITBUTTON_VERTEXARRAY(vertexArrays) (vertexArrays[5])

#define PLAYER_VBO(vertexBuffers) (vertexBuffers[0])
#define PLAYER_BOUNDS_VBO(vertexBuffers) (vertexBuffers[2])
#define TEXT_VBO(vertexBuffers) (vertexBuffers[3])
#define PLAYBUTTON_VBO(vertexBuffers) (vertexBuffers[4])
#define EXITBUTTON_VBO(vertexBuffers) (vertexBuffers[5])

#define TEXT_TEXTURE(textures) (textures[3])
#define PLAYER_TEXTURE(textures) (textures[0])

#define MS_POSX WORLDSPACEX_TO_VIEWSPACE((-SCREENWIDTH)) + (50.0 / 620.0)
#define MS_POSY WORLDSPACEY_TO_VIEWSPACE((SCREENHEIGHT)) - (50.0 / 480.0)

#define FPS_POSX MS_POSX + (10.0 / 620)
#define FPS_POSY MS_POSY - (50.0 / 480.0)

#define RED ((struct Vec4f32){1.0f, 0.0f, 0.0f, 1.0f})
#define YELLOW ((struct Vec4f32){1.0f, 1.0f, 0.0f, 1.0f})
#define GREEN ((struct Vec4f32){0.0f, 1.0f, 0.0f, 1.0f})
#define CYAN ((struct Vec4f32){0.0f, 1.0f, 1.0f, 1.0f})
#define PURPLE ((struct Vec4f32){1.0f, 0.0f, 1.0f, 1.0f})
#define BLUE ((struct Vec4f32){1.0f, 0.0f, 0.0f, 1.0f})

static void initPlayer(struct Entity *entity, GLuint *program,
                       GLuint *vertexArrayId, GLuint *vertexBufferId) {
  struct Vec2f32 xPositions = (struct Vec2f32){-0.05f, 0.05f};
  struct Vec2f32 yPositions = (struct Vec2f32){-0.1f, 0.1f};

  initPlayerEntity(entity);

  createTextureShaderProgram(program);
  createQuadVertexArray(vertexArrayId, vertexBufferId, xPositions, yPositions);
}

static inline void initFontAndText(struct Font *font, GLuint *texture,
                                   GLuint *program, GLuint *vertexArrayId,
                                   GLuint *vertexBufferId) {
  initFont(font, texture);
  createTextShaderProgram(program);
  createTextBuffers(vertexArrayId, vertexBufferId);
}

static void update(struct Entity *entities, struct InputState inputState,
                   GLuint *programs, GLuint *vertexArrays,
                   GLuint *vertexBuffers) {

  updatePlayer(&PLAYER_ENTITY(entities), inputState, entities);
}

static void renderInfo(struct Font *font, long long *currentTick,
                       long long *previousTick, char *msString,
                       char *fpsString) {
  getInfoStrings(msString, fpsString, currentTick, previousTick);
  renderText(font, msString, MS_POSX, MS_POSY, &YELLOW,
             WORLDSPACEY_TO_VIEWSPACE(font->height));
  renderText(font, fpsString, FPS_POSX, FPS_POSY, &YELLOW,
             WORLDSPACEY_TO_VIEWSPACE(font->height));
}

i32 main() {
  SDL_Window *window;
  SDL_GLContext context;

  struct Entity entities[10];
  struct Image background;
  struct Font font;

  GLuint programs[10];
  GLuint vertexArrays[10];

  GLuint vertexBuffers[10];
  GLuint textures[10];

  long long currentTick;
  long long previousTick = 0;

  char fpsString[16];
  char msString[16];

  window = initSDLWindow(&context, SCREENWIDTH, SCREENHEIGHT);

  // THIS SHOULD REMAIN GETTING CALLED LIKE THIS
  initFontAndText(&font, &TEXT_TEXTURE(textures), &TEXT_PROGRAM(programs),
                  &TEXT_VERTEXARRAY(vertexArrays), &TEXT_VBO(vertexBuffers));
  initPlayer(&PLAYER_ENTITY(entities), &PLAYER_PROGRAM(programs),
             &PLAYER_VERTEXARRAY(vertexArrays), &PLAYER_VBO(vertexBuffers));

  struct InputState inputState;
  struct Image playerImage;
  parsePNG(&playerImage, PLAYER_FILE_LOCATION);
  initInputState(&inputState);

  bool running = true;
  while (running) {

    currentTick = timeInMilliseconds();
    // Handle input
    if (handleInput(&inputState)) {
      break;
    }
    initNewFrame();
    update(entities, inputState, programs, vertexArrays, vertexBuffers);
    renderInfo(&font, &currentTick, &previousTick, msString, fpsString);
    renderQuad(PLAYER_VERTEXARRAY(vertexArrays), playerImage);

    SDL_GL_SwapWindow(window);
  }

  SDL_Quit();
  return 0;
}
