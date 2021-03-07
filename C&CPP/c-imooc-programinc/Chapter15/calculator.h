#ifndef CHAPTER15__CALCULATOR_H_
#define CHAPTER15__CALCULATOR_H_

#include "operation.h"

// 6 : -1.56805e+015
#define MAX_CHAR 13

typedef struct {
  char input_buffer[MAX_CHAR + 1];
  int input_buffer_position;
  double result;

  Operation previous_operation;
  Operation current_operation;

  void (*display_text)(char *);
} CalcContext;

int HandleInput(CalcContext *context, char input_value);

CalcContext *CreateCalcContext();

void DestroyCalcContext(CalcContext **p_context);

#endif //CHAPTER15__CALCULATOR_H_
