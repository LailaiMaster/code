#include <stdio.h>

// x 形式参数
double F(double x) {
  return x * x + x + 1;
}

double G(double x, double y, double z) {
  return x * x + y * y + z * z;
}

int main(void) {
  /*
   * <return type> <name> (<parameters>) {
   *    ... statement
   *    return <return value>;
   * }
   */
  puts("HelloWorld");

  // 2.0: 实际参数
  double result_f = F(2.0);
  double result_g = G(3.0, 4.0, 5.0);

  // Add
  // Sum
  // FindNumber

  printf("result of f: %F\n", result_f);
  printf("result of G: %F\n", result_g);

  return 0;
}