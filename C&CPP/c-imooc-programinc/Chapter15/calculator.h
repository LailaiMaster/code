#ifndef CHAPTER15__CALCULATOR_H_
#define CHAPTER15__CALCULATOR_H_

#include "operation.h"

// 6 : -1.56805e+015
#define MAX_CHAR 13

/**上下文*/
typedef struct {
    /*存储输入的内容*/
    char input_buffer[MAX_CHAR + 1];
    /*输入到几个位置*/
    int input_buffer_position;
    /*上一次计算的结构*/
    double result;

    /*之前的运算*/
    Operation previous_operation;
    /*当前的计算*/
    Operation current_operation;

    /*函数指针，用于输出一个结果*/
    void (*display_text)(char *);
} CalcContext;

CalcContext *CreateCalcContext();

int HandleInput(CalcContext *context, char input_value);

void DestroyCalcContext(CalcContext **p_context);

#endif //CHAPTER15__CALCULATOR_H_
