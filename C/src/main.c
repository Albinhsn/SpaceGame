#include "common.h"
#include "input.h"
#include "renderer.h"
#include "ui.h"


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

  u32 score = 0;
  u8 hp = 3;

  UI ui;
  ui.state = UI_MAIN_MENU;
  ConsoleUI      console;
  GameOverUI     gameOver;
  MainMenuUI     mainMenu;
  PauseMenuUI    pauseMenu;
  SettingsMenuUI settingsMenu;
  initUI(&ui, &console, &gameOver, &mainMenu, &pauseMenu, &settingsMenu);

  while (ui.state != UI_EXIT)
  {

    if (handleInput(&inputState))
    {
      break;
    }
    initNewFrame();

    renderEntity(&entity);
    renderUI(&ui, &inputState, score, hp);

    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
