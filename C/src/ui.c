#include "ui.h"
#include "common.h"
#include "renderer.h"
#include <SDL2/SDL_video.h>
#include <string.h>

static void initUIComponent(UIComponent* component, f32 x, f32 y, f32 width, f32 height, u32 textureIdx)
{
  component->textureIdx = textureIdx;
  component->x          = x;
  component->y          = y;
  component->width      = width;
  component->height     = height;
}

static void initSlider(SliderUIComponent* slider, f32 initialValue, f32 minValue, f32 maxValue, f32 x, f32 y, f32 width, f32 height)
{
  slider->minValue = minValue;
  slider->maxValue = maxValue;
  slider->value    = initialValue;
  initUIComponent(&slider->background, x, y, width, height, TEXTURE_GREY_BOX);
  initUIComponent(&slider->bar, x, y, width * 0.9f, height, TEXTURE_GREY_SLIDER_HORIZONTAL);
  initUIComponent(&slider->slider, x, y, width * 0.1f, height * 0.9f, TEXTURE_GREY_SLIDER_UP);
}

static void initCheckbox(CheckboxUIComponent* checkbox, f32 x, f32 y, f32 width, f32 height)
{
  checkbox->toggled = false;
  initUIComponent(&checkbox->background, x, y, width, height, TEXTURE_GREY_BOX);
  initUIComponent(&checkbox->checkmark, x, y, width, height, TEXTURE_GREY_CHECKMARK_GREY);
}

static void initButton(ButtonUIComponent* button, Color color, const char* text, f32 fontSize, f32 spaceSize, f32 x, f32 y, f32 width, f32 height, u32 textureIdx)
{
  button->component.x          = x;
  button->component.y          = y;
  button->component.width      = width;
  button->component.height     = height;
  button->component.textureIdx = textureIdx;
  button->color                = color;
  button->text                 = text;
  button->fontSize             = fontSize;
  button->spaceSize            = spaceSize;
}

static void initDropdown(DropdownUIComponent* slider, u32 itemCount, const char** itemText, void* dropdownData, Color color, const char* text, f32 fontSize, f32 spaceSize, f32 x, f32 y, f32 width,
                         f32 height)
{
  slider->toggled      = false;
  slider->itemCount    = itemCount;
  slider->dropdownData = dropdownData;

  initButton(&slider->dropdownButton, color, text, fontSize, spaceSize, x, y, width, height, TEXTURE_GREY_BOX);

  slider->items = (ButtonUIComponent*)malloc(sizeof(ButtonUIComponent) * itemCount);
  for (u32 i = 0; i < itemCount; i++)
  {
    y -= 2.0f * height;
    initButton(&slider->items[i], color, itemText[i], fontSize, spaceSize, x, y, width, height, TEXTURE_GREY_BOX);
  }
}

static bool hovers(UIComponent component, InputState* inputState)
{
  f32 mouseX = ((inputState->mouseX / (f32)getScreenWidth()) * 2.0f - 1.0f) * 100.0f;
  f32 mouseY = ((inputState->mouseY / (f32)getScreenHeight()) * 2.0f - 1.0f) * -100.0f;

  f32 minX   = component.x - component.width;
  f32 maxX   = component.x + component.width;
  f32 minY   = component.y - component.height;
  f32 maxY   = component.y + component.height;

  return minX <= mouseX && mouseX <= maxX && minY <= mouseY && mouseY <= maxY;
}

static bool componentIsReleased(UIComponent component, InputState* inputState)
{
  if (!inputState->mr_1)
  {
    return false;
  }
  return hovers(component, inputState);
}

static bool componentIsPressed(UIComponent component, InputState* inputState)
{
  if (!inputState->md_1)
  {
    return false;
  }
  return hovers(component, inputState);
}

