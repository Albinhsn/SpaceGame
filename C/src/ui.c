#include "ui.h"
#include "common.h"
#include "sdl.h"
#include <stdbool.h>

void renderSlider(struct SliderUIComponent slider) {
  renderUIComponent(slider.background);
  renderUIComponent(slider.slider);
  renderUIComponent(slider.button);
}

void updateSlider(struct SliderUIComponent slider, int xrel, int yrel) {
  // Transform xrel and yrel to floats
  float xDiff = MOUSEX_TO_VIEWSPACE(xrel);
  float yDiff = MOUSEX_TO_VIEWSPACE(yrel);

  // update slider positioning
  float minX = slider.button.xPositions.x + xDiff;
  float maxX = slider.button.yPositions.y + xDiff;

  float minY = slider.button.xPositions.x + yDiff;
  float maxY = slider.button.yPositions.y + yDiff;

  // ToDo Check out of bounds here

  // Update buttonBuffer
  GLfloat bufferData[20] = {
      minX, minY, 0.0f, 0.0f, 1.0f, // Bottom left
      maxX, minY, 0.0f, 1.0f, 1.0f, // Bottom right
      minX, maxY, 0.0f, 0.0f, 0.0f, // Top left
      maxX, maxY, 0.0f, 1.0f, 0.0f  // Top right
  };
  sta_glBindVertexArray(slider.button.vertexArrayId);
  sta_glBindBuffer(GL_ARRAY_BUFFER, slider.button.vertexBufferId);
  sta_glBufferData(GL_ARRAY_BUFFER, 20 * sizeof(GLfloat), bufferData,
                   GL_STATIC_DRAW);
  sta_glBindVertexArray(0);
}

void renderDropdownUIComponent(struct DropdownUIComponent *dropdown,
                               struct Font *font) {
  renderUIComponent(dropdown->background);
  renderUIComponent(dropdown->buttonBackground);
  renderUIComponent(dropdown->button);
  renderTextUIComponent(dropdown->text, font);
  if (dropdown->toggled) {
    for (int i = 0; i < dropdown->itemLength; i++) {
      renderUIComponent(dropdown->items[i].background);
      renderTextUIComponent(dropdown->items[i].text, font);
    }
  }
}

void renderUIComponent(struct UIComponent component) {
  renderQuad(component.vertexArrayId, component.image);
}

void renderTextUIComponent(struct TextUIComponent component,
                           struct Font *font) {
  renderText(font, component.text, component.x, component.y, &component.color,
             component.size);
}
void renderTextButtonUIComponent(struct TextButtonUIComponent component,
                                 struct Font *font) {
  renderUIComponent(component.component);
  renderText(font, component.text.text, component.text.x, component.text.y,
             &component.text.color, component.text.size);
}

void initTextUIComponent(struct TextUIComponent *textComponent, char *text,
                         struct Vec4f32 color, f32 fontSize,
                         struct Vec2f32 position) {

  textComponent->color = color;
  textComponent->text = text;
  textComponent->size = fontSize;

  textComponent->x = position.x;
  textComponent->y = position.y;

  createTextBuffers(&textComponent->vertexArrayId,
                    &textComponent->vertexBufferId);
}
void initUIComponent(struct UIComponent *component, struct Image *image,
                     struct Vec2f32 xPosition, struct Vec2f32 yPosition) {
  createQuadVertexArray(&component->vertexArrayId, &component->vertexBufferId,
                        xPosition, yPosition);

  component->xPositions = xPosition;
  component->yPositions = yPosition;
  component->image = *image;
}

// Should send size
void initTextButtonUIComponent(struct TextButtonUIComponent *textComponent,
                               struct UIComponent *button, struct Image *image,
                               char *text, struct Vec4f32 color,
                               struct Vec2f32 xPosition,
                               struct Vec2f32 yPosition, f32 fontSize) {

  initUIComponent(button, image, xPosition, yPosition);

  textComponent->component = *button;

  struct Vec2f32 x = button->xPositions;
  struct Vec2f32 y = button->yPositions;
  struct Vec2f32 position =
      (struct Vec2f32){((x.x + x.y) / 2.0f), ((y.x + y.y) / 2.0f)};

  initTextUIComponent(&textComponent->text, text, color, fontSize, position);
}

bool buttonDown(struct InputState inputState, struct UIComponent button) {
  if (!inputState.md_1) {
    return false;
  }
  f32 mouseX = MOUSEX_TO_VIEWSPACE(inputState.mouseX);
  f32 mouseY = -(MOUSEY_TO_VIEWSPACE(inputState.mouseY));
  return withinUIComponent(mouseX, mouseY, button);
}

bool buttonClicked(struct InputState inputState, struct UIComponent button) {
  if (!inputState.mr_1) {
    return false;
  }
  f32 mouseX = MOUSEX_TO_VIEWSPACE(inputState.mouseX);
  f32 mouseY = -(MOUSEY_TO_VIEWSPACE(inputState.mouseY));
  return withinUIComponent(mouseX, mouseY, button);
}
