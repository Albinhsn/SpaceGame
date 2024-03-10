#include "common.h"
#include "input.h"
#include "renderer.h"

i32 main()
{
  Font font;
  initRenderer(&font);

  InputState inputState;
  initInputState(&inputState);

  Entity entity;
  entity.x          = 0;
  entity.y          = 0;
  entity.height     = 10.0f;
  entity.width      = 10.0f;
  entity.rotation   = 0.0f;
  entity.textureIdx = 0;

  while (true)
  {

    if (handleInput(&inputState))
    {
      break;
    }
    initNewFrame();

    renderTexture(&entity);
    renderTextCentered("Hello World!", &RED, 0, 0);

    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
