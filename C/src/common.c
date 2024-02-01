#include "common.h"
#include <stdio.h>
#include <string.h>

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
