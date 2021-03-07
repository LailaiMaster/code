#include <stdio.h>

int main() {
  int a = 0;
  int i = 0;
  while(i < 10) {
    a = a + i;
    i++;
  }
  printf("%d\n", a);
  return 0;
}