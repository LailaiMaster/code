#include <stdio.h>
#include <stdbool.h>

//=============== 循环 ===============
int main() {
  /*
   * while (<condition>) {
   *   ... statement
   * }
   */

  /*
   * do {
   *  ... statement
   * } while (<condition>);
   */

//#define ADD '+'
//#define SUB '-'
//#define MULTIPLY '*'
//#define DIVIDE '/'
//#define REM '%'
//
//  int left;
//  int right;
//
//  char operator;
//
//  char command;
//  do {
//
//    printf("Please input an expression: \n");
//    scanf("%d %c %d", &left, &operator, &right);
//
//    int result;
//    switch (operator) {
//      case ADD:result = left + right;
//        break;
//      case SUB:result = left - right;
//        break;
//      case MULTIPLY:result = left * right;
//        break;
//      case DIVIDE:result = left / right;
//        break;
//      case REM:result = left % right;
//        break;
//      default:printf("Unsupported operation: %c\n", operator);
//        return 1;
//    }
//
//    printf("Result: %d\n", result);
//
//    printf("Again? Type 'q' for quit: \n");
//    // puts("Again? Type 'q' for quit: ");
//
//    // scanf("%c", &command);
//    getchar();
//    command = getchar();
//
//    printf("%d\n", command);
//  } while (command != 'q');

  /*
   * for (<initialization>; <condition>; <state>) {
   *  ... statement
   * }
   *
   *  < C99
   *  initialization 不能有变量定义
   */

//  int num = 1;
//  int sum = 0;

//  while (num < 100) {
//    sum += num;
//    num++;
//  }

  // < C99
//  int num;
//  int sum = 0;
//  for (num = 1; num < 100; ++num) {
//    sum += num;
//  }

  // since C99
  int sum = 0;
  for (int num = 1; num < 100; ++num) {
    sum += num;
  }

  printf("sum of [1, 100]:%d\n", sum);

  int sumij = 0;
  for (int i = 0, j = 0; i <= 100 && j <= 100; ++i, ++j) {
    sumij += i * (i + j);
  }
  printf("sumij: %d\n", sumij);

  // continue break
  for (int i = 0; i < 10; ++i) {
    if (i == 2) continue;
    if (i == 8) break;
    printf("i: %d\n", i);
  }

  // goto
  int i = 0;
  begin:
  printf("%d\n", i);
  if (i++ < 5) {
    goto begin;
  }
  return 0;
}
