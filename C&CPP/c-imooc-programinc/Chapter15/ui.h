#ifndef CHAPTER15__UI_H_
#define CHAPTER15__UI_H_

#include "calculator.h"

typedef struct {
  void *text_displayer;
  CalcContext *context;
} UiContext;

#endif //CHAPTER15__UI_H_
