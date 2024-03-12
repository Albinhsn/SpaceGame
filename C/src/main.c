#include "background.h"
#include "common.h"
#include "entity.h"
#include "input.h"
#include "renderer.h"
#include "timer.h"
#include "ui.h"
#include "wave.h"
#include <stdlib.h>

struct Game
{
  u64    score;
  u64    lastUpdated;
  Timer  timer;
  Player player;
  Wave   wave;
};
typedef struct Game Game;

static void         resetGame(Game* game)
{
  game->score       = 0;
  game->lastUpdated = 0;
  memset(&g_bullets, 0, sizeof(Bullet) * MAX_BULLET_COUNT);
  memset(&g_entities, 0, sizeof(Entity) * MAX_ENTITY_COUNT);
  resetTimer(&game->timer);
  createPlayer(&game->player);
  getWave(&game->wave, 0);
  printf("Restarted game\n");
}

static void updateUIState(UI* ui, UIState newState, Game* game, Timer* timer)
{

  if (ui->state == UI_GAME_RUNNING && newState != UI_GAME_RUNNING)
  {
    stopTimer(timer);
  }

  if (ui->state != UI_SETTINGS_MENU && newState == UI_SETTINGS_MENU)
  {
    ui->settingsMenu->parentState = ui->state;
  }

  if (ui->state == UI_GAME_OVER && newState != UI_GAME_OVER)
  {
    resetGame(game);
  }

  if (ui->state != UI_CONSOLE && newState == UI_CONSOLE)
  {
    ui->console->parent = ui->state;
  }

  if (newState == UI_GAME_RUNNING && !timer->running)
  {
    startTimer(timer);
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
  for (int i = 0; i < MAX_BULLET_COUNT; i++)
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
  for (u32 i = 0; i < MAX_BULLET_COUNT; i++)
  {
    Bullet* bullet = &bullets[i];
    if (bullet->entity != 0)
    {
      if (entitiesCollided(bullet->entity, player->entity) && !bullet->playerBullet)
      {
        bullet->entity = 0;
        bullet->hp     = 0;
        if (getStateVariable("god") != 1)
        {
          player->hp -= 1;
          printf("Hit player!\n");
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

      if (getStateVariable("god") != 1)
      {
        player->hp -= 1;
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
    updateBullets(timer->lastTick);
    handleCollisions(wave, player, timer->lastTick, score);
    if (player->hp <= 0)
    {
      *state = UI_GAME_OVER;
    }
    removeOutOfBoundsBullets(timer->lastTick);
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

  renderTextStartsAt(fpsString, &WHITE, x, y - 20.0f, fontSize, spaceSize);
  renderTextStartsAt(msString, &WHITE, x, y, fontSize, spaceSize);

  *prevTick = SDL_GetTicks();
}

i32 main()
{
  srand(NULL);
  loadEntityData();
  loadWaves();
  loadBulletData();
  loadStateVariables();

  Font font;
  initRenderer(&font);

  InputState inputState;
  initInputState(&inputState);

  UI ui;
  ui.state = UI_MAIN_MENU;
  ConsoleUI      console;
  GameOverUI     gameOver;
  MainMenuUI     mainMenu;
  PauseMenuUI    pauseMenu;
  SettingsMenuUI settingsMenu;
  initUI(&ui, &console, &gameOver, &mainMenu, &pauseMenu, &settingsMenu);

  u64  prevTick = 0;

  Game game;
  memset(&game, 0, sizeof(Game));
  resetGame(&game);

  Background background;
  initBackground(&background);

  while (ui.state != UI_EXIT)
  {

    initNewFrame();

    updateBackground(&background);

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
        updateUIState(&ui, UI_PAUSE_MENU, &game, &game.timer);
      }
      gameLoop(&ui.state, &inputState, &game.wave, &game.player, &game.timer, &game.lastUpdated, &game.score);
    }
    else if (handleInput(&inputState))
    {
      break;
    }

    UIState newState = renderUI(&ui, &inputState, game.score, game.player.hp);
    updateUIState(&ui, newState, &game, &game.timer);

    renderInfoStrings(&prevTick);
    SDL_GL_SwapWindow(g_renderer.window);
  }

  SDL_Quit();
  return 0;
}
