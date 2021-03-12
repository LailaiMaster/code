#include "calculator.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

/**清空缓冲区*/
static void ClearBuffer(CalcContext *context) {
    context->input_buffer[0] = 0;//[0] = "\0" 结束标识符
    context->input_buffer_position = 0;
}

/**清空 Context*/
static void ClearAll(CalcContext *context) {
    ClearBuffer(context);
    context->result = 0;
    memset(&context->previous_operation, 0, sizeof(Operation));
    memset(&context->current_operation, 0, sizeof(Operation));
}

static void FormatOutput(CalcContext *context) {
    if (context->result == -0.0) {
        context->result = 0.0;
    }
    sprintf(context->input_buffer, "%.6g", context->result);
}

static void DisplayOutput(CalcContext *context) {
    FormatOutput(context);
    context->display_text(context->input_buffer);
    ClearBuffer(context);
}

/**格式化输入*/
static void FormatInput(CalcContext *context) {
    if (context->input_buffer_position == 0) {//没有输入
        context->input_buffer[0] = '0';
        context->input_buffer[1] = 0;
        context->input_buffer_position = 1;
    } else if (context->input_buffer_position == 1) {//只输入了一个
        if (context->input_buffer[0] == '.') {//如果第一个数是 .，则需要补一个0
            context->input_buffer[0] = '0';
            context->input_buffer[1] = '.';
            context->input_buffer[2] = 0;
            context->input_buffer_position = 2;
        }
    } else if (context->input_buffer_position > 1) {//多余一个输入
        char *p = context->input_buffer;

        if (p[0] == '0' && p[1] != '.') {
            //023 --> 2
            //0123 --> 1
            p[0] = p[1];
            p[1] = 0;
            context->input_buffer_position = 1;
        } else if (p[context->input_buffer_position - 1] == '.') {//最后一个字符是 .，就不能再输入 .，
            for (int i = 0; i < context->input_buffer_position - 1; ++i) {
                if (p[i] == '.') {
                    context->input_buffer_position--;
                    p[context->input_buffer_position] = 0;
                    break;
                }
            }
        }
    }
}

static void DisplayInput(CalcContext *context) {
    FormatInput(context);
    context->display_text(context->input_buffer);
}

/**
 * @param operand 哪个操作数来保存这个输入
 */
static void ApplyInput(CalcContext *context, double *operand) {
    if (context->input_buffer_position > 0) {
        *operand = atof(context->input_buffer);
        ClearBuffer(context);
    } else {
        *operand = context->result;
    }
}

static void ApplyInputToLeft(CalcContext *context) {
    ApplyInput(context, &context->current_operation.left);
}

static void ApplyInputToRight(CalcContext *context) {
    ApplyInput(context, &context->current_operation.right);
}

static void ApplyPreviousOperation(CalcContext *context) {
    context->result = OperationApply(&context->previous_operation);
}

static void ApplyCurrentOperation(CalcContext *context) {
    context->result = OperationApply(&context->current_operation);
    memcpy(&context->previous_operation, &context->current_operation, sizeof(Operation));
    memset(&context->current_operation, 0, sizeof(Operation));
}

/**
 * @return 1 输入合法；0 输入不合法。
 */
int HandleInput(CalcContext *context, char input_value) {
    switch (input_value) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '.': {
            if (context->input_buffer_position < MAX_CHAR) {
                context->input_buffer[context->input_buffer_position++] = input_value;
                context->input_buffer[context->input_buffer_position] = '\0';
            }
            //展示这次输入
            DisplayInput(context);
            break;
        }
        case '+':
            context->current_operation.operator = Plus;
            //提交之前输入到左操作数
            ApplyInputToLeft(context);
            break;
        case '-':
            context->current_operation.operator = Minus;
            //提交之前输入到左操作数
            ApplyInputToLeft(context);
            break;
        case '*':
            context->current_operation.operator = Multiply;
            //提交之前输入到左操作数
            ApplyInputToLeft(context);
            break;
        case '/':
            context->current_operation.operator = Divide;
            //提交之前输入到左操作数
            ApplyInputToLeft(context);
            break;
        case '='://提交并计算
            if (context->current_operation.operator) {
                ApplyInputToRight(context);
                ApplyCurrentOperation(context);
                DisplayOutput(context);
            } else if (context->previous_operation.operator) {
                context->previous_operation.left = context->result;
                ApplyPreviousOperation(context);
                DisplayOutput(context);
            } else {
                printf("No operation.\n");
            }
            break;
        case '%':
            ApplyInputToLeft(context);
            context->current_operation.operator = Multiply;
            context->current_operation.right = 0.01;

            ApplyCurrentOperation(context);
            DisplayOutput(context);
            break;
        case 'S':
            ApplyInputToLeft(context);
            context->current_operation.operator = Multiply;
            context->current_operation.right = -1;

            ApplyCurrentOperation(context);
            DisplayOutput(context);
            break;
        case 'C':
            ClearAll(context);
            DisplayInput(context);
            break;
    }
    return 1;
}

/**创建 Context*/
CalcContext *CreateCalcContext() {
    CalcContext *calc_context = malloc(sizeof(CalcContext));
    ClearAll(calc_context);
    calc_context->display_text = NULL;
    return calc_context;
}

/**销毁 Context*/
void DestroyCalcContext(CalcContext **p_context) {
    free(*p_context);
    *p_context = NULL;
}