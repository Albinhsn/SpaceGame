#include "common.h"
#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include "ui.h"

static void updateUIState(UI* ui, UIState newState)
{

  if (ui->state == UI_GAME_RUNNING && newState != UI_GAME_RUNNING)
  {
    // stopTimer
  }

  if (ui->state != UI_SETTINGS_MENU && newState == UI_SETTINGS_MENU)
  {
    ui->settingsMenu->parentState = ui->state;
  }

  if (ui->state != UI_CONSOLE && newState == UI_CONSOLE)
  {
    ui->console->parent = ui->state;
  }

  ui->state = newState;
}

static bool shouldHandleUpdates(Timer* timer, u64* lastUpdated)
{
  u64 lastTick = timer->lastTick;

  if (lastTick >= *lastUpdated + 16)
  {
    *lastUpdated = lastTick;
    return true;
  }
  return false;
}

static void gameLoop(UIState* state, InputState* inputState, Player* player, Timer* timer, u64* lastUpdated)
{
  updateTimer(timer);
  if (shouldHandleUpdates(timer, lastUpdated))
  {
    if (handleInput(inputState))
    {
      *state = UI_EXIT;
    }

    debugInputState(inputState);
    if (updatePlayer(inputState, player, timer))
    {
      Entity* parent = player->entity;
      Bullet* bullet = getNewBullet();
      bullet->entity = getNewEntity();

      f32 y          = parent->y + parent->height;
      initEntity(bullet->entity, parent->x, parent->y, 2.0f, 4.0f, TEXTURE_PLAYER_BULLET, 0.0f);
      bullet->parent = parent;
      bullet->hp     = 1;
    }
  }
  renderEntity(player->entity);
  for (int i = 0; i < bulletCount; i++)
  {
    renderEntity(bullets[i].entity);
  }
}

static void renderInfoStrings(u64* prevTick)
{

  f32  fontSize  = FONT_FONT_SIZE_SMALL;
  f32  spaceSize = FONT_SPACE_SIZE_SMALL;
  f32  x         = -100.0f;
  f32  y         = 60.0f;
  u64  ms        = SDL_GetTicks() - *prevTick;

  char msString[32];
  memset(msString, 0, 32);
  sprintf(msString, "ms:%ld", ms);
  char fpsString[32];
  memset(fpsString, 0, 32);
  sprintf(fpsString, "fps:%d", (u32)MIN(1000.0f / ms, 999));

  renderTextStartsAt(fpsString, &WHITE, x, y - 20.0f);
  renderTextStartsAt(msString, &WHITE, x, y);

  *prevTick = SDL_GetTicks();
}

i32 main()
{
  loadEntityData();
  Timer timer;
  resetTimer(&timer);
  startTimer(&timer);

  Font font;
  initRenderer(&font);

  InputState inputState;
  initInputState(&inputState);

  u32 score = 0;
  u8  hp    = 3;

  UI  ui;
  ui.state = UI_MAIN_MENU;
  ConsoleUI      console;
  GameOverUI     gameOver;
  MainMenuUI     mainMenu;
  PauseMenuUI    pauseMenu;
  SettingsMenuUI settingsMenu;
  initUI(&ui, &console, &gameOver, &mainMenu, &pauseMenu, &settingsMenu);

  u64 lastUpdated = 0;
  u64 prevTick    = 0;
  bulletCount     = 0;
  entityCount     = 1;

  Player player;
  createPlayer(&player);
  debugPlayer(&player);

  while (ui.state != UI_EXIT)
  {

    initNewFrame();

    if (inputState.keyboardStateRelease['c'] && ui.state != UI_CONSOLE)
    {
      ui.console->parent                   = ui.state;
      ui.state                             = UI_CONSOLE;
      inputState.keyboardStateRelease['c'] = false;
    }
    if (ui.state == UI_GAME_RUNNING)
    {
      if (inputState.keyboardStateRelease['p'])
      {
        updateUIState(&ui, UI_PAUSE_MENU);
      }
      gameLoop(&ui.state, &inputState, &player, &timer, &lastUpdated);
    }
    else if (handleInput(&inputState))
    {
      break;
    }

    UIState newState = renderUI(&ui, &inputState, score, hp);
    updateUIState(&ui, newState);

    renderInfoStrings(&prevTick);
    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
