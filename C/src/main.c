#include "common.h"
#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include "ui.h"
#include "wave.h"
#include <stdlib.h>

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

static void renderGameEntities(Wave* wave, Player* player)
{
  renderEntity(player->entity);
  for (int i = 0; i < g_bulletCount; i++)
  {
    if (g_bullets[i].entity != 0)
    {
      renderEntity(g_bullets[i].entity);
    }
  }
  for (int i = 0; i < wave->enemyCount; i++)
  {
    if (wave->enemies[i].entity != 0)
    {
      renderEntity(wave->enemies[i].entity);
    }
  }
}

static void handleCollisions(Wave* wave, Player* player, u64 currentTick, u64* score)
{
  Bullet* bullets = g_bullets;
  Enemy*  enemies = wave->enemies;
  for (u32 i = 0; i < g_bulletCount; i++)
  {
    Bullet* bullet = &bullets[i];
    if (bullet->entity != 0)
    {
      if (entitiesCollided(bullet->entity, player->entity) && !bullet->playerBullet)
      {
        player->hp -= 1;
        bullet->entity = 0;
        bullet->hp     = 0;
        printf("Hit player!\n");
        if (player->hp <= 0)
        {
          printf("YOU DIED\n");
          exit(1);
        }
      }

      for (u32 j = 0; j < wave->enemyCount; j++)
      {
        if (bullet->playerBullet && enemyIsAlive(&enemies[j], wave->timeWaveStarted, currentTick) && entitiesCollided(bullet->entity, enemies[j].entity))
        {
          bullet->entity = 0;
          bullet->hp     = 0;
          enemies[j].hp -= 1;
          if (enemies[j].hp <= 0)
          {
            *score += enemies[j].scoreGiven;
            enemies[j].entity = 0;
          }
          printf("Hit Enemy! %d\n", enemies[j].hp);
          break;
        }
      }
    }
  }

  for (u32 i = 0; i < wave->enemyCount; i++)
  {
    if (enemyIsAlive(&enemies[i], wave->timeWaveStarted, currentTick) && entitiesCollided(player->entity, enemies[i].entity))
    {
      enemies[i].hp -= 1;
      if (enemies[i].hp <= 0)
      {
        *score += enemies[i].scoreGiven;
        enemies[i].entity = 0;
      }

      player->hp -= 1;
      if (player->hp <= 0)
      {
        printf("YOU DIED\n");
        exit(1);
      }
    }
  }
}

static void gameLoop(UIState* state, InputState* inputState, Wave* wave, Player* player, Timer* timer, u64* lastUpdated, u64* score)
{
  updateTimer(timer);
  if (shouldHandleUpdates(timer, lastUpdated))
  {
    if (handleInput(inputState))
    {
      *state = UI_EXIT;
    }

    if (updatePlayer(inputState, player, timer))
    {
      createNewBullet(player->entity, 4);
    }
    updateWave(wave, timer->lastTick);
    updateBullets();
    handleCollisions(wave, player, timer->lastTick, score);
  }

  renderGameEntities(wave, player);
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
  srand(NULL);
  loadEntityData();
  loadWaves();
  loadBulletData();
  Timer timer;
  resetTimer(&timer);
  startTimer(&timer);

  Font font;
  initRenderer(&font);

  InputState inputState;
  initInputState(&inputState);

  u64 score = 0;

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
  g_bulletCount   = 0;
  g_entityCount   = 1;

  Player player;
  createPlayer(&player);

  Wave wave;
  getWave(&wave, 0);

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
      gameLoop(&ui.state, &inputState, &wave, &player, &timer, &lastUpdated, &score);
    }
    else if (handleInput(&inputState))
    {
      break;
    }

    UIState newState = renderUI(&ui, &inputState, score, player.hp);
    updateUIState(&ui, newState);

    renderInfoStrings(&prevTick);
    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
