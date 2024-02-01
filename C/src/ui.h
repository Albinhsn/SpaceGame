#ifndef UI_H
#define UI_H

#include "files.h"
#include "input.h"
#include "sdl.h"
#include "vector.h"

struct UIComponent
{
  struct Image   image;
  struct Vec2f32 xPositions;
  struct Vec2f32 yPositions;
  GLuint         vertexArrayId;
  GLuint         vertexBufferId;
};

struct TextUIComponent
{
  struct Vec4f32 color;
  char*          text;
  f32            y;
  f32            x;
  GLuint         vertexArrayId;
  GLuint         vertexBufferId;
  f32            size;
};

struct TextButtonUIComponent
{
  struct UIComponent     component;
  struct TextUIComponent text;
};

struct CheckboxUIComponent
{
  struct UIComponent uncheckedBox;
  struct UIComponent checkedBox;
  bool               checked;
};

struct SliderUIComponent
{
  struct UIComponent slider;
  struct UIComponent button;
  struct UIComponent background;
};

// This can be optimized a lot
struct DropdownItemUIComponent
{
  struct UIComponent     background;
  struct TextUIComponent text;
};

struct DropdownUIComponent
{
  struct UIComponent              button;
  struct UIComponent              buttonBackground;
  struct UIComponent              background;
  struct TextUIComponent          text;

  struct DropdownItemUIComponent* items;
  i8                              itemLength;
  bool                            toggled;
};

void        renderDropdownUIComponent(struct DropdownUIComponent* dropdown, struct Font* font);
void        renderUIComponent(struct UIComponent component);
void        renderTextButtonUIComponent(struct TextButtonUIComponent component, struct Font* font);
void        renderTextUIComponent(struct TextUIComponent component, struct Font* font);
void initTextUIComponent(struct TextUIComponent* textComponent, char* text, struct Vec4f32 color, f32 fontSize, struct Vec2f32 position);
void        initTextButtonUIComponent(struct TextButtonUIComponent* textComponent, struct UIComponent* button, struct Image* image, char* text, struct Vec4f32 color, struct Vec2f32 xPosition,
                                      struct Vec2f32 yPosition, f32 fontSize);
void        initUIComponent(struct UIComponent* component, struct Image* image, struct Vec2f32 xPosition, struct Vec2f32 yPosition);

inline bool withinUIComponent(f32 x, f32 y, struct UIComponent button)
{
  bool withinX = (button.xPositions.x <= x) && (button.xPositions.y >= x);
  bool withinY = (button.yPositions.x <= y) && (button.yPositions.y >= y);
  return withinX && withinY;
}

bool buttonDown(struct InputState inputState, struct UIComponent button);
bool buttonClicked(const struct InputState inputState, struct UIComponent button);

#endif
