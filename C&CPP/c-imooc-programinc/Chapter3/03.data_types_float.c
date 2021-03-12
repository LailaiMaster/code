#include <stdio.h>
#include <limits.h>

int main() {
    //=============== 浮点型 ===============
    /*
     * 怎么理解这个浮：浮点型用科学计数法表示，比如 12345 会用 1.2345e4（表示 1.2345 乘以 10 的四次方），浮点类型中会用一部分位来表示基数，另一部分来表示指数。
     * 精确度随着数值的不同而浮动。
     */
  float a_float = 3.14f; // 6, 7~8 +-10^-37 ~ 10^37
  printf("size of float: %d\n", sizeof(float));
  double a_double = 3.14; // 15~16
  printf("size of double: %d\n", sizeof(double));

    //=============== 浮点型精确度 ===============
  float lat = 39.90815f;
  //检测精确度，与期望的值不同，第一个不精确的位就是其精确度【可以多少几个数】，这里期望的是 0.000001 结果为 0.000008，说明到第 6 位就不精确了。
  printf("%f", 39.908156f - lat);

    //=============== 算钱的功能不能用浮点数 ===============
  float money = 3.14f; // error, never use float or double to describe money
  return 0;
}
