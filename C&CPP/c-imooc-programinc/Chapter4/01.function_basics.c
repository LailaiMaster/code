#include <stdio.h>

double F(double x) {
  return x * x + x + 1;
}

double G(double x, double y, double z) {
  return x * x + y * y + z * z;
}

//=============== 认识函数 ===============
int main(void) {
  puts("HelloWorld");

  double result_f = F(2.0);
  double result_g = G(3.0, 4.0, 5.0);

  printf("result of f: %F\n", result_f);
  printf("result of G: %F\n", result_g);

  return 0;
}