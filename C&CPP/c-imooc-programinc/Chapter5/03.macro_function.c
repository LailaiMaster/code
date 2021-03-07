#include <stdio.h>

#define MAX(a, b) (a) > (b) ? (a) : (b)

#define IS_HEX_CHARACTER(ch) \
((ch) >= '0' && (ch) <= '9') || \
((ch) >= 'A' && (ch) <= 'F') || \
((ch) >= 'a' && (ch) <= 'f')



int Max(int a, int b) {
  return a > b ? a : b;
}

int main() {
  int max = MAX(1.0, 3);
  int max2 = MAX(1, MAX(3, 4));

  int max3 = Max(1, Max(3, 4));

  int max4 = MAX(max++, 5);

  printf("max2: %d\n", max2);

  printf("is A a hex character? %d\n", IS_HEX_CHARACTER('A'));
  return 0;
}