static UIState renderMainMenu(MainMenuUI* mainMenu, InputState* inputState)
{
  renderButton(&mainMenu->playButton);
  if (componentIsReleased(mainMenu->playButton.component, inputState))
  {
    return UI_GAME_RUNNING;
  }
  renderButton(&mainMenu->settingsButton);
  if (componentIsReleased(mainMenu->settingsButton.component, inputState))
  {
    return UI_SETTINGS_MENU;
  }
  renderButton(&mainMenu->exitButton);
  if (componentIsReleased(mainMenu->exitButton.component, inputState))
  {
    return UI_EXIT;
  }
  return UI_MAIN_MENU;
}

struct ScreenSizePair
{
  i32 w;
  i32 h;
};
typedef struct ScreenSizePair ScreenSizePair;

static void                   updateWindowSize(i32 width, i32 height)
{
  SDL_SetWindowSize(g_renderer.window, width, height);
  glViewport(0, 0, width, height);
}

static void updateSliderPosition(SliderUIComponent* slider, InputState* inputState)
{
  slider->slider.x = ((inputState->mouseX / (f32)getScreenWidth()) * 2.0f - 1.0f) * 100.0f;

  f32 offset       = 10.0f;
  slider->slider.x = MAX(slider->slider.x, (slider->background.x - slider->background.width + offset));
  slider->slider.x = MIN(slider->slider.x, (slider->background.x + slider->background.width - offset));
}
static void updateSliderValue(SliderUIComponent* slider)
{
  f32 minSliderX = slider->background.x - slider->background.width + 10.0f + slider->slider.width;
  f32 maxSliderX = slider->background.x - slider->background.width + 10.0f + slider->slider.width;

  slider->value  = slider->value + (slider->maxValue - slider->minValue) * (slider->slider.x - minSliderX) / (maxSliderX - minSliderX);
}

static UIState renderSettingsMenu(SettingsMenuUI* settingsMenu, InputState* inputState)
{

  // RETURN
  if (componentIsReleased(settingsMenu->returnButton.component, inputState))
  {
    return settingsMenu->parentState;
  }
  renderButton(&settingsMenu->returnButton);

  // CHECKBOX
  if (componentIsReleased(settingsMenu->vsyncCheckbox.background, inputState))
  {
    settingsMenu->vsyncCheckbox.toggled = !settingsMenu->vsyncCheckbox.toggled;
    SDL_GL_SetSwapInterval(settingsMenu->vsyncCheckbox.toggled);
  }
  f32 endX = settingsMenu->vsyncCheckbox.background.x - settingsMenu->vsyncCheckbox.background.width;
  renderTextEndsAt("vsync", &WHITE, endX, 0);
  renderCheckbox(&settingsMenu->vsyncCheckbox);

  // SCREEN SIZE DROPDOWN
  DropdownUIComponent* dropdown = &settingsMenu->screenSizeDropdown;
  if (componentIsReleased(dropdown->dropdownButton.component, inputState))
  {
    dropdown->toggled = !dropdown->toggled;
  }
  renderDropdown(dropdown);
  if (dropdown->toggled)
  {
    ScreenSizePair* data = (ScreenSizePair*)dropdown->dropdownData;
    for (int i = 0; i < dropdown->itemCount; i++)
    {
      if (componentIsReleased(dropdown->items[i].component, inputState))
      {
        updateWindowSize(data[i].w, data[i].h);
        dropdown->toggled = false;
        break;
      }
    }
  }

  // AUDIO SLIDER
  SliderUIComponent* slider = &settingsMenu->audioSlider;
  if (componentIsPressed(slider->slider, inputState))
  {
    updateSliderPosition(slider, inputState);
    updateSliderValue(slider);
  }
  renderSlider(slider);
  return UI_SETTINGS_MENU;
}

static UIState renderGameOver(GameOverUI* gameOver, InputState* inputState, u32 score)
{
  f32 fontSize = FONT_FONT_SIZE_LARGE;
  renderTextCentered(gameOver->lostGame ? "GAME OVER" : "GAME WON", &WHITE, 0, 90.0f - FONT_FONT_SIZE_LARGE);

  char scoreText[32];
  memset(scoreText, 0, 32);
  sprintf(scoreText, "Score: %d", score);
  renderTextCentered(scoreText, &WHITE, 0, 90.0f - 3 * fontSize);

  renderButton(&gameOver->restartButton);
  if (componentIsReleased(gameOver->restartButton.component, inputState))
  {
    return UI_GAME_RUNNING;
  }

  renderButton(&gameOver->mainMenuButton);
  if (componentIsReleased(gameOver->mainMenuButton.component, inputState))
  {
    return UI_MAIN_MENU;
  }
  return UI_GAME_OVER;
}

