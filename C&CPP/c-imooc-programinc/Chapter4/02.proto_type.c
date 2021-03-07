#include <stdio.h>

void EmptyParamList(void);

/*
 * 1. 函数名
 * 2. 函数返回值类型，如果没写，默认为 int
 * 3. 函数参数列表，参数类型，和参数的顺序，参数形参名不重要
 */
int Add(int, int);

int main(void) {
  puts("");
  EmptyParamList();

  int result = Add(1, 2);
  printf("result of add: %d\n", result);
  return 0;
}

void EmptyParamList(void) {
  puts("Hello");
}