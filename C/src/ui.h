#ifndef UI_H
#define UI_H

#include "common.h"
#include "font.h"
#include "input.h"
#include "vector.h"

enum UIState
{
  UI_EXIT,
  UI_MAIN_MENU,
  UI_SETTINGS_MENU,
  UI_GAME_OVER,
  UI_GAME_RUNNING,
  UI_PAUSE_MENU,
  UI_CONSOLE
};
typedef enum UIState UIState;

struct UIComponent
{
  u32 textureIdx;
  f32 x;
  f32 y;
  f32 width;
  f32 height;
};
typedef struct UIComponent UIComponent;

struct ButtonUIComponent
{
  f32         fontSize;
  f32         spaceSize;
  Color       color;
  UIComponent component;
  const char* text;
};
typedef struct ButtonUIComponent ButtonUIComponent;

struct CheckboxUIComponent
{
  UIComponent checkmark;
  UIComponent background;
  bool        toggled;
};
typedef struct CheckboxUIComponent CheckboxUIComponent;

struct DropdownUIComponent
{
  u32                itemCount;
  ButtonUIComponent  dropdownButton;
  ButtonUIComponent* items;
  void*              dropdownData;
  bool               toggled;
};
typedef struct DropdownUIComponent DropdownUIComponent;

struct SliderUIComponent
{
  UIComponent background;
  UIComponent bar;
  UIComponent slider;
  f32         value;
  f32         minValue;
  f32         maxValue;
};
typedef struct SliderUIComponent SliderUIComponent;

#define CONSOLE_NUMBER_OF_COMMANDS_VISIBLE 6
struct ConsoleUI
{
  UIState     parent;
  UIComponent background;
  UIComponent consoleInput;
  u8          inputLength;
  u8*         input;
  u8**        sentCommands;
};
typedef struct ConsoleUI ConsoleUI;

struct GameOverUI
{
  ButtonUIComponent restartButton;
  ButtonUIComponent mainMenuButton;
  bool              lostGame;
};
typedef struct GameOverUI GameOverUI;

struct MainMenuUI
{
  ButtonUIComponent playButton;
  ButtonUIComponent exitButton;
  ButtonUIComponent settingsButton;
};
typedef struct MainMenuUI MainMenuUI;

struct PauseMenuUI
{
  ButtonUIComponent playButton;
  ButtonUIComponent mainMenuButton;
  ButtonUIComponent settingsButton;
};
typedef struct PauseMenuUI PauseMenuUI;

struct SettingsMenuUI
{
  UIState             parentState;
  ButtonUIComponent   returnButton;
  CheckboxUIComponent vsyncCheckbox;
  DropdownUIComponent screenSizeDropdown;
  SliderUIComponent   audioSlider;
};
typedef struct SettingsMenuUI SettingsMenuUI;

struct UI
{
  UIState         state;
  ConsoleUI*      console;
  GameOverUI*     gameOver;
  MainMenuUI*     mainMenu;
  PauseMenuUI*    pauseMenu;
  SettingsMenuUI* settingsMenu;
};
typedef struct UI UI;

UIState           renderUI(UI* ui, InputState* inputState, u32 score, u8 hp);
void              initUI(UI* ui, ConsoleUI* console, GameOverUI* gameOver, MainMenuUI* mainMenu, PauseMenuUI* pauseMenu, SettingsMenuUI* settings);

#endif