static UIState renderGameRunning(InputState * inputState, u32 score, u8 hp)
{
  f32  fontSize = FONT_FONT_SIZE_MEDIUM;
  char scoreText[32];
  memset(scoreText, 0, 32);
  sprintf(scoreText, "Score: %d", score);
  renderTextStartsAt(scoreText, &WHITE, -100.0f, 100.0f - fontSize);
  renderHealth(hp);

  if(inputState->keyboardStateRelease[ASCII_ESCAPE]){
    return UI_PAUSE_MENU;
  }


  return UI_GAME_RUNNING;
}

static UIState renderPauseMenu(PauseMenuUI* pauseMenu, InputState* inputState)
{
  renderButton(&pauseMenu->playButton);
  if (componentIsPressed(pauseMenu->playButton.component, inputState))
  {
    return UI_GAME_RUNNING;
  }

  renderButton(&pauseMenu->mainMenuButton);
  if (componentIsPressed(pauseMenu->mainMenuButton.component, inputState))
  {
    return UI_MAIN_MENU;
  }

  renderButton(&pauseMenu->settingsButton);
  if (componentIsPressed(pauseMenu->settingsButton.component, inputState))
  {
    return UI_SETTINGS_MENU;
  }

  return UI_PAUSE_MENU;
}

static UIState handleInputConsole(ConsoleUI* console, InputState* inputState)
{
  if (inputState->keyboardStateRelease[ASCII_ESCAPE])
  {
    return console->parent;
  }
  for (i32 i = 0; i < 127; i++)
  {
    if (inputState->keyboardStateRelease[i] && isalpha(i))
    {
      console->input[console->inputLength] = (char)i;
      console->inputLength++;
    }
  }

  if (inputState->keyboardStateRelease[ASCII_BACKSPACE] && console->inputLength > 0)
  {
    console->inputLength--;
    console->input[console->inputLength] = 0;
  }

  if (inputState->keyboardStateRelease[ASCII_RETURN])
  {

    if (strncmp((char*)console->input, "exit", 4) == 0)
    {
      return UI_EXIT;
    }

    for (i32 i = CONSOLE_NUMBER_OF_COMMANDS_VISIBLE - 2; i >= 0; i--)
    {
      memcpy(console->sentCommands[i + 1], console->sentCommands[i], 32);
    }
    memcpy(console->sentCommands[0], console->input, 32);
    memset(console->input, 0, 32);
    console->inputLength = 0;
  }

  return UI_CONSOLE;
}

static void renderSentCommandsConsole(ConsoleUI* console)
{
  f32 fontSize = FONT_FONT_SIZE_MEDIUM;
  f32 x        = console->consoleInput.x - console->consoleInput.width;
  f32 y        = console->consoleInput.y;

  renderTextStartsAt((const char*)console->input, &BLACK, x, y);
  for (u32 i = 0; i < CONSOLE_NUMBER_OF_COMMANDS_VISIBLE; i++)
  {
    y += 15.0f;
    renderTextStartsAt((const char*)console->sentCommands[i], &RED, x, y);
  }
}

static UIState renderConsole(ConsoleUI* console, InputState* inputState)
{
  renderComponent(&console->background);
  renderComponent(&console->consoleInput);
  renderSentCommandsConsole(console);

  UIState state = handleInputConsole(console, inputState);
  if (state != UI_CONSOLE)
  {
    for (i32 i = 0; i < CONSOLE_NUMBER_OF_COMMANDS_VISIBLE; i++)
    {
      memset(console->sentCommands[i], 0, 32);
    }
    memset(console->input, 0, 32);
    console->inputLength = 0;
  }

  return state;
}

