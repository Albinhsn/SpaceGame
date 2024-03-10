#include "renderer.h"
#include "common.h"
#include "files.h"
#include "sdl.h"
#include "texture.h"
#include <string.h>

Renderer g_renderer;
Texture  textures[32];
u32      textureCount = 0;

u32      getTextureId(enum TextureModel textureModel)
{
  return textures[textureModel].textureId;
}

static const char* textureLocations[18] = {
    "./resources/images/PNG/Sprites/Ships/spaceShips_001.png",
    "./resources/images/PNG/Sprites/Missiles/spaceMissiles_012.png",
    "./resources/images/PNG/Default/enemy_B.png",
    "./resources/images/PNG/Default/enemy_E.png",
    "./resources/images/PNG/Default/enemy_C.png",
    "./resources/images/PNG/Default/satellite_C.png",
    "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
    "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
    "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
    "./resources/images/PNG/Sprites/Missiles/spaceMissiles_022.png",
    "./resources/images/PNG/Default/meteor_detailedLarge.png",
    "./resources/images/PNG/Default/tile_0044.png",
    "./resources/UI/grey_button05.png",
    "./resources/UI/grey_box.png",
    "./resources/UI/grey_checkmarkGrey.png",
    "./resources/UI/grey_sliderUp.png",
    "./resources/UI/grey_sliderHorizontal.png",
    "./resources/UI/grey_button14.png",
};

void generateTextures()
{
  glActiveTexture(GL_TEXTURE0);
  for (u32 i = 0; i < 18; i++)
  {
    Texture* texture = &textures[i];
    glGenTextures(1, &texture->textureId);
    glBindTexture(GL_TEXTURE_2D, texture->textureId);

    const char* location = textureLocations[i];

    u32         len      = strlen(textureLocations[i]);
    parsePNG(&texture->data, &texture->width, &texture->height, textureLocations[i]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture->width, texture->height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture->data);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);

    sta_glGenerateMipmap(GL_TEXTURE_2D);
  }
}

void createTextureVertexArray()
{
  GLfloat bufferData[20] = {
      -1.0f, -1.0f, 0.0f, 1.0f, //
      1.0f,  -1.0f, 1.0f, 1.0f, //
      -1.0f, 1.0f,  0.0f, 0.0f, //
      1.0f,  1.0f,  1.0f, 0.0f  //
  };
  int    indices[6] = {0, 1, 2, 1, 3, 2};

  GLuint indexBufferId;
  GLuint vertexBufferId;

  sta_glGenVertexArrays(1, &g_renderer.textureVertexId);
  sta_glBindVertexArray(g_renderer.textureVertexId);

  sta_glGenBuffers(1, &vertexBufferId);
  sta_glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
  sta_glBufferData(GL_ARRAY_BUFFER, 16 * sizeof(GLfloat), bufferData, GL_STATIC_DRAW);

  sta_glEnableVertexAttribArray(0);
  sta_glEnableVertexAttribArray(1);

  sta_glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, sizeof(GLfloat) * 4, 0);
  sta_glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, sizeof(GLfloat) * 4, (signed char*)NULL + (2 * sizeof(GLfloat)));

  sta_glGenBuffers(1, &indexBufferId);
  sta_glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
  sta_glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * sizeof(GLuint), indices, GL_STATIC_DRAW);

  sta_glBindVertexArray(0);
}

void createAndCompileShader(GLuint* shaderId, int glShaderMacro, const char* source)
{
  char* buffer;
  int   len;
  readFile(&buffer, &len, source);
  *shaderId = sta_glCreateShader(glShaderMacro);
  sta_glShaderSource(*shaderId, 1, &buffer, NULL);

  sta_glCompileShader(*shaderId);

  free(buffer);
}

void createAndCompileVertexShader(GLuint* shaderId, const char* source)
{
  createAndCompileShader(shaderId, GL_VERTEX_SHADER, source);
}

void createAndCompileFragmentShader(GLuint* shaderId, const char* source)
{
  createAndCompileShader(shaderId, GL_FRAGMENT_SHADER, source);
}

void createTextShaderProgram()
{
  GLuint vShader, fShader;
  createAndCompileVertexShader(&vShader, "shaders/font.vs");
  createAndCompileFragmentShader(&fShader, "shaders/font.ps");

  g_renderer.fontProgramId = sta_glCreateProgram();
  sta_glAttachShader(g_renderer.fontProgramId, vShader);
  sta_glAttachShader(g_renderer.fontProgramId, fShader);

  sta_glBindAttribLocation(g_renderer.fontProgramId, 0, "inputPosition");
  sta_glBindAttribLocation(g_renderer.fontProgramId, 1, "inputTexCoord");

  sta_glLinkProgram(g_renderer.fontProgramId);
}

void createTextureShaderProgram()
{
  GLuint vShader, fShader;
  createAndCompileVertexShader(&vShader, "shaders/texture.vs");
  createAndCompileFragmentShader(&fShader, "shaders/texture.ps");

  g_renderer.textureProgramId = sta_glCreateProgram();
  sta_glAttachShader(g_renderer.textureProgramId, vShader);
  sta_glAttachShader(g_renderer.textureProgramId, fShader);

  sta_glBindAttribLocation(g_renderer.textureProgramId, 0, "inputPosition");
  sta_glBindAttribLocation(g_renderer.textureProgramId, 1, "inputTexCoord");

  sta_glLinkProgram(g_renderer.textureProgramId);
}

void initRenderer()
{
  g_renderer.window = initSDLWindow(&g_renderer.context, DEFAULT_SCREENWIDTH, DEFAULT_SCREENHEIGHT);
  createTextureShaderProgram();
  createTextureVertexArray();

  createTextShaderProgram();
  generateTextures();
}

void renderTexture(Entity* entity)
{
  Texture texture = textures[entity->textureIdx];

  sta_glUseProgram(g_renderer.textureProgramId);
  sta_glBindVertexArray(g_renderer.textureVertexId);

  glBindTexture(GL_TEXTURE_2D, texture.textureId);

  Matrix3x3 transMatrix;
  clearMat3x3(&transMatrix);
  getTransformationMatrix(&transMatrix, entity->x, entity->y, entity->width, entity->height, entity->rotation);

  i32 location = sta_glGetUniformLocation(g_renderer.textureProgramId, "transMatrix");
  if (location == -1)
  {
    printf("failed to set transMatrix\n");
    exit(1);
  }
  sta_glUniformMatrix3fv(location, 1, true, (f32*)&transMatrix);

  glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
  sta_glBindVertexArray(0);
}
