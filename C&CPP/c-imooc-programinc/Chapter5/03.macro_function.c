#include <stdio.h>

/**
 * 定义宏函数：替换时时原封不动的将参数替换到定义中，为了保证正确性，尽量由括号包含一下函数。
 * 宏函数：没有参数类型和返回值类型，本质就是替换，只要替换后的表达式可以运行即可。
 */
#define MAX(a, b) (a) > (b) ? (a) : (b)

/**
 * 定义多行的宏函数，可以使用 "\" 换行。
 */
#define IS_HEX_CHARACTER(ch) \
((ch) >= '0' && (ch) <= '9') || \
((ch) >= 'A' && (ch) <= 'F') || \
((ch) >= 'a' && (ch) <= 'f')

int Max(int a, int b) {
  return a > b ? a : b;
}

//=============== 宏函数 ===============
int main() {
  int max = MAX(1.0, 3);
  int max2 = MAX(1, MAX(3, 4));

  int max3 = Max(1, Max(3, 4));

  //禁用传输带有副作用的表达式到宏函数中。
  //max++ 会影响预期的结果。
  int max4 = MAX(max++, 5);

  printf("max2: %d\n", max2);

  printf("is A a hex character? %d\n", IS_HEX_CHARACTER('A'));
  return 0;
}