UIState renderUI(UI* ui, InputState* inputState, u32 score, u8 hp)
{

  switch (ui->state)
  {
  case UI_MAIN_MENU:
  {
    return renderMainMenu(ui->mainMenu, inputState);
  }
  case UI_SETTINGS_MENU:
  {
    return renderSettingsMenu(ui->settingsMenu, inputState);
  }
  case UI_GAME_OVER:
  {
    return renderGameOver(ui->gameOver, inputState, score);
  }
  case UI_GAME_RUNNING:
  {
    return renderGameRunning(inputState, score, hp);
  }
  case UI_PAUSE_MENU:
  {
    return renderPauseMenu(ui->pauseMenu, inputState);
  }
  case UI_CONSOLE:
  {
    return renderConsole(ui->console, inputState);
  }
  default:
  {
  }
  }

  return ui->state;
}

void initConsoleUI(ConsoleUI* console)
{
  console->inputLength = 0;
  console->input       = (u8*)malloc(sizeof(u8) * TEXT_MAX_LENGTH);
  memset(console->input, 0, TEXT_MAX_LENGTH);
  console->sentCommands = (u8**)malloc(sizeof(u8*) * CONSOLE_NUMBER_OF_COMMANDS_VISIBLE);
  for (u32 i = 0; i < CONSOLE_NUMBER_OF_COMMANDS_VISIBLE; i++)
  {
    console->sentCommands[i] = (u8*)malloc(sizeof(u8) * TEXT_MAX_LENGTH);
    memset(console->sentCommands[i], 0, TEXT_MAX_LENGTH);
  }
  console->parent                  = UI_CONSOLE;

  console->background.x            = 0;
  console->background.y            = 0;
  console->background.width        = 50.0f;
  console->background.height       = 50.0f;
  console->background.textureIdx   = TEXTURE_GREY_BOX;

  f32 fontSize                     = 6.0f;

  console->consoleInput.textureIdx = TEXTURE_GREY_BUTTON_14;
  console->consoleInput.x          = 0.0f;
  console->consoleInput.y          = -42.0f;
  console->consoleInput.width      = 50.0f;
  console->consoleInput.height     = fontSize * 1.2f;
}

