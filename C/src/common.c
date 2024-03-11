#include "common.h"
#include "renderer.h"
#include <SDL2/SDL_video.h>
#include <stdio.h>
#include <string.h>

f32 convertFloatToBE(f32 f)
{
  f32   retVal;
  char* toConvert   = (char*)&f;
  char* returnFloat = (char*)&retVal;

  returnFloat[0]    = toConvert[3];
  returnFloat[1]    = toConvert[2];
  returnFloat[2]    = toConvert[1];
  returnFloat[3]    = toConvert[0];

  return retVal;
}

u64 getScreenWidth()
{
  i32 w, h;
  SDL_GetWindowSize(g_renderer.window, &w, &h);

  return w;
}

u64 getScreenHeight()
{
  i32 w, h;
  SDL_GetWindowSize(g_renderer.window, &w, &h);

  return h;
}

long long timeInMilliseconds(void)
{
  struct timeval tv;

  gettimeofday(&tv, NULL);
  return (((long long)tv.tv_sec) * 1000) + (tv.tv_usec / 1000);
}

void getInfoStrings(char* msString, char* fpsString, long long* lastTick, long long* previousTick)
{
  memset(msString, 0, strlen(msString));
  memset(fpsString, 0, strlen(fpsString));

  char tempString[16];
  memcpy(msString, "ms: ", 4);
  memcpy(fpsString, "fps: ", 5);

  int ms = *lastTick - *previousTick;
  if (ms > 9999)
  {
    ms = 9999;
  }

  *previousTick = *lastTick;
  sprintf(tempString, "%d", ms);
  strcat(msString, tempString);

  float fps = (1.0f / (float)ms) * 1000;
  sprintf(tempString, "%d", (int)fps);
  strcat(fpsString, tempString);
}
