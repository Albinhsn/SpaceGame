#include "common.h"
#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include "ui.h"

Entity                entities[100];
Bullet                bullets[256];
u32                   bulletCount;

static inline Entity* getPlayer()
{
  return &entities[0];
}

static inline void initPlayer(Player* player)
{
  player->entity = getPlayer();
  initEntity(player->entity, 0.0f, 0.0f, 10.0f, 10.0f, TEXTURE_PLAYER_MODEL, 0.0f);
  player->hp       = INITIAL_HP;
  player->lastShot = 0;
}

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

static void gameLoop(InputState* inputState, Player* player, Timer* timer, u64* lastUpdated)
{
  updateTimer(timer);
  if (shouldHandleUpdates(timer, lastUpdated))
  {
    if (updatePlayer(inputState, player, timer))
    {
      createBullet(&bullets[bulletCount], player->entity);
      bulletCount++;
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

  Player player;
  initPlayer(&player);

  while (ui.state != UI_EXIT)
  {

    if (handleInput(&inputState))
    {
      break;
    }
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
      gameLoop(&inputState, &player, &timer, &lastUpdated);
    }

    UIState newState = renderUI(&ui, &inputState, score, hp);
    updateUIState(&ui, newState);

    renderInfoStrings(&prevTick);
    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
