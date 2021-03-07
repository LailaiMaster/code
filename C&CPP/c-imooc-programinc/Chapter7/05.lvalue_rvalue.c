#include <stdio.h>
#include <io_utils.h>

int main() {
  int a;
  a = 2;

  int *p = &a;
  *p = 2;

  int b = *p;

  // &a = p; // ERROR!
  // *p + 1 = 3; // ERROR!
  int array[4] = {0};
  int *pa = array;
  *pa = 2;
  *(pa++) = 3;
  *(pa + 2) = 4;
  PRINT_INT_ARRAY(array, 4);

  // left: storage
  // right: value
  return 0;
}