void initGameOverUI(GameOverUI* gameOver)
{
  f32 buttonWidth  = BUTTON_SIZE_MEDIUM_WIDTH;
  f32 buttonHeight = BUTTON_SIZE_MEDIUM_HEIGHT;
  f32 fontSize     = FONT_FONT_SIZE_MEDIUM;
  f32 spaceSize    = FONT_SPACE_SIZE_MEDIUM;

  initButton(&gameOver->restartButton, RED, "RESTART", spaceSize, fontSize, 0.0f, 0.0f, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
  initButton(&gameOver->mainMenuButton, RED, "MAIN MENU", spaceSize, fontSize, 0.0f, -2 * buttonHeight, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
  gameOver->lostGame = false;
}

void initMainMenuUI(MainMenuUI* mainMenu)
{
  mainMenu->playButton.text                     = "PLAY";
  mainMenu->playButton.color                    = RED;
  mainMenu->playButton.fontSize                 = 6.0f;
  mainMenu->playButton.spaceSize                = 10.0f;

  mainMenu->playButton.component.x              = 0.0f;
  mainMenu->playButton.component.y              = 31.0f;
  mainMenu->playButton.component.width          = 40.0f;
  mainMenu->playButton.component.height         = 10.0f;
  mainMenu->playButton.component.textureIdx     = TEXTURE_GREY_BOX;

  mainMenu->settingsButton.text                 = "SETTINGS";
  mainMenu->settingsButton.color                = RED;
  mainMenu->settingsButton.fontSize             = 6.0f;
  mainMenu->settingsButton.spaceSize            = 10.0f;

  mainMenu->settingsButton.component.x          = 0.0f;
  mainMenu->settingsButton.component.y          = 0.0f;
  mainMenu->settingsButton.component.width      = 40.0f;
  mainMenu->settingsButton.component.height     = 10.0f;
  mainMenu->settingsButton.component.textureIdx = TEXTURE_GREY_BOX;

  mainMenu->exitButton.text                     = "EXIT";
  mainMenu->exitButton.color                    = RED;
  mainMenu->exitButton.fontSize                 = 6.0f;
  mainMenu->exitButton.spaceSize                = 10.0f;

  mainMenu->exitButton.component.x              = 0.0f;
  mainMenu->exitButton.component.y              = -31.0f;
  mainMenu->exitButton.component.width          = 40.0f;
  mainMenu->exitButton.component.height         = 10.0f;
  mainMenu->exitButton.component.textureIdx     = TEXTURE_GREY_BOX;
}

void initPauseMenuUI(PauseMenuUI* menu)
{
  f32 buttonWidth  = BUTTON_SIZE_MEDIUM_WIDTH;
  f32 buttonHeight = BUTTON_SIZE_MEDIUM_HEIGHT;
  f32 fontSize     = FONT_FONT_SIZE_MEDIUM;
  f32 spaceSize    = FONT_SPACE_SIZE_MEDIUM;

  initButton(&menu->playButton, RED, "PLAY", spaceSize, fontSize, 0.0f, 2 * buttonHeight, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
  initButton(&menu->settingsButton, RED, "SETTINGS", spaceSize, fontSize, 0.0f, 0.0f, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
  initButton(&menu->mainMenuButton, RED, "MAIN MENU", spaceSize, fontSize, 0.0f, -2 * buttonHeight, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
}

void initSettingsUI(SettingsMenuUI* settings)
{
  f32 buttonWidth          = BUTTON_SIZE_LARGE_WIDTH;
  f32 buttonHeight         = BUTTON_SIZE_LARGE_HEIGHT;
  f32 fontSize             = FONT_FONT_SIZE_MEDIUM;
  f32 spaceSize            = FONT_SPACE_SIZE_MEDIUM;
  f32 dropdownButtonWidth  = BUTTON_SIZE_MEDIUM_WIDTH;
  f32 dropdownButtonHeight = BUTTON_SIZE_MEDIUM_HEIGHT;

  initButton(&settings->returnButton, RED, "RETURN", fontSize, spaceSize, 0.0f, -40.0f, buttonWidth, buttonHeight, TEXTURE_GREY_BOX);
  initSlider(&settings->audioSlider, 50.0f, 0.0f, 100.0f, 0.0f, 60.0f, 65.0f, 8.0f);

  u32             itemCount   = 4;
  ScreenSizePair* pairs       = (ScreenSizePair*)malloc(sizeof(ScreenSizePair) * itemCount);
  pairs[0]                    = (ScreenSizePair){.w = 1920, .h = 1080};
  pairs[1]                    = (ScreenSizePair){.w = 1600, .h = 900};
  pairs[2]                    = (ScreenSizePair){.w = 1024, .h = 768};
  pairs[3]                    = (ScreenSizePair){.w = 620, .h = 480};
  const char* dropdownText[4] = {
      "1920x1080",
      "1600x900",
      "1024x768",
      "620x480",
  };

  initDropdown(&settings->screenSizeDropdown, itemCount, dropdownText, (void*)pairs, RED, "Resolution", fontSize, spaceSize, 60.0f, 20.0f, dropdownButtonWidth, dropdownButtonHeight);
  initCheckbox(&settings->vsyncCheckbox, 0.0f, 0.0f, 6.0f, 8.0f);

  settings->parentState = UI_MAIN_MENU;
}

void initUI(UI* ui, ConsoleUI* console, GameOverUI* gameOver, MainMenuUI* mainMenu, PauseMenuUI* pauseMenu, SettingsMenuUI* settings)
{
  initConsoleUI(console);
  ui->console = console;

  initGameOverUI(gameOver);
  ui->gameOver = gameOver;

  initMainMenuUI(mainMenu);
  ui->mainMenu = mainMenu;

  initPauseMenuUI(pauseMenu);
  ui->pauseMenu = pauseMenu;

  initSettingsUI(settings);
  ui->settingsMenu = settings;
}
