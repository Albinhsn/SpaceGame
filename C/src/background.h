#ifndef BACKGROUND_H
#define BACKGROUND_H

#include "entity.h"
#include "timer.h"

struct Background
{
  Timer   timer;
  u64     lastUpdate;
  Entity* meteors;
  u64     numberOfMeteors;
};
typedef struct Background Background;

void initBackground(Background * background);
void updateBackground(Background * background);

#endif
